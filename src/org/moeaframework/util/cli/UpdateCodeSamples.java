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

import java.awt.HeadlessException;
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
import java.lang.reflect.Modifier;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartPanel;
import org.moeaframework.analysis.plot.ImageUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.validate.Validate;

/**
 * Utility to update code samples and output found in various documentation files, including Markdown and HTML files.
 * This syncs the examples with actual code that is compiled and tested to ensure its correctness.
 * <p>
 * Special comments, called "processor instructions", are embedded within the documentation with the following format:
 * <pre>{@code
 *   <!-- :<processor>: arg1 arg2 ... -->
 * }</pre>
 * The following processors are available:
 * <ul>
 *   <li>{@code code} - Copies all or part of a source code file into a code block in the template
 *   <li>{@code exec} - Compiles and executes a Java file, copying all or part of the output to a code block
 *   <li>{@code plot} - Compiles and executes a Java file, then snapshots the resulting plot to an image file
 * </ul>
 * Processors have required and optional arguments.  For instance, the {@code code} processor requires {@code src}
 * with optional {@code lines} and style options.  Options are key-value pairs written in the form {@code key=value}.
 * The value can be quoted ({@code key="quoted value"}) or excluded for boolean arguments.
 * <p>
 * The option {@code "lines"} is used to specify which line numbers are copied to the output.  The format is similar to
 * Python string splices:
 * <pre>{@code
 *   lines=5:10       // Copies lines 5-10
 *   lines=5          // Copies only line 5
 *   lines=:10        // Copies first 10 lines
 *   lines=-10:       // Copies last 10 lines
 *   lines=:-1        // Copies everything except the last line
 * }</pre>
 * This utility can be run in validate-only mode or update mode.  In validate mode, any changes to the files will
 * result in an error.  This is useful in CI to validate the docs are up-to-date.  In update mode, the files are
 * updated with any changes.
 */
public class UpdateCodeSamples extends CommandLineUtility {
	
	private static final long DEFAULT_SEED = 123456;
	
	private static final String DEFAULT_LINE_SEPARATOR = "\n";
	
	private static final File[] DEFAULT_PATHS = new File[] { new File("docs/"), new File("website/"), new File("src/README.md.template") };
	
	/**
	 * If {@code true}, the files are updated with any changes.  Otherwise, this runs in validation mode wherein any
	 * detected change will result in an error.
	 */
	private boolean update;
	
	/**
	 * If {@code true}, any compiled source files are cleaned and rebuilt.  Otherwise, the file timestamps are checked
	 * to determine which must be recompiled.
	 */
	private boolean clean;
	
