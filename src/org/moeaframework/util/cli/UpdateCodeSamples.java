/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util.cli;

import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartPanel;
import org.moeaframework.analysis.plot.ImageUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.validate.Validate;

/**
 * Utility to update code samples and output found in various documents, including Markdown and HTML files.
 * The aim is to inject real Java examples into the documentation so that all code snippets can be tested and
 * validated.
 * <p>
 * This works by embedding a special comment in one of the supported template file types.  The comment follows the
 * format:
 * <pre>{@code
 *   <!-- :<processor>: option1 option2 ... -->
 * }</pre>
 * The following processors are available:
 * <ul>
 *   <li>{@code code} - Copies all or part of a source code file into a code block in the template
 *   <li>{@code exec} - Compiles and executes a Java file, copying all or part of the output to a code block
 *   <li>{@code plot} - Compiles and executes a Java file, then snapshots the resulting plot to an image file
 * </ul>
 * Options can be provided to each processor to configure its behavior.  Options can be defined in one of these
 * formats:
 * <pre>{@code
 *   key=value             // Key-value pair
 *   key="quoted value"    // Quoted value
 *   key                   // Equivalent to key=true
 * }</pre>
 * This utility can be run in validate-only mode or update mode.  In validate mode, any changes to the files will
 * result in an error.  This is useful in CI to validate the docs are up-to-date.  In update mode, the files are
 * updated with any changes.
 */
public class UpdateCodeSamples extends CommandLineUtility {
	
	private static final long DEFAULT_SEED = 123456;
	
	private static final String DEFAULT_LINE_SEPARATOR = "\n";
	
	private static final File[] DEFAULT_PATHS = new File[] { new File("docs/"), new File("website/"), new File("src/README.md.template") };
	
	private static final Pattern REGEX = Pattern.compile("<!--\\s+\\:([a-zA-Z]+)\\:\\s+(.*)\\s+-->");
		
	private static final Pattern LINES_REGEX = Pattern.compile("(\\-?[0-9]+)?([:\\\\-])?(\\-?[0-9]+)?");
	
	private static final Pattern BEGIN_EXAMPLE_REGEX = Pattern.compile("^\\s+(//|#)\\s*begin-example:\\s*([a-zA-Z][a-zA-Z0-9\\-_]*)\\s*$");

	private static final Pattern END_EXAMPLE_REGEX = Pattern.compile("^\\s+(//|#)\\s*end-example:\\s*([a-zA-Z][a-zA-Z0-9\\-_]*)\\s*$");
	
	/**
	 * {@code true} if compiled files should be cleaned and rebuilt.
	 */
	private boolean clean;
	
	/**
	 * {@code true} if running in update mode; {@code false} for validate mode.
	 */
	private boolean update;
	
	/**
	 * The seed for making results consistent between runs.
	 */
	private long seed;
	
