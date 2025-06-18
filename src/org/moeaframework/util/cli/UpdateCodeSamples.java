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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.moeaframework.analysis.plot.PlotBuilder;
import org.moeaframework.analysis.plot.PlotBuilder.DisplayDriver;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.validate.Validate;

/**
 * Utility to update code samples and output found in various documentation files, including Markdown and HTML files,
 * by embedding special instructions within the files that are processed by this utility.  These instructions are
 * embedded as comments in the document, allowing the instruction to remain hidden when the document is rendered.  For
 * both Markdown and HTML, these comments are structured as follows:
 * <pre>{@code
 *   <!-- :<processor>: arg1 arg2 ... -->
 * }</pre>
 * The processor type, which is surrounded by colons (:), determines the kind of processing performed when the
 * instruction is encountered.  The following processors are available:
 * <ul>
 *   <li>{@code code} - Copies all or part of a source code file into a code block in the output
 *   <li>{@code exec} - Compiles and executes a Java file, copying all or part of the output to a code block
 *   <li>{@code plot} - Compiles and executes a Java file that creates plots using {@link PlotBuilder}, saving the
 *       rendered plot to an image file.
 * </ul>
 * The processor type is followed by some number of arguments.  These arguments are formatted as {@code key=value}
 * pairs.  Some useful arguments include:
 * <ul>
 *   <li>{@code src} - The source code file containing the code being displayed or executed.
 *   <li>{@code lines} - The line number ({@code lines=5}), range ({@code lines=5:10}), or splice ({@code lines=:-1})
 *       of the code being referenced.
 *   <li>{@code id} - Alternative to line numbers, referencing a block of code between {@code // begin-example: <id>}
 *       and {@code // end-example: <id>} comments.
 *   <li>{@code preserveComments}, {@code preserveIndentation}, {@code preserveTabs}, etc. - Formatting flags that
 *       determine how source code is displayed.  Since these are boolean-valued, the value can be omitted.
 * </ul>
 * These instructions are placed immediately before the block of text they will be updating.  For example:
 * <pre>{@code
 *   <!-- :code: src=examples/Example1.java lines=5:10 preserveComments -->
 *   
 *   ```java
 *   // The code block being synced by the instruction above
 *   ```
 * }</pre>
 * This utility can be run in validate-only mode or update mode.  In validate mode, any changes to the files will
 * result in an error.  This is useful in CI to validate the docs are up-to-date.  In update mode, the files are
 * updated with any changes.
 */
public class UpdateCodeSamples extends CommandLineUtility {
	
	private static final long DEFAULT_SEED = 123456;
	
	private static final String DEFAULT_LINE_SEPARATOR = "\n";
	
	private static final File[] DEFAULT_PATHS = new File[] {
			new File("docs/"),
			new File("website/"),
			new File("src/README.md.template") };
	
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
	 * The registered template file formatters.  The key is the file extension excluding any leading {@code "."}.
	 */
	private Map<String, FileFormatter> fileFormatters;
	
	/**
	 * The registered source code languages.  The key is the file extension excluding any leading {@code "."}.
	 */
	private Map<String, Language> languages;
	
	/**
	 * The registered processors.  The key is the name of the processor.
	 */
	private Map<String, Processor> processors;
	