	/**
	 * The random number generator seed supplied when executing code to keep results consistent.
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
		System.out.println("Seed: " + seed);
		
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
	 * @throws IOException if an I/O error occurred while processing a file
	 */
	private boolean scan(File file) throws IOException {
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
	 * Processes a single file, validating or updating code samples.  The file is skipped if the file type is
	 * not recognized.
	 * 
	 * @param file the file to process
	 * @return {@code true} if the file was modified
	 * @throws IOException if an I/O error occurred while processing the file
	 */
	public boolean process(File file) throws IOException {
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
				
				ProcessorInstruction instruction = fileType.tryParseProcessorInstruction(line);
								
				if (instruction != null) {
					instruction.setLineSeparator(lineSeparator);
					instruction.setClean(clean);
					instruction.setSeed(seed);
					instruction.setTemplateFile(file);
					
					System.out.println("    > Running " + instruction);
					
					fileChanged |= instruction.run(reader, writer);
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
	
	/**
	 * Determines the line separator in use by the source file.
	 * 
	 * @param file the file
	 * @return the line separator
	 * @throws IOException if an I/O error occurred while reading the file
	 */
	private static String determineLineSeparator(File file) throws IOException {
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
	
	static class ProcessorInstruction {
		
		private static final TreeSet<String> INTERNAL_KEYS;
		
		static {
			INTERNAL_KEYS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			INTERNAL_KEYS.add("templateFile");
			INTERNAL_KEYS.add("clean");
			INTERNAL_KEYS.add("seed");
		}
		
		/**
		 * The configured processor.
		 */
		private final Processor processor;

		/**
		 * The type of the template file that determines how the generated output is formatted.
		 */
		private final TemplateFileType fileType;
		
		/**
		 * The line separator of the original file.
		 */
		private String lineSeparator;

		/**
		 * The options supplied to this instruction.
		 */
		private final TypedProperties options;
				
		/**
		 * Constructs default options for the given processor.
		 * 
		 * @param processor the processor
		 */
		public ProcessorInstruction(Processor processor, TemplateFileType fileType) {
			super();
			this.processor = processor;
			this.fileType = fileType;
			
			lineSeparator = System.lineSeparator();
			options = new TypedProperties();
		}
		
		protected void parseOptions(String str) throws IOException {
			try (StringReader argumentsReader = new StringReader(str)) {
				StreamTokenizer tokenizer = new StreamTokenizer(argumentsReader);
				tokenizer.resetSyntax();
				
				tokenizer.wordChars(33, 33); // 34 is "
				tokenizer.wordChars(35, 37); // 38 is '
				tokenizer.wordChars(39, 60); // 61 is =
				tokenizer.wordChars(62, 126);
				tokenizer.wordChars(128 + 32, 255);
				tokenizer.whitespaceChars(0, ' ');
				tokenizer.quoteChar('"');
				tokenizer.quoteChar('\'');

				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
						throw new IOException("Failed to parse instruction, encountered unexpected character '" +
								(char)tokenizer.ttype + "'");
					}
					
					String key = tokenizer.sval;
					
					tokenizer.nextToken();
									
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_EOF) {
						options.setBoolean(key, true);
						tokenizer.pushBack();
						continue;
					} else if (tokenizer.ttype != '=') {
						throw new IOException("Failed to parse instruction, expected '=' after key");
					}
									
					tokenizer.nextToken();
					
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == '"' || tokenizer.ttype == '\'') {
						options.setString(key, tokenizer.sval);
					} else {
						throw new IOException("Failed to parse instruction, expected value after '='");
					}
				}
			}
		}
		
		public TemplateFileType getFileType() {
			return fileType;
		}
		
		public long getSeed() {
			return options.getLong("seed", DEFAULT_SEED);
		}
		
		public void setSeed(long seed) {
			options.setLong("seed", seed);
		}
		
		public boolean isClean() {
			return options.getBoolean("clean", false);
		}
		
		public void setClean(boolean clean) {
			options.setBoolean("clean", clean);
		}

		public File getTemplateFile() {
			return new File(options.getString("templateFile"));
		}
		
		public void setTemplateFile(File templateFile) {
			options.setString("templateFile", templateFile.toString());
		}
		
		/**
		 * Returns the line separator of the original file.
		 * 
		 * @return the line separator
		 */
		public String getLineSeparator() {
			return lineSeparator;
		}
		
		/**
		 * Sets the line separator for the original file.
		 * 
		 * @param lineSeparator the line separator
		 */
		public void setLineSeparator(String lineSeparator) {
			this.lineSeparator = lineSeparator;
		}
		
		/**
		 * Returns the options supplied with this instruction.
		 * 
		 * @return the options
		 */
		public TypedProperties getOptions() {
			return options;
		}
		
		/**
		 * Runs this processor instruction.
		 * 
		 * @param reader the reader for the original file
		 * @param writer the writer for the modified file
		 * @return {@code true} if the contents were changed; {@code false} otherwise
		 * @throws IOException if an I/O error occurred
		 */
		public boolean run(LineReader reader, PrintWriter writer) throws IOException {
			return processor.run(reader, writer, this);
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
			
			if (options.contains("id")) {
				String identifier = options.getString("id");
				TextMatcher matcher = processor.getSourceLanguage(this).getSnippetMatcher(identifier);
				
				boolean inSnippet = false;
				Iterator<String> iterator = lines.iterator();
				
				while (iterator.hasNext()) {
					String line = iterator.next();
					
					if (matcher.isStart(line)) {
						inSnippet = true;
						iterator.remove();
					} else if (matcher.isEnd(line)) {
						inSnippet = false;
						iterator.remove();
					} else if (!inSnippet) {
						iterator.remove();
					}
				}
				
				if (lines.isEmpty()) {
					throw new IOException("Failed to find start of code snippet with id '" + identifier + "'");
				}
				
				if (inSnippet) {
					throw new IOException("Failed to find end of code snippet with id '" + identifier + "'");
				}
			} else {
				String lineRange = options.getString("lines", ":");
				String[] tokens = lineRange.split(":", 2);
				int startingLine = 1;
				int endingLine = Integer.MAX_VALUE;
				
				if (!tokens[0].isBlank()) {
					startingLine = Integer.parseInt(tokens[0]);
				}

				if (tokens.length > 1 && !tokens[1].isBlank()) {
					endingLine = Integer.parseInt(tokens[1]);
				} else if (tokens.length == 1 && !tokens[0].isBlank()) {
					endingLine = Integer.parseInt(tokens[0]);
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
			}
			
			// Apply any formatting specific to the output source code language
			Language language = processor.getOutputLanguage(this);
			
			if (!options.getBoolean("preserveComments", false)) {
				lines = language.stripComments(lines);
			}
			
			if (!options.getBoolean("preserveIndentation", false)) {
				lines = language.stripIndentation(lines);
			}
			
			if (!options.getBoolean("preserveTabs", false)) {
				lines = language.replaceTabsWithSpaces(lines);
			}
			
			if (options.getBoolean("showEllipsis", false)) {
				lines.add("...");
			}
			
			return fileType.formatCodeBlock(lines, language, this);
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
			
			sb.append(processor.getClass().getSimpleName());
			sb.append(":");
			
			for (String key : options.keySet()) {
				if (INTERNAL_KEYS.contains(key)) {
					continue;
				}
				
				sb.append(" ");
				sb.append(key + "=" + options.getString(key));
			}
			
			return sb.toString();
		}
		
	}
	
	abstract static class Processor {
		
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

		public Processor() {
			super();
		}
		
		/**
		 * Runs the processor.  Typically, a processor should generate the new output and call
		 * {@link #replace(LineReader, PrintWriter, String, ProcessorInstruction)}.
		 * 
		 * @param reader the reader for the original file
		 * @param writer the writer for the modified file
		 * @param instruction the processor instruction being executed
		 * @return {@code true} if the contents were changed; {@code false} otherwise
		 * @throws IOException if an I/O error occurred
		 */
		public abstract boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException;
		
		/**
		 * Returns a {@link TextMatcher} used to identify the lines to replace.
		 * 
		 * @param instruction the processor instruction being executed
		 * @return the text matcher
		 */
		protected abstract TextMatcher getReplaceMatcher(ProcessorInstruction instruction);
		
		/**
		 * Returns the {@link Language} of the new code block output.
		 * 
		 * @param instruction the processor instruction being executed
		 * @return the language of the code block
		 */
		protected abstract Language getOutputLanguage(ProcessorInstruction instruction);
		
		/**
		 * Returns the {@link Language} of the referenced source file.  This is used to auto-detect the language based
		 * on the source file.
		 * 
		 * @param instruction the processor instruction being executed
		 * @return the language of the source file
		 */
		protected Language getSourceLanguage(ProcessorInstruction instruction) {
			return Language.fromExtension(FilenameUtils.getExtension(instruction.getOptions().getString("src")));
		}
		
		/**
		 * Replaces the content.
		 * 
		 * @param reader the reader for the original file
		 * @param writer the writer for the modified file
		 * @param newContent the new content
		 * @param instruction the processor instruction being executed
		 * @return {@code true} if the contents were changed; {@code false} otherwise
		 * @throws IOException if an I/O error occurred
		 */
		protected boolean replace(LineReader reader, PrintWriter writer, String newContent, ProcessorInstruction instruction) throws IOException {
			String oldContent = scanContentToReplace(reader, writer, instruction);
			
			boolean contentChanged = diff(oldContent, newContent);
			
			writer.write(newContent);
			writer.write(instruction.getLineSeparator());
			
			return contentChanged;
		}
		
		/**
		 * Locates a section of the file immediately following the current position that matches the pattern defined by
		 * {@link #getReplaceMatcher(ProcessorInstruction)}.  Any empty lines are ignored for matching but are written
		 * to the output to preserve whitespace.
		 * 
		 * @param reader the reader for the original file
		 * @param writer the writer for the modified file
		 * @return the old content that is being replaced
		 * @throws IOException if an I/O error occurred
		 */
		private String scanContentToReplace(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			StringBuilder sb = new StringBuilder();
			TextMatcher matcher = getReplaceMatcher(instruction);
			boolean inBlock = false;
			
			for (String line : reader) {
				if (!inBlock) {
					if (matcher.isStart(line)) {
						sb.append(line);
						sb.append(instruction.getLineSeparator());
						
						if (!matcher.isBlock() && matcher.isEnd(line)) {
							return sb.toString();
						}
						
						inBlock = true;
					} else if (line.isBlank()) {
						writer.write(line);
						writer.write(instruction.getLineSeparator());
					} else {
						throw new IOException("Found non-empty line '" + line + "' when scanning for matching content");
					}
				} else {
					sb.append(line);
					sb.append(instruction.getLineSeparator());
					
					if (matcher.isEnd(line)) {
						return sb.toString();
					}
				}
			}
			
			throw new IOException("Reached end of file before finding matching content");
		}
		
		/**
		 * Determines if any differences exist between the two strings, displaying any differences in the terminal.
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
	
	/**
	 * Processor that copies and formats source code.
	 */
	static class CodeProcessor extends Processor {
		
		/**
		 * Caches the content of the files / resources that are accessed by this tool.
		 */
		private final Map<String, String> cache;
		
		public CodeProcessor() {
			super();
			cache = new HashMap<>();
		}
		
		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			return replace(reader, writer, instruction.formatCode(loadContent(instruction)), instruction);
		}
		
		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			return instruction.getFileType().getCodeBlockMatcher();
		}
		
		@Override
		protected Language getOutputLanguage(ProcessorInstruction instruction) {
			if (instruction.getOptions().contains("language")) {
				return instruction.getOptions().getEnum("language", Language.class);
			} else {
				return getSourceLanguage(instruction);
			}
		}
		
		/**
		 * Loads content from a file or URL (restricted to GitHub).
		 * 
		 * @param instructions the processor instruction
		 * @return the content
		 * @throws IOException if an error occurred loading the file or URL
		 */
		private String loadContent(ProcessorInstruction instruction) throws IOException {
			String source = instruction.getOptions().getString("src");
			
			if (cache.containsKey(source)) {
				return cache.get(source);
			}
			
			URI uri = null;
			
			try {
				uri = URI.create(source);
			} catch (IllegalArgumentException e) {
				uri = null;
			}
			
			if (uri == null || uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("file")) {
				String content = Files.readString(Path.of(source), StandardCharsets.UTF_8);
				cache.put(source, content);
				return content;
			} else if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
				if (!uri.getHost().equalsIgnoreCase("raw.githubusercontent.com") ||
						!uri.getPath().startsWith("/MOEAFramework/")) {
					throw new IOException("Invalid path '" + source + "', unsupport host or path");
				}
				
				String content = IOUtils.toString(uri.toURL(), StandardCharsets.UTF_8);
				cache.put(source, content);
				return content;
			} else {
				throw new IOException("Invalid path '" + source + "', unsupported scheme");
			}
		}
		
	}
	
	/**
	 * Processor that compiles and executes a Java file.
	 */
	static class ExecProcessor extends Processor {
		
		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			compile(instruction);
			return replace(reader, writer, instruction.formatCode(execute(instruction)), instruction);
		}
		
		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			return instruction.getFileType().getCodeBlockMatcher();
		}
		
		@Override
		protected Language getOutputLanguage(ProcessorInstruction instruction) {
			return instruction.getOptions().getEnum("language", Language.class, Language.Text);
		}
		
		/**
		 * Compiles the Java class in the context of the current Java VM.
		 * 
		 * @param instruction the instruction being processed
		 * @throws IOException if an I/O error occurred
		 */
		protected void compile(ProcessorInstruction instruction) throws IOException {
			String source = instruction.getOptions().getString("src");
			String extension = FilenameUtils.getExtension(source);
			
			if (!extension.equalsIgnoreCase("java")) {
				Validate.that("file extension", extension).failUnsupportedOption("java");
			}
			
			Path javaPath = Path.of(source);
			Path classPath = javaPath.getParent().resolve(
					FilenameUtils.removeExtension(javaPath.getFileName().toString()) + ".class");

			if (instruction.isClean() || !Files.exists(classPath) || FileUtils.isFileNewer(javaPath.toFile(), classPath.toFile())) {
				FileUtils.deleteQuietly(classPath.toFile());

				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
								
				if (compiler.run(null, null, null, javaPath.toAbsolutePath().toString()) != 0) {
					throw new IOException("Failed to compile " + javaPath);
				}
			}
		}
		
		/**
		 * Executes a Java program and captures the output.  An optional method can be provided referencing a static
		 * or instance method that is compatible with the {@link Runnable} functional interface.  An instance of the
		 * class will be created using its no-arg constructor if required.
		 * 
		 * @param instruction the instruction being processed
		 * @return the standard output produced by the program
		 * @throws IOException if an I/O error occurred
		 */
		protected String execute(ProcessorInstruction instruction) throws IOException {
			PrintStream oldOut = System.out;
			String source = instruction.getOptions().getString("src");
			String methodName = instruction.getOptions().getString("method", "main");
			
			PRNG.setSeed(instruction.getSeed());
						
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream newOut = new PrintStream(baos)) {
				System.setOut(newOut);
					
				Class<?> cls = Class.forName(getClassName(source), true, Thread.currentThread().getContextClassLoader());
				
				if (methodName.equals("main")) {
					Method method = cls.getDeclaredMethod(methodName, String[].class);
					String[] args = instruction.getOptions().getStringArray("args", new String[0]);

					if (CommandLineUtility.class.isAssignableFrom(cls)) {
						Settings.PROPERTIES.setString(Settings.KEY_CLI_EXECUTABALE, "./cli " + cls.getSimpleName());
					}
					
					method.invoke(null, (Object)args);
				} else {
					Method method = cls.getDeclaredMethod(methodName);
					Object instance = Modifier.isStatic(method.getModifiers()) ? null : cls.getConstructor().newInstance();
					
					method.invoke(instance);
				}				

				newOut.close();
				return baos.toString();
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException |
					 InstantiationException | IllegalArgumentException e) {
				throw new IOException("Failed to execute method " + methodName + " in " + getClassName(source), e);
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof RuntimeException re) {
					throw re;
				}
				throw new IOException("Failed during execution of method " + methodName + " in " + getClassName(source), e.getCause());
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
	
	/**
	 * Processor that compiles and executes a Java program that produces a plot, saving the plot as an image.
	 */
	static class PlotProcessor extends ExecProcessor {
		
		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			try {
				compile(instruction);
				execute(instruction);
				return replace(reader, writer, instruction.formatImage(capturePlot(instruction)), instruction);
			} catch (HeadlessException e) {
				System.out.println("    > Skipping, requires graphical display");
				return false;
			}
		}
		
		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			return instruction.getFileType().getImageMatcher();
		}
		
		private String capturePlot(ProcessorInstruction instruction) throws IOException {
			File baseDirectory = instruction.getTemplateFile().getParentFile();
			String dest = instruction.getOptions().getString("dest");
			File file = new File(baseDirectory, dest);
			
			try {
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
			} catch (InvocationTargetException | InterruptedException e) {
				throw new IOException("Failed to capture plot", e);
			}
			
			return dest;
		}
		
	}
	
	/**
	 * The language of the source code.
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
		final String[] extensions;
		
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
		
		/**
		 * Returns a text matcher that identifies code snippets by their id.
		 * 
		 * @param id the identifier
		 * @return the text matcher
		 */
		public TextMatcher getSnippetMatcher(String id) {
			return switch (this) {
				case Java, C -> new TextMatcher(
						s -> StringUtils.containsIgnoreCase(s, "// begin-example: " + id),
						s -> StringUtils.containsIgnoreCase(s, "// end-example: " + id),
						true);
				case Text, Bash -> new TextMatcher(
						s -> StringUtils.containsIgnoreCase(s, "# begin-example: " + id),
						s -> StringUtils.containsIgnoreCase(s, "# end-example: " + id),
						true);
			};
		}
		
	}
	
	/**
	 * Supported file types that are processed by this utility.  This defines how code blocks are identified and
	 * formatted for a particular input file type.
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
		
		private static final Pattern REGEX = Pattern.compile("<!--\\s+\\:([a-zA-Z]+)\\:\\s+(.*)\\s+-->");
		
		/**
		 * The file extensions for the file type.
		 */
		final String[] extensions;
		
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
		 * Determines if the current line represents a processor instruction and, if so, parses it.
		 * 
		 * @param line the current line
		 * @return the processor instruction, or {@code null} if the line is not an instruction
		 * @throws IOException if an I/O error occurred
		 */
		public ProcessorInstruction tryParseProcessorInstruction(String line) throws IOException {
			Matcher matcher = REGEX.matcher(line);
			
			if (matcher.matches()) {
				Processor processor = Processor.fromString(matcher.group(1));
				
				ProcessorInstruction instruction = new ProcessorInstruction(processor, this);
				instruction.parseOptions(matcher.group(2));
				
				return instruction;
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
		 * @param instruction the instruction being executed
		 * @return the formatted code block
		 */
		public String formatCodeBlock(List<String> lines, Language language, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			
			switch (this) {
				case Markdown -> {
					sb.append("```" + getBrush(language));
					sb.append(instruction.getLineSeparator());
					sb.append(String.join(instruction.getLineSeparator(), lines));
					sb.append(instruction.getLineSeparator());
					sb.append("```");
				}
				case Html -> {
					sb.append("<pre class=\"brush: " + getBrush(language) + "; toolbar: false;\">");
					sb.append(instruction.getLineSeparator());
					sb.append("<![CDATA[");
					sb.append(instruction.getLineSeparator());
					sb.append(String.join(instruction.getLineSeparator(), lines));
					sb.append(instruction.getLineSeparator());
					sb.append("]]>");
					sb.append(instruction.getLineSeparator());
					sb.append("</pre>");
				}
				default -> {
					sb.append(String.join(instruction.getLineSeparator(), lines));
				}
			}
			
			return sb.toString();
		}
		
		/**
		 * Wraps the image path with any image tags and formatting options.
		 * 
		 * @param path the image path, either relative to the source document or an absolute URL
		 * @param instruction the instruction being executed
		 * @return the formatted image
		 */
		public String formatImage(String path, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			String width = instruction.getOptions().getString("width", "100%");
			
			switch (this) {
				case Markdown -> {
					sb.append("<p align=\"center\">");
					sb.append(instruction.getLineSeparator());
					sb.append("\t<img src=\"" + path + "\" width=\"" + width + "\" />");
					sb.append(instruction.getLineSeparator());
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
	
	/**
	 * Text matcher used to identify tags or code blocks.
	 */
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