	/**
	 * Creates a new instance of the command line utility to update code examples.
	 */
	public UpdateCodeSamples() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("c")
				.longOpt("clean")
				.build());
		options.addOption(Option.builder("u")
				.longOpt("update")
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.build());

		return options;
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		clean = commandLine.hasOption("clean");
		update = commandLine.hasOption("update");
		seed = commandLine.hasOption("seed") ? Long.parseLong(commandLine.getOptionValue("seed")) : DEFAULT_SEED;
						
		System.out.println("Running in " + (update ? "update" : "validate") + " mode");
		System.out.println("Using seed " + seed);
		
		Settings.PROPERTIES.setInt(Settings.KEY_HELP_WIDTH, 120);
		
		boolean fileChanged = false;

		if (commandLine.getArgs().length == 0) {
			for (File path : DEFAULT_PATHS) {
				fileChanged |= scan(path);
			}
		} else {
			for (String arg : commandLine.getArgs()) {
				fileChanged |= scan(new File(arg));
			}
		}
		
		if (fileChanged) {
			if (update) {
				System.out.println("Updated code samples, please validate and commit the changes!");
			} else {
				throw new FrameworkException("Detected changes to code samples!");
			}
		}
	}
	
	/**
	 * Recursively processes all files and directories.
	 * 
	 * @param file the file or directory to process
	 * @return {@code true} if any files were modified
	 * @throws InterruptedException if the process was interrupted
	 * @throws IOException if an I/O error occurred while processing a file
	 */
	private boolean scan(File file) throws IOException, InterruptedException {
		boolean fileChanged = false;
		
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println("Scanning directory " + file);
				for (File nestedFile : file.listFiles()) {
					fileChanged |= scan(nestedFile);
				}
			} else {
				fileChanged |= process(file);
			}
		} else {
			System.out.println("Skipping " + file + ", does not exist");
		}
		
		return fileChanged;
	}
	
	/**
	 * Determines the line separator in use by the source file, avoiding unnecessary diffs in generated files.
	 * 
	 * @param file the file
	 * @return the line separator
	 * @throws IOException if an I/O error occurred while reading the file
	 */
	private String determineLineSeparator(File file) throws IOException {
		String content = Files.readString(file.toPath());

		if (content.matches("(?s).*(\\r\\n).*")) {
			return "\r\n";
		} else if (content.matches("(?s).*(\\n).*")) {
			return "\n";
		} else if (content.matches("(?s).*(\\r).*")) {
			return "\r";
		} else {
			return DEFAULT_LINE_SEPARATOR;
		}
	}
	
	/**
	 * Processes a single file, validating or updating code samples.  The file is skipped if the file type is
	 * not recognized.
	 * 
	 * @param file the file to process
	 * @return {@code true} if the file was modified
	 * @throws InterruptedException if the process was interrupted
	 * @throws IOException if an I/O error occurred while processing the file
	 */
	public boolean process(File file) throws IOException, InterruptedException {
		boolean fileChanged = false;
		String extension = FilenameUtils.getExtension(file.getName());
		
		if (extension.equalsIgnoreCase("template")) {
			extension = FilenameUtils.getExtension(file.getName().substring(0,
					file.getName().length() - extension.length() - 1));
		}
		
		TemplateFileType fileType = TemplateFileType.fromExtension(extension);
		
		if (fileType == null) {
			System.out.println("Skipping " + file + ", not a recognized extension");
			return fileChanged;
		}
				
		System.out.println("Processing " + file);
		File tempFile = File.createTempFile("temp", null);
		
		String lineSeparator = determineLineSeparator(file);
		
		try (LineReader reader = LineReader.wrap(new FileReader(file));
			 PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
			for (String line : reader) {
				writer.write(line);
				writer.write(lineSeparator);
				
				Matcher matcher = REGEX.matcher(line);
								
				if (matcher.matches()) {
					Processor processor = Processor.fromString(matcher.group(1));
					
					FormattingOptions options = new FormattingOptions(fileType);
					options.parseOptions(matcher.group(2));
					options.setLineSeparator(lineSeparator);
					options.getProperties().setBoolean("clean", clean);
					options.getProperties().setLong("seed", seed);
					options.getProperties().setString("baseDirectory", file.getParentFile().toString());
					
					String src = options.getSource();
					
					// For source code, derive the language from file extension if not given explicitly
					if (processor instanceof CodeProcessor && !options.getProperties().contains("language")) {
						options.setLanguage(Language.fromExtension(FilenameUtils.getExtension(src)));
					}
					
					System.out.println("    > Running " + processor.getClass().getSimpleName() + ": " + options);
					
					fileChanged |= processor.run(reader, writer, options);
				} else {
					// TODO: Remove after cleaning up usages
					Pattern legacy = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:(.*)\\s+-->");

					if (legacy.matcher(line).matches()) {
						throw new IOException("Found legacy comment: " + line);
					}
				}
			}
		}
		
		if (update) {
			Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.deleteIfExists(tempFile.toPath());
		}
		
		return fileChanged;
	}
	
	// TODO: Can we get rid of FormattingOptions and use TypedProperties intead?
	
	/**
	 * Formatting options for the code block.
	 */
	static class FormattingOptions {
		
		/**
		 * Constant representing the first line in the file.
		 */
		private static final int FIRST_LINE = 1;
		
		/**
		 * Constant representing the last line in the file.
		 */
		private static final int LAST_LINE = Integer.MAX_VALUE;

		/**
		 * The type of the file.
		 */
		private final TemplateFileType fileType;
		
		/**
		 * The starting line number.
		 */
		private int startingLine;
		
		/**
		 * The ending line number.
		 */
		private int endingLine;
		
		/**
		 * Identifier for the code block, as an alternative to line numbers.
		 */
		private String identifier;
		
		/**
		 * The line separator of the original file.
		 */
		private String lineSeparator;
		
		private final TypedProperties properties;
				
		/**
		 * Constructs default options for the given processor.
		 * 
		 * @param processor the processor
		 */
		public FormattingOptions(TemplateFileType fileType) {
			super();
			this.fileType = fileType;
			
			startingLine = FIRST_LINE;
			endingLine = LAST_LINE;
			lineSeparator = System.lineSeparator();
			properties = new TypedProperties();
		}
		
		public void parseOptions(String str) throws IOException {
			try (StringReader argumentsReader = new StringReader(str)) {
				StreamTokenizer tokenizer = new StreamTokenizer(argumentsReader);
				tokenizer.resetSyntax();
				
				tokenizer.wordChars(33, 33); // 34 is "
				tokenizer.wordChars(35, 37); // 38 is '
				tokenizer.wordChars(39, 60); // 58 is :
				tokenizer.wordChars(62, 126);
				tokenizer.wordChars(128 + 32, 255);
				tokenizer.whitespaceChars(0, ' ');
				tokenizer.quoteChar('"');
				tokenizer.quoteChar('\'');

				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
						throw new IOException("Failed to parse comment, encountered unexpected character '" +
								(char)tokenizer.ttype + "'");
					}
					
					String key = tokenizer.sval;
					
					if (key.startsWith("[") && key.endsWith("]")) {
						properties.setString("lines", key.substring(1, key.length() - 1));
						continue;
					}
					
					tokenizer.nextToken();
									
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_EOF) {
						properties.setBoolean(key, true);
						tokenizer.pushBack();
						continue;
					} else if (tokenizer.ttype != '=') {
						throw new IOException("Failed to parse comment, expected '=' after key");
					}
									
					tokenizer.nextToken();
					
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == '"' || tokenizer.ttype == '\'') {
						properties.setString(key, tokenizer.sval);
					} else {
						throw new IOException("Failed to parse comment, expected value after '='");
					}
				}
			}
			
			// TODO: MOve?
			parseLineNumbers(properties.getString("lines", null));
		}
		
		/**
		 * Parses the line numbers in the format {@code [<startingLine>:<endingLine>]}.  The format is analogous
		 * to Python string splices.
		 * <pre>{@code
		 *   [5:10]       // Copies lines 5-10
		 *   [5:5]        // Copies only line 5
		 *   [:10]        // Copies first 10 lines
		 *   [-10:]       // Copies last 10 lines
		 *   [:-1]        // Copies everything except the last line
		 *   [:]          // Copies entire file
		 * }</pre>
		 * <p>
		 * A string identifier can also be given, in which case we locate the code block between the comments
		 * <pre>{@code
		 *   // begin-example:<id>
		 *   ... code block
		 *   // end-example:<id>
		 * }</pre>
		 * 
		 * @param str the string representation of the line numbers
		 * @throws IOException 
		 */
		public void parseLineNumbers(String str) throws IOException {
			if (str == null) {
				startingLine = FIRST_LINE;
				endingLine = LAST_LINE;
				return;
			}
			
			if (str.startsWith("[") && str.endsWith("]")) {
				str = str.substring(1, str.length()-1);
			}
			
			Matcher matcher = LINES_REGEX.matcher(str);
			
			if (matcher.matches()) {				
				startingLine = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : FIRST_LINE;
				endingLine = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) :
					matcher.group(2) != null ? LAST_LINE : startingLine;
			} else {
				throw new IOException("Malformed line range: " + str);
			}
		}
		
		/**
		 * Sets the line separator for the original file.
		 * 
		 * @param lineSeparator the line separator
		 */
		public void setLineSeparator(String lineSeparator) {
			this.lineSeparator = lineSeparator;
		}
		
		public TemplateFileType getFileType() {
			return fileType;
		}
		
		public Language getLanguage() {
			return properties.getEnum("language", Language.class, Language.Text);
		}
		
		public void setLanguage(Language language) {
			properties.setEnum("language", language);
		}
		
		public String getSource() {
			return properties.getString("src");
		}
		
		/**
		 * Returns the starting line number.  Line numbers start at index {@code 1}.  If negative, the line number
		 * is measured from the end of the file.
		 * 
		 * @return the starting line number
		 */
		public int getStartingLine() {
			return startingLine;
		}
		
		/**
		 * Returns the ending line number.  Line numbers start at index {@code 1}.  If the value exceeds the length of
		 * the file, will include all lines up to the end of the file.  If the value is negative, the line number is
		 * measured from the end of the file.
		 * 
		 * @return the ending line number
		 */
		public int getEndingLine() {
			return endingLine;
		}
		
		/**
		 * Returns the identifier for the code block.  When set, this identifier is used to identify the section of
		 * code instead of line numbers.
		 * 
		 * @return the identifier
		 */
		public String getIdentifier() {
			return properties.getString("id", null);
		}
		
		/**
		 * Returns the line separaator of the original file.
		 * 
		 * @return the line separator
		 */
		public String getLineSeparator() {
			return lineSeparator;
		}
		
		public TypedProperties getProperties() {
			return properties;
		}

		/**
		 * Formats the given code block based on the options and target file type.
		 * 
		 * @param content the content to format
		 * @return the formatted content
		 * @throws IOException if an I/O error occurred
		 */
		public String formatCode(String content) throws IOException {
			List<String> lines = new ArrayList<>(content.lines().toList());
			Language language = getLanguage();
			int startingLine = getStartingLine();
			int endingLine = getEndingLine();
			
			if (getIdentifier() != null) {
				String identifier = getIdentifier();
				
				startingLine = -1;
				endingLine = -1;
				
				for (int i = 0; i < lines.size(); i++) {
					Matcher beginMatcher = BEGIN_EXAMPLE_REGEX.matcher(lines.get(i));
					
					if (beginMatcher.matches() && beginMatcher.group(2).equalsIgnoreCase(identifier)) {
						startingLine = i + 1;
					}
					
					Matcher endMatcher = END_EXAMPLE_REGEX.matcher(lines.get(i));
					
					if (endMatcher.matches() && endMatcher.group(2).equalsIgnoreCase(identifier)) {
						endingLine = i - 1;
					}
				}
				
				if (startingLine < 0 || endingLine < 0 || endingLine < startingLine) {
					throw new IOException("Failed to find code block identified by '" + identifier + "'");
				}
				
				// offset line numbers since we start at 1
				startingLine += 1;
				endingLine += 1;
			}
			
			if (startingLine < 0) {
				startingLine += lines.size() + 1;
			} else if (startingLine == 0) {
				startingLine = 1;
			}
				
			if (endingLine < 0) {
				endingLine += lines.size();
			}
				
			lines = lines.subList(Math.max(0, startingLine - 1), Math.min(lines.size(), endingLine));
			
			if (!properties.getBoolean("preserveComments", false)) {
				lines = language.stripComments(lines);
			}
			
			if (!properties.getBoolean("preserveIndentation", false)) {
				lines = language.stripIndentation(lines);
			}
			
			if (!properties.getBoolean("preserveTabs", false)) {
				lines = language.replaceTabsWithSpaces(lines);
			}
			
			if (properties.getBoolean("showEllipsis", false)) {
				lines.add("...");
			}
			
			return fileType.formatCodeBlock(lines, this);
		}
		
		/**
		 * Formats the image on the options and target file type.
		 * 
		 * @param path the image path
		 * @return the formatted image
		 */
		public String formatImage(String path) {
			return fileType.formatImage(path, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			for (String key : properties.keySet()) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				
				sb.append(key + "=" + properties.getString(key));
			}
			
			return sb.toString();
		}
		
	}
	
	private abstract static class Processor {
		
		private static final Map<String, Processor> INSTANCES;
		
		static {
			INSTANCES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			INSTANCES.put("code", new CodeProcessor());
			INSTANCES.put("exec", new ExecProcessor());
			INSTANCES.put("plot", new PlotProcessor());
		}
		
		/**
		 * Determine the processor from its string representation using case-insensitive matching.
		 * 
		 * @param value the string representation of the processor
		 * @return the processor
		 * @throws IllegalArgumentException if the processor is not supported
		 */
		public static Processor fromString(String value) {
			Processor instance = INSTANCES.get(value);
			
			if (instance == null) {
				return Validate.that("value", value).failUnsupportedOption(INSTANCES.keySet());
			}
			
			return instance;
		}
		
		public abstract boolean run(LineReader reader, PrintWriter writer, FormattingOptions options) throws IOException;
		
		protected abstract TextMatcher getReplaceMatcher(FormattingOptions options);
		
		protected boolean replace(LineReader reader, PrintWriter writer, String newContent, FormattingOptions options) throws IOException {
			String oldContent = scanContentToReplace(reader, writer, options);
			
			boolean contentChanged = diff(oldContent, newContent);
			
			writer.write(newContent);
			writer.write(options.getLineSeparator());
			
			return contentChanged;
		}
		
		/**
		 * Locates a section of the file immediately following the current position that matches a pattern.  The match must
		 * occur immediately following the current position.  Any empty lines are ignored for matching but are written to
		 * the output.  Returns the matched content, including the start / end lines.
		 * 
		 * @param reader the reader for the original file
		 * @param writer the writer for the modified file
		 * @return the code block
		 * @throws IOException if an I/O error occurred while reading the file
		 */
		private String scanContentToReplace(LineReader reader, PrintWriter writer, FormattingOptions options) throws IOException {
			StringBuilder sb = new StringBuilder();
			TextMatcher matcher = getReplaceMatcher(options);
			boolean inBlock = false;
			
			for (String line : reader) {
				if (!inBlock) {
					if (matcher.isStart(line)) {
						sb.append(line);
						sb.append(options.getLineSeparator());
						
						if (!matcher.isBlock() && matcher.isEnd(line)) {
							return sb.toString();
						}
						
						inBlock = true;
					} else if (line.isBlank()) {
						writer.write(line);
						writer.write(options.getLineSeparator());
					} else {
						throw new IOException("Found non-empty line '" + line + "' when scanning for matching content");
					}
				} else {
					sb.append(line);
					sb.append(options.getLineSeparator());
					
					if (matcher.isEnd(line)) {
						return sb.toString();
					}
				}
			}
			
			throw new IOException("Reached end of file before finding matching content");
		}
		
		/**
		 * Determines if any differences exist between the two code blocks, displaying any differences in the terminal.
		 * This will flag whitespace differences, but excludes the end of line characters.
		 * 
		 * @param first the first code block
		 * @param second the second code block
		 * @return {@code true} if any differences were detected
		 */
		private boolean diff(String first, String second) {
			List<String> firstLines = first.lines().toList();
			List<String> secondLines = second.lines().toList();
			
			boolean result = false;
			
			for (int i = 0; i < Math.max(firstLines.size(), secondLines.size()); i++) {
				if (i >= firstLines.size()) {
					System.out.println("      ! ++ " + secondLines.get(i));
					result = true;
				} else if (i >= secondLines.size()) {
					System.out.println("      ! -- " + firstLines.get(i));
					result = true;
				} else if (!firstLines.get(i).equals(secondLines.get(i))) {
					System.out.println("      ! -- " + firstLines.get(i));
					System.out.println("      ! ++ " + secondLines.get(i));
					result = true;
				}
			}
			
			return result;
		}
		
	}
	
	private static class CodeProcessor extends Processor {
		
		/**
		 * Caches the content of the files / resources that are accessed by this tool.
		 */
		private final Map<String, String> cache;
		
		public CodeProcessor() {
			super();
			cache = new HashMap<>();
		}
		
		public boolean run(LineReader reader, PrintWriter writer, FormattingOptions options) throws IOException {
			return replace(reader, writer, options.formatCode(loadContent(options.getSource())), options);
		}
		
		protected TextMatcher getReplaceMatcher(FormattingOptions options) {
			return options.getFileType().getCodeBlockMatcher();
		}
		
		/**
		 * Loads content from a file or URL (restricted to GitHub).
		 * 
		 * @param path the path to load
		 * @return the content
		 * @throws IOException if an error occurred loading the file or URL
		 */
		private String loadContent(String path) throws IOException {
			if (cache.containsKey(path)) {
				return cache.get(path);
			}
			
			URI uri = null;
			
			try {
				uri = URI.create(path);
			} catch (IllegalArgumentException e) {
				uri = null;
			}
			
			if (uri == null || uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("file")) {
				String content = Files.readString(Path.of(path), StandardCharsets.UTF_8);
				cache.put(path, content);
				return content;
			} else if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
				if (!uri.getHost().equalsIgnoreCase("raw.githubusercontent.com") ||
						!uri.getPath().startsWith("/MOEAFramework/")) {
					throw new IOException("Invalid path '" + path + "', unsupport host or path");
				}
				
				String content = IOUtils.toString(uri.toURL(), StandardCharsets.UTF_8);
				cache.put(path, content);
				return content;
			} else {
				throw new IOException("Invalid path '" + path + "', unsupported scheme");
			}
		}
		
	}
	
	private static class ExecProcessor extends Processor {
		
		public boolean run(LineReader reader, PrintWriter writer, FormattingOptions options) throws IOException {
			compile(options);
			return replace(reader, writer, options.formatCode(execute(options)), options);
		}
		
		protected TextMatcher getReplaceMatcher(FormattingOptions options) {
			return options.getFileType().getCodeBlockMatcher();
		}
		
		/**
		 * Compiles the Java program.
		 * 
		 * @return the standard output produced by the program
		 * @throws IOException if an I/O error occurred while running the process
		 * @throws InterruptedException if the process was interrupted
		 */
		protected void compile(FormattingOptions options) throws IOException {
			String source = options.getSource();
			String extension = FilenameUtils.getExtension(source);
			boolean clean = options.getProperties().getBoolean("clean", false);
			
			if (!extension.equalsIgnoreCase("java")) {
				Validate.that("file extension", extension).failUnsupportedOption("java");
			}
			
			Path javaPath = Path.of(source);
			Path classPath = javaPath.getParent().resolve(
					FilenameUtils.removeExtension(javaPath.getFileName().toString()) + ".class");

			if (clean || !Files.exists(classPath) || FileUtils.isFileNewer(javaPath.toFile(), classPath.toFile())) {
				FileUtils.deleteQuietly(classPath.toFile());

				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				
				if (compiler.run(null, null, null, javaPath.toAbsolutePath().toString()) != 0) {
					throw new IOException("Failed to compile " + javaPath);
				}
			}
		}
		
		/**
		 * Runs the Java program and captures its output.
		 * 
		 * @param filename the Java file to run
		 * @return the standard output produced by the program
		 * @throws IOException if an I/O error occurred while running the process
		 */
		protected String execute(FormattingOptions options) throws IOException {
			PrintStream oldOut = System.out;
			String source = options.getSource();
			String[] args = options.getProperties().getStringArray("args", new String[0]);
			long seed = options.getProperties().getLong("seed");
						
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream newOut = new PrintStream(baos)) {
				System.setOut(newOut);
					
				Class<?> cls = Class.forName(getClassName(source), true, Thread.currentThread().getContextClassLoader());
				Method mainMethod = cls.getDeclaredMethod("main", String[].class);
				
				Settings.PROPERTIES.setString(Settings.KEY_CLI_EXECUTABALE, "./cli " + cls.getSimpleName());
				
				PRNG.setSeed(seed);
				mainMethod.invoke(null, (Object)args);

				newOut.close();
				return baos.toString();
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException |
					InvocationTargetException e) {
				throw new IOException("Failed to execute " + getClassName(source), e);
			} finally {
				System.setOut(oldOut);
			}
		}
		
		/**
		 * Returns the fully-qualified Java class name derived from the file name.
		 * 
		 * @param filename the Java file name
		 * @return the fully-qualified Java class name
		 */
		private String getClassName(String filename) {
			Path path = Paths.get(FilenameUtils.removeExtension(filename));
			
			if (path.startsWith("examples") || path.startsWith("src") || path.startsWith("test")) {
				path = path.subpath(1, path.getNameCount());
			}
			
			return path.toString().replaceAll("[\\\\/]", ".");
		}
		
	}
	
	private static class PlotProcessor extends ExecProcessor {
		
		public boolean run(LineReader reader, PrintWriter writer, FormattingOptions options) throws IOException {
			compile(options);
			execute(options);
			return replace(reader, writer, options.formatImage(capturePlot(options)), options);
		}
		
		protected TextMatcher getReplaceMatcher(FormattingOptions options) {
			return options.getFileType().getImageMatcher();
		}
		
		private String capturePlot(FormattingOptions options) throws IOException {
			try {
				String baseDirectory = options.getProperties().getString("baseDirectory");
				String dest = options.getProperties().getString("dest");
				File file = new File(baseDirectory, dest);
				
				SwingUtilities.invokeAndWait(() -> {
					for (Window window : Window.getWindows()) {
						if (window.isShowing() &&
								window instanceof JFrame frame &&
								frame.getContentPane().getComponent(0) instanceof ChartPanel chartPanel) {
							try {
								ImageUtils.save(chartPanel.getChart(), file);
							} catch (IOException e) {
								throw new UncheckedIOException(e);
							}
							
							window.dispose();
						}
					}
				});
				
				return dest;
			} catch (InvocationTargetException | InterruptedException e) {
				throw new IOException("Failed to capture plot", e);
			}
		}
		
	}
	
	/**
	 * The language shown in the code block, used to ensure the appropriate syntax highlighting is configured.
	 */
	enum Language {
		
		/**
		 * Java source code.
		 */
		Java("java"),
		
		/**
		 * C/C++ source code.
		 */
		C("c", "cpp"),
		
		/**
		 * Bash or terminal commands.
		 */
		Bash("sh"),
		
		/**
		 * Plain text.
		 */
		Text;
		
		/**
		 * The file extensions for the file type.
		 */
		private final String[] extensions;
		
		Language(String... extensions) {
			this.extensions = extensions;
		}
		
		/**
		 * Determine the language from its string representation using case-insensitive matching.
		 * 
		 * @param value the string representation of the language
		 * @return the language
		 * @throws IllegalArgumentException if the language is not supported
		 */
		public static Language fromString(String value) {
			return TypedProperties.getEnumFromString(Language.class, value);
		}
		
		/**
		 * Determine the language from the file extension.
		 * 
		 * @param extension the file extension, excluding the {@code "."}
		 * @return the language, or {@code Text} if the extension is not recognized
		 */
		public static Language fromExtension(String extension) {
			for (Language language : values()) {
				for (String fileExtension : language.extensions) {
					if (extension.equalsIgnoreCase(fileExtension)) {
						return language;
					}
				}
			}
						
			return Text;
		}
		
		/**
		 * Strips comments out of the code block.
		 * 
		 * @param lines the code block
		 * @return the updated code block
		 */
		public List<String> stripComments(List<String> lines) {
			String content = String.join(DEFAULT_LINE_SEPARATOR, lines);
			content = stripComments(content);
			
			List<String> result = new ArrayList<>(content.lines().toList());
			return stripLeadingAndTrailingBlankLines(result);
		}
		
		/**
		 * Removes any leading indentation from the code block.
		 * 
		 * @param lines the code block
		 * @return the updated code block
		 */
		public List<String> stripIndentation(List<String> lines) {
			String content = String.join(DEFAULT_LINE_SEPARATOR, lines);
			content = content.stripIndent();
			return new ArrayList<>(content.lines().toList());
		}
		
		/**
		 * Replaces tabs with four spaces.
		 * 
		 * @param lines the code block
		 * @return the updated code block
		 */
		public List<String> replaceTabsWithSpaces(List<String> lines) {
			List<String> result = new ArrayList<>();
			
			for (String line : lines) {
				result.add(line.replaceAll("[\\t]", "    "));
			}

			return result;
		}
		
		/**
		 * Removes any leading or trailing blank lines, which are empty or contain only whitespace.
		 * 
		 * @param lines the code block
		 * @return the updated code block
		 */
		public List<String> stripLeadingAndTrailingBlankLines(List<String> lines) {
			List<String> result = new ArrayList<>(lines);
			
			while (result.get(0).isBlank()) {
				result.remove(0);
			}
			
			while (result.get(result.size()-1).isBlank()) {
				result.remove(result.size()-1);
			}
			
			return result;
		}
		
		/**
		 * Strips comments out of the code block.  This also removes any duplicate blank lines that may result
		 * when removing the comments.
		 * 
		 * @param content the code block
		 * @return the updated code block
		 */
		public String stripComments(String content) {
			return switch (this) {
				case Java, C -> {
					// Remove C-style // comments
					content = content.replaceAll("(?<=[\\r\\n])[\\t ]*\\/\\/[^\\n]*\\r?\\n", "");
					content = content.replaceAll("[\\t ]*\\/\\/[^\\n]*", "");
					
					// Remove C-style /* */ and Javadoc /** */ comments
					content = content.replaceAll("(?<=[\\r\\n])[\\t ]*\\/\\*[^*]*\\*+(?:[^\\/*][^*]*\\*+)*\\/[\\t ]*\\r?\\n", "");
					content = content.replaceAll("[\\t ]*\\/\\*[^*]*\\*+(?:[^\\/*][^*]*\\*+)*\\/", "");
					
					yield content;
				}
				case Text, Bash -> content;
			};
		}
		
	}
	
	/**
	 * Supported file types that are processed by this utility.  This defines how code blocks are identified and
	 * formatted for a particular file format.
	 */
	enum TemplateFileType {
		
		/**
		 * Markdown files.
		 */
		Markdown("md"),
		
		/**
		 * HTML or XSLT files.
		 */
		Html("html", "xml");
		
		/**
		 * The file extensions for the file type.
		 */
		private final String[] extensions;
		
		/**
		 * Constructs a new file type with the given extensions.
		 * 
		 * @param extensions the extensions
		 */
		private TemplateFileType(String... extensions) {
			this.extensions = extensions;
		}
		
		/**
		 * Determine the file type from the file extension.
		 * 
		 * @param extension the file extension, excluding the {@code "."}
		 * @return the file type, or {@code null} if the file type is not recognized
		 */
		public static TemplateFileType fromExtension(String extension) {
			for (TemplateFileType fileType : values()) {
				for (String fileExtension : fileType.extensions) {
					if (extension.equalsIgnoreCase(fileExtension)) {
						return fileType;
					}
				}
			}
						
			return null;
		}
		
		/**
		 * Returns the text matcher for code blocks.
		 * 
		 * @return the text matcher
		 */
		public TextMatcher getCodeBlockMatcher() {
			return switch (this) {
				case Markdown -> new TextMatcher(s -> s.startsWith("```"), s -> s.endsWith("```"), true);
				case Html -> new TextMatcher(s -> s.startsWith("<pre"), s -> s.endsWith("</pre>"), true);
			};
		}
		
		/**
		 * Returns the text matcher for images.  Note that for Markdown, we prefer using embedded HTML tags instead of
		 * {@code ![alt_text](image_url)} as it provides better control over the image scale and alignment.
		 * 
		 * @return the text matcher
		 */
		public TextMatcher getImageMatcher() {
			return switch (this) {
				case Markdown -> new TextMatcher(s -> s.startsWith("<p align=\"center\">"), s -> s.endsWith("</p>"), false);
				case Html -> new TextMatcher(s -> s.startsWith("<img"), s -> s.endsWith("/>"), false);
			};
		}
		
		/**
		 * Returns the name of the "brush" that provides the appropriate syntax highlighting for the language.
		 * Defaults to plain text if the language is not recognized.
		 * 
		 * @param language the language displayed in the code block
		 * @return the name of the "brush"
		 */
		public String getBrush(Language language) {
			// TODO: Use TreeSet?
			final Set<String> markdownBrush = new HashSet<>(Arrays.asList("java", "c", "bash"));
			final Set<String> htmlBrush = new HashSet<>(Arrays.asList("java"));
			
			String brushName = language.name().toLowerCase();
						
			return switch (this) {
				case Markdown -> markdownBrush.contains(brushName) ? brushName : "";
				case Html -> htmlBrush.contains(brushName) ? brushName : "plain";
			};
		}
		
		/**
		 * Wraps the code block in the starting / ending lines appropriate for the file type.
		 * 
		 * @param lines the code block
		 * @param options the formatting options
		 * @return the formatted code block
		 */
		public String formatCodeBlock(List<String> lines, FormattingOptions options) {
			StringBuilder sb = new StringBuilder();
			
			switch (this) {
				case Markdown -> {
					sb.append("```" + getBrush(options.getLanguage()));
					sb.append(options.getLineSeparator());
					sb.append(String.join(options.getLineSeparator(), lines));
					sb.append(options.getLineSeparator());
					sb.append("```");
				}
				case Html -> {
					sb.append("<pre class=\"brush: " + getBrush(options.getLanguage()) + "; toolbar: false;\">");
					sb.append(options.getLineSeparator());
					sb.append("<![CDATA[");
					sb.append(options.getLineSeparator());
					sb.append(String.join(options.getLineSeparator(), lines));
					sb.append(options.getLineSeparator());
					sb.append("]]>");
					sb.append(options.getLineSeparator());
					sb.append("</pre>");
				}
				default -> {
					sb.append(String.join(options.getLineSeparator(), lines));
				}
			}
			
			return sb.toString();
		}
		
		/**
		 * Wraps the image path with any image tags and formatting options.
		 * 
		 * @param path the image path, either relative to the source document or an absolute URL
		 * @param options the formatting options
		 * @return the formatted image
		 */
		public String formatImage(String path, FormattingOptions options) {
			StringBuilder sb = new StringBuilder();
			String width = options.getProperties().getString("width", "100%");
			
			switch (this) {
				case Markdown -> {
					sb.append("<p align=\"center\">");
					sb.append(options.getLineSeparator());
					sb.append("\t<img src=\"" + path + "\" width=\"" + width + "\" />");
					sb.append(options.getLineSeparator());
					sb.append("</p>");
				}
				case Html -> {
					sb.append("<img src=\"" + path + "\" width=\"" + width + "\" />");
				}
				default -> {}
			}
			
			return sb.toString();
		}
	}
	
	private static class TextMatcher {
		
		private final Predicate<String> startMatcher;
		
		private final Predicate<String> endMatcher;
		
		private final boolean block;
		
		public TextMatcher(Predicate<String> startMatcher, Predicate<String> endMatcher, boolean block) {
			super();
			this.startMatcher = startMatcher;
			this.endMatcher = endMatcher;
			this.block = block;
		}
		
		public boolean isStart(String line) {
			return startMatcher.test(line);
		}

		public boolean isEnd(String line) {
			return endMatcher.test(line);
		}

		public boolean isBlock() {
			return block;
		}
		
	}
	
	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new UpdateCodeSamples().start(args);
	}

}