	/**
	 * Creates a new instance of the command line utility to update code examples.
	 */
	public UpdateCodeSamples() {
		super();
		
		fileFormatters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		fileFormatters.put("md", new MarkdownFormatter());
		fileFormatters.put("html", new HtmlFormatter());
		fileFormatters.put("xslt", new HtmlFormatter());
		
		languages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		languages.put("sh", new ShellScript());
		languages.put("java", new Java());
		
		processors = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		processors.put("code", new CodeProcessor());
		processors.put("exec", new ExecProcessor());
		processors.put("plot", new PlotProcessor());
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
		options.addOption(Option.builder("d")
				.longOpt("disable")
				.hasArgs()
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
		
		if (commandLine.hasOption("disable")) {
			for (String value : commandLine.getOptionValues("disable")) {
				System.out.println("Disabling " + value);
				processors.put(value, new DisabledProcessor());
			}
		}
		
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
		
		FileFormatter fileFormatter = getFileFormatter(file);
		
		if (fileFormatter == null) {
			System.out.println("Skipping " + file + ", not a supported extension");
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
				
				ProcessorInstruction instruction = fileFormatter.tryParseProcessorInstruction(line);
								
				if (instruction != null) {
					instruction.setLineSeparator(lineSeparator);
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
	 * Returns the matching file formatter from the given file based on its extension.
	 * 
	 * @param file the file
	 * @return the matching file formatter, or {@code null} if no match found
	 */
	protected FileFormatter getFileFormatter(File file) {
		String fileExtension = FilenameUtils.getExtension(file.getName());
			
		if (fileExtension.equalsIgnoreCase("template")) {
			fileExtension = FilenameUtils.getExtension(
					file.getName().substring(0, file.getName().length() - fileExtension.length() - 1));
		}
		
		return fileFormatters.get(fileExtension);
	}
	
	/**
	 * Returns the matching source language from the given file based on its extension.
	 * 
	 * @param file the file
	 * @return the matching source language
	 */
	protected Language getLanguage(File file) {
		return getLanguageForExtension(FilenameUtils.getExtension(file.getName()));
	}
	
	/**
	 * Returns the matching source language from the given file extension.
	 * 
	 * @param extension the file extension
	 * @return the matching source language
	 */
	protected Language getLanguageForExtension(String extension) {
		Language language = languages.get(extension);
		
		if (language == null) {
			language = new Plaintext();
		}
		
		return language;
	}
	
	/**
	 * Returns the matching processor from its name.
	 * 
	 * @param value the name of the processor
	 * @return the matching processor
	 * @throws IllegalArgumentException if the processor is not supported
	 */
	protected Processor getProcessor(String value) {
		Processor processor = processors.get(value);
		
		if (processor == null) {
			return Validate.that("value", value).failUnsupportedOption(processors.keySet());
		}
		
		return processor;
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
	
	/**
	 * Class representing a processor instruction, which defines the type of processor and any arguments.
	 */
	class ProcessorInstruction {
		
		/**
		 * The configured processor.
		 */
		private final Processor processor;

		/**
		 * The file formatter that specifies how content is searched and rendered in the template file.
		 */
		private final FileFormatter fileFormatter;
		
		/**
		 * The path of the template file.
		 */
		private File templateFile;
		
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
		public ProcessorInstruction(Processor processor, FileFormatter fileFormatter) {
			super();
			this.processor = processor;
			this.fileFormatter = fileFormatter;
			
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
		
		/**
		 * Returns the template file formatter.
		 * 
		 * @return the template file formatter
		 */
		public FileFormatter getFileFormatter() {
			return fileFormatter;
		}
		
		/**
		 * Returns the seed configured with this instruction or, if unset, the default seed.
		 * 
		 * @return the seed
		 */
		public long getSeed() {
			return options.getLong("seed", seed);
		}

		/**
		 * Returns the template file, which is the file currently being processed and contained this instruction.
		 * 
		 * @return the template file
		 */
		public File getTemplateFile() {
			return templateFile;
		}
		
		/**
		 * Sets the template file.
		 * 
		 * @param templateFile the template file
		 */
		public void setTemplateFile(File templateFile) {
			Validate.that("templateFile", templateFile).isNotNull();
			this.templateFile = templateFile;
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
			Validate.that("lineSeparator", lineSeparator).isNotNull();
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
		 * Returns the source file.
		 * 
		 * @return the source file
		 */
		protected File getSourceFile() {
			return new File(options.getString("src"));
		}
		
		/**
		 * Returns the {@link Language} of the source file, as determined by the file extension.
		 * 
		 * @return the language of the source file
		 */
		protected Language getSourceFileLanguage() {
			return getLanguage(getSourceFile());
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
		 * Formats the given code block in the style required by the template file type.
		 * 
		 * @param content the content to format
		 * @return the formatted content
		 * @throws IOException if an I/O error occurred
		 */
		public String formatCode(String content) throws IOException {
			List<String> lines = new ArrayList<>(content.lines().toList());
			
			if (options.contains("id")) {
				String identifier = options.getString("id");
				TextMatcher matcher = getSourceFileLanguage().getSnippetMatcher(identifier);
				
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
			
			return fileFormatter.formatCodeBlock(lines, language, this);
		}
		
		/**
		 * Formats the given image path or URL in the style required by the template file type.
		 * 
		 * @param path the image path
		 * @return the formatted image
		 */
		public String formatImage(String path) {
			return fileFormatter.formatImage(path, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append(processor.getClass().getSimpleName());
			sb.append(":");
			
			for (String key : options.keySet()) {
				sb.append(" ");
				sb.append(key + "=" + options.getString(key));
			}
			
			return sb.toString();
		}
		
	}
	
	/**
	 * Base class for defining a processor that responds to a particular instruction.
	 */
	abstract class Processor {

		Processor() {
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
		 * Returns the {@link Language} of the generated code.  This typically matches
		 * {@link #getSourceFileLanguage(ProcessorInstruction)} but can be overridden.
		 * 
		 * @param instruction the processor instruction being executed
		 * @return the language of the generated code
		 */
		protected abstract Language getOutputLanguage(ProcessorInstruction instruction);
		
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
		private String scanContentToReplace(LineReader reader, PrintWriter writer, ProcessorInstruction instruction)
				throws IOException {
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
	class CodeProcessor extends Processor {
		
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
			return instruction.getFileFormatter().getCodeBlockMatcher();
		}
		
		@Override
		protected Language getOutputLanguage(ProcessorInstruction instruction) {
			if (instruction.getOptions().contains("language")) {
				return getLanguageForExtension(instruction.getOptions().getString("language"));
			} else {
				return instruction.getSourceFileLanguage();
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
	class ExecProcessor extends Processor {
		
		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			return replace(reader, writer, instruction.formatCode(execute(instruction)), instruction);
		}
		
		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			return instruction.getFileFormatter().getCodeBlockMatcher();
		}
		
		@Override
		protected Language getOutputLanguage(ProcessorInstruction instruction) {
			return new Plaintext();
		}
		
		/**
		 * Compiles and executes the source file.
		 * 
		 * @param instruction the processor instruction defining the execution
		 * @return the output from the execution
		 * @throws IOException if an I/O error occurred
		 */
		protected String execute(ProcessorInstruction instruction) throws IOException {
			Language language = instruction.getSourceFileLanguage();
			return language.execute(instruction);
		}
		
	}
	
	/**
	 * Processor that compiles and executes a Java program that produces a plot, saving the plot as an image.
	 */
	class PlotProcessor extends ExecProcessor {
		
		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {			
			DisplayDriver oldDisplayDriver = PlotBuilder.getDisplayDriver();
			
			try {
				// Set up a display driver to save the plot to an image file
				String dest = instruction.getOptions().getString("dest");				
				File tempFile = File.createTempFile("temp", "." + FilenameUtils.getExtension(dest));
								
				PlotBuilder.setDisplayDriver(new DisplayDriver() {
	
					@Override
					public void show(PlotBuilder<?> builder, int width, int height) {
						try {
							builder.save(tempFile, width, height);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					}
					
				});
				
				// Compile and execute the plotting code
				execute(instruction);
				
				// Detect if the plot changed
				File baseDirectory = instruction.getTemplateFile().getParentFile();
				File destFile = new File(baseDirectory, dest);
				
				boolean contentChanged = !destFile.exists() || Files.mismatch(tempFile.toPath(), destFile.toPath()) >= 0;
				
				if (contentChanged) {
					System.out.println("      ! Plot '" + dest + "' changed!");
					
					if (update) {
						Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} else {
						Files.deleteIfExists(tempFile.toPath());
					}					
				}
				
				contentChanged |= replace(reader, writer, instruction.formatImage(dest), instruction);
				return contentChanged;
			} finally {
				PlotBuilder.setDisplayDriver(oldDisplayDriver);
			}
		}
		
		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			return instruction.getFileFormatter().getImageMatcher();
		}
		
	}
	
	/**
	 * Processor that no-ops.
	 */
	class DisabledProcessor extends Processor {

		@Override
		public boolean run(LineReader reader, PrintWriter writer, ProcessorInstruction instruction) throws IOException {
			return false;
		}

		@Override
		protected TextMatcher getReplaceMatcher(ProcessorInstruction instruction) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected Language getOutputLanguage(ProcessorInstruction instruction) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * Base class for implementing different programming languages.
	 */
	abstract class Language {
		
		Language() {
			super();
		}
		
		/**
		 * Strips comments out of the code block.  This also removes any duplicate blank lines that may result
		 * when removing the comments.
		 * 
		 * @param content the code block
		 * @return the updated code block
		 */
		public abstract String stripComments(String content);
		
		/**
		 * Returns a text matcher that locates code examples referenced by id.
		 * 
		 * @param id the code example id
		 * @return the text matcher
		 */
		public abstract TextMatcher getSnippetMatcher(String id);
		
		/**
		 * Attempts to compile and execute the referenced code.
		 * 
		 * @param instruction the processor instruction
		 * @return the output from executing the code
		 * @throws IOException if an I/O error occurred
		 */
		public String execute(ProcessorInstruction instruction) throws IOException {
			return Validate.fail("Execution not supported for " + getClass().getSimpleName());
		}
		
		/**
		 * Splits the content in a list of lines.  The resulting list is mutable.
		 * 
		 * @param content the content
		 * @return the lines
		 */
		public List<String> split(String content) {
			return new ArrayList<>(content.lines().toList());
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
			
			return stripLeadingAndTrailingBlankLines(split(content));
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
			return split(content);
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
		
	}
	
	/**
	 * Handles plain text or content without a defined programming language.
	 */
	class Plaintext extends Language {

		@Override
		public String stripComments(String content) {
			return content;
		}
		
		@Override
		public TextMatcher getSnippetMatcher(String id) {
			return new TextMatcher(
					s -> StringUtils.containsIgnoreCase(s, "# begin-example: " + id),
					s -> StringUtils.containsIgnoreCase(s, "# end-example: " + id),
					true);
		}
		
	}
	
	/**
	 * Handles shell scripts.
	 */
	class ShellScript extends Plaintext {
		
	}
	
	/**
	 * Handles Java source code.
	 */
	class Java extends Language {
		
		@Override
		public String execute(ProcessorInstruction instruction) throws IOException {
			File sourceFile = instruction.getSourceFile();
			File classFile = new File(sourceFile.getParent(), FilenameUtils.removeExtension(sourceFile.getName()) + ".class");
			String className = getClassName(sourceFile.getPath());

			if (clean || !classFile.exists() || FileUtils.isFileNewer(sourceFile, classFile)) {
				FileUtils.deleteQuietly(classFile);

				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
								
				if (compiler.run(null, null, null, sourceFile.getAbsolutePath()) != 0) {
					throw new IOException("Failed to compile " + sourceFile);
				}
			}
			
			PrintStream oldOut = System.out;
			String methodName = instruction.getOptions().getString("method", "main");
			
			PRNG.setSeed(instruction.getSeed());
						
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream newOut = new PrintStream(baos)) {
				System.setOut(newOut);
					
				Class<?> cls = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
				
				if (methodName.equals("main")) {
					Method method = cls.getDeclaredMethod(methodName, String[].class);
					String[] args = instruction.getOptions().getStringArray("args", new String[0]);

					if (CommandLineUtility.class.isAssignableFrom(cls)) {
						Settings.PROPERTIES.setString(Settings.KEY_CLI_EXECUTABALE, "./cli " + cls.getSimpleName());
					}
					
					method.invoke(null, (Object)args);
				} else {
					if (instruction.getOptions().contains("args")) {
						Validate.fail("Arguments are only supported on main methods");
					}
					
					Method method = cls.getDeclaredMethod(methodName);
					Object instance = Modifier.isStatic(method.getModifiers()) ? null : cls.getConstructor().newInstance();
					
					method.invoke(instance);
				}				

				newOut.close();
				return baos.toString();
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException |
					 InstantiationException | IllegalArgumentException e) {
				throw new IOException("Failed to execute method " + methodName + " in " + className, e);
			} catch (InvocationTargetException e) {
				throw new IOException("Failed during execution of method " + methodName + " in " + className, e.getCause());
			} finally {
				System.setOut(oldOut);
			}
		}
		
		@Override
		public String stripComments(String content) {
			// Remove C-style // comments
			content = content.replaceAll("(?<=[\\r\\n])[\\t ]*\\/\\/[^\\n]*\\r?\\n", "");
			content = content.replaceAll("[\\t ]*\\/\\/[^\\n]*", "");
			
			// Remove C-style /* */ and Javadoc /** */ comments
			content = content.replaceAll("(?<=[\\r\\n])[\\t ]*\\/\\*[^*]*\\*+(?:[^\\/*][^*]*\\*+)*\\/[\\t ]*\\r?\\n", "");
			content = content.replaceAll("[\\t ]*\\/\\*[^*]*\\*+(?:[^\\/*][^*]*\\*+)*\\/", "");
			
			return content;
		}
		
		@Override
		public TextMatcher getSnippetMatcher(String id) {
			return new TextMatcher(
					s -> s.equalsIgnoreCase("// begin-example: " + id),
					s -> s.equalsIgnoreCase("// end-example: " + id),
					true);
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
	 * Formatter for a specific file type that determines how content is searched and rendered.
	 */
	abstract class FileFormatter {

		/**
		 * The regular expression used to match processor instructions.
		 */
		private static final Pattern REGEX = Pattern.compile("<!--\\s+\\:([a-zA-Z]+)\\:\\s+(.*)\\s+-->");
		
		/**
		 * Constructs a new template file type with the given extensions.
		 * 
		 * @param extensions the extensions
		 */
		FileFormatter() {
			super();
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
				Processor processor = getProcessor(matcher.group(1));
				
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
		public abstract TextMatcher getCodeBlockMatcher();
		
		/**
		 * Returns the text matcher for images.
		 * 
		 * @return the text matcher
		 */
		public abstract TextMatcher getImageMatcher();
		
		/**
		 * Returns the name of the brush used for syntax highlighting the given source language.
		 * 
		 * @param language the source language
		 * @return the brush name
		 */
		public abstract String getBrush(Language language);
		
		/**
		 * Renders the code block in the format required by this template file type.  The returned content must match
		 * the pattern defined by {@link #getCodeBlockMatcher()}.
		 * 
		 * @param lines the code block
		 * @param instruction the instruction being executed
		 * @return the formatted code block
		 */
		public abstract String formatCodeBlock(List<String> lines, Language language, ProcessorInstruction instruction);
		
		/**
		 * Wraps the image path or URL with any image tags and formatting options.  The returned content must match
		 * the pattern defined by {@link #getImageMatcher()}.
		 * 
		 * @param path the image path, either relative to the source document or an absolute URL
		 * @param instruction the instruction being executed
		 * @return the formatted image
		 */
		public abstract String formatImage(String path, ProcessorInstruction instruction);
	}
	
	/**
	 * Formatter for Markdown files.
	 * <p>
	 * Note that we prefer using embedded HTML tags instead of {@code ![alt_text](image_url)} as it provides better
	 * control over the image scale and alignment.
	 */
	class MarkdownFormatter extends FileFormatter {

		MarkdownFormatter() {
			super();
		}
		
		public TextMatcher getCodeBlockMatcher() {
			return new TextMatcher(s -> s.startsWith("```"), s -> s.endsWith("```"), true);
		}
		
		public TextMatcher getImageMatcher() {
			return new TextMatcher(s -> s.startsWith("<p align=\"center\">"), s -> s.endsWith("</p>"), false);
		}
		
		@Override
		public String getBrush(Language language) {
			if (language instanceof Java) {
				return "java";
			} else if (language instanceof ShellScript) {
				return "sh";
			} else {
				return "";
			}
		}
		
		@Override
		public String formatCodeBlock(List<String> lines, Language language, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("```");
			sb.append(getBrush(language));
			sb.append(instruction.getLineSeparator());
			sb.append(String.join(instruction.getLineSeparator(), lines));
			sb.append(instruction.getLineSeparator());
			sb.append("```");

			return sb.toString();
		}
		
		@Override
		public String formatImage(String path, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<p align=\"center\">");
			sb.append(instruction.getLineSeparator());
			sb.append("\t<img src=\"" + path + "\"");
			
			if (instruction.getOptions().contains("width")) {
				sb.append(" width=\"" + instruction.getOptions().getString("width") + "\"");
			}
			
			sb.append(" />");
			sb.append(instruction.getLineSeparator());
			sb.append("</p>");

			return sb.toString();
		}
		
	}
	
	/**
	 * Formatter for HTML or XSLT files.
	 */
	class HtmlFormatter extends FileFormatter {

		HtmlFormatter() {
			super();
		}

		@Override
		public TextMatcher getCodeBlockMatcher() {
			return new TextMatcher(s -> s.startsWith("<pre"), s -> s.endsWith("</pre>"), true);
		}
		
		@Override
		public TextMatcher getImageMatcher() {
			return new TextMatcher(s -> s.startsWith("<img"), s -> s.endsWith("/>"), false);
		}
		
		@Override
		public String getBrush(Language language) {
			if (language instanceof Java) {
				return "java";
			} else {
				return "plain";
			}
		}
		
		@Override
		public String formatCodeBlock(List<String> lines, Language language, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("<pre class=\"brush: ");
			sb.append(getBrush(language));
			sb.append("; toolbar: false;\">");
			sb.append(instruction.getLineSeparator());
			sb.append("<![CDATA[");
			sb.append(instruction.getLineSeparator());
			sb.append(String.join(instruction.getLineSeparator(), lines));
			sb.append(instruction.getLineSeparator());
			sb.append("]]>");
			sb.append(instruction.getLineSeparator());
			sb.append("</pre>");
			
			return sb.toString();
		}
		
		@Override
		public String formatImage(String path, ProcessorInstruction instruction) {
			StringBuilder sb = new StringBuilder();
			sb.append("<img src=\"" + path + "\"");
			
			if (instruction.getOptions().contains("width")) {
				sb.append(" width=\"" + instruction.getOptions().getString("width") + "\"");
			}
			
			sb.append(" />");	
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
			return startMatcher.test(line.trim());
		}

		public boolean isEnd(String line) {
			return endMatcher.test(line.trim());
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
