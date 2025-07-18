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
package org.moeaframework.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.moeaframework.analysis.plot.PlotBuilder;
import org.moeaframework.analysis.plot.PlotBuilder.DisplayDriver;
import org.moeaframework.core.Copyable;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.JavaBuilder;
import org.moeaframework.util.ReflectionUtils;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.format.Displayable;
import org.moeaframework.util.validate.Validate;

/**
 * Utility to update code samples and output found in various documentation files, in particular Markdown, by embedding
 * special instructions within the files that are processed by this utility.  These instructions are embedded as
 * comments in the document, allowing the instruction to remain hidden when the document is rendered:
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
 *   <li>{@code lines} - The line number ({@code lines=5}), range ({@code lines=5:10}), or slice ({@code lines=:-1})
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
	
	private static final File DEFAULT_BUILD_PATH = new File("build/");
	
	private static final File[] DEFAULT_SOURCE_PATHS = new File[] {
			new File("src/"),
			new File("test/"),
			new File("examples/") };
		
	private static final File[] DEFAULT_ARGUMENTS = new File[] {
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
	 * The destination path for compiled files (i.e., class files for Java).
	 */
	private File buildPath;
	
	/**
	 * List of source paths that are included when compiling Java files.
	 */
	private List<File> sourcePath;
	
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
		options.addOption(Option.builder()
				.longOpt("buildPath")
				.hasArg()
				.build());
		options.addOption(Option.builder()
				.longOpt("sourcePath")
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
		
		if (commandLine.hasOption("disable")) {
			for (String value : commandLine.getOptionValues("disable")) {
				System.out.println("Disabling " + value);
				processors.put(value, new DisabledProcessor());
			}
		}
		
		if (commandLine.hasOption("buildPath")) {
			buildPath = new File(commandLine.getOptionValue("buildPath"));
		} else {
			buildPath = DEFAULT_BUILD_PATH;
		}
		
		sourcePath = new ArrayList<>(List.of(DEFAULT_SOURCE_PATHS));
		
		if (commandLine.hasOption("sourcePath")) {
			for (String arg : commandLine.getOptionValue("sourcePath").split(Pattern.quote(File.pathSeparator))) {
				sourcePath.add(new File(arg));
			}
		}
		
		Settings.PROPERTIES.setInt(Settings.KEY_HELP_WIDTH, 120);
		
		boolean fileChanged = false;

		if (commandLine.getArgs().length == 0) {
			for (File path : DEFAULT_ARGUMENTS) {
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
		
		Document document = new Document(file);
		int currentLine = 1;
		
		while (currentLine <= document.size()) {
			ProcessorInstruction instruction = fileFormatter.tryParseProcessorInstruction(file, document, currentLine);
						
			if (instruction != null) {
				System.out.println("    > Running " + instruction);
				fileChanged |= instruction.run(document);
			}
			
			currentLine += 1;
		}
		
		if (fileChanged && update) {
			document.save(file);
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
			fileExtension = FilenameUtils.getExtension(FilenameUtils.removeExtension(file.getName()));
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
	 * Class representing a processor instruction, which defines the type of processor and any arguments.
	 */
	class ProcessorInstruction {
		
		private final Processor processor;

		private final FileFormatter fileFormatter;

		private final File templateFile;

		private final int lineNumber;

		private final TypedProperties options;

		public ProcessorInstruction(Processor processor, FileFormatter fileFormatter, File templateFile, int lineNumber) {
			super();
			this.processor = processor;
			this.fileFormatter = fileFormatter;
			this.templateFile = templateFile;
			this.lineNumber = lineNumber;
			
			options = new TypedProperties();
		}
		
		protected void parseOptions(String str) {
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
						throw new ParsingException(lineNumber, "Failed to parse instruction, encountered unexpected character '" +
								(char)tokenizer.ttype + "'");
					}
					
					String key = tokenizer.sval;
					
					tokenizer.nextToken();
									
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_EOF) {
						options.setBoolean(key, true);
						tokenizer.pushBack();
						continue;
					} else if (tokenizer.ttype != '=') {
						throw new ParsingException(lineNumber, "Failed to parse instruction, expected '=' after key");
					}
									
					tokenizer.nextToken();
					
					if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == '"' || tokenizer.ttype == '\'') {
						options.setString(key, tokenizer.sval);
					} else {
						throw new ParsingException(lineNumber, "Failed to parse instruction, expected value after '='");
					}
				}
			} catch (IOException e) {
				throw new ParsingException(lineNumber, "Failed to parse instruction", e);
			}
		}
		
		public FileFormatter getFileFormatter() {
			return fileFormatter;
		}
		
		public long getSeed() {
			return options.getLong("seed", seed);
		}

		public File getTemplateFile() {
			return templateFile;
		}
		
		public int getLineNumber() {
			return lineNumber;
		}
		
		public TypedProperties getOptions() {
			return options;
		}
		
		protected File getSourceFile() {
			return new File(options.getString("src"));
		}
		
		protected Language getSourceFileLanguage() {
			return getLanguage(getSourceFile());
		}
		
		public boolean run(Document document) throws IOException {
			return processor.run(document, this);
		}

		public void formatCode(Document document) {
			if (options.contains("id")) {
				String identifier = options.getString("id");
				TextMatcher matcher = getSourceFileLanguage().getSnippetMatcher(identifier);
				Slice slice = matcher.scan(1, document);
				
				if (slice == null) {
					throw new ParsingException(lineNumber, "Failed to find code snippet with id '" + identifier + "'");
				}
				
				document.retain(new Slice(slice.getStart() + 1, slice.getEnd() - 1));
			} else if (options.contains("method")) {
				String methodName = options.getString("method");
				TextMatcher matcher = getSourceFileLanguage().getMethodMatcher(methodName);
				Slice slice = matcher.scan(1, document);
				
				if (slice == null) {
					throw new ParsingException(lineNumber, "Failed to find method named '" + methodName + "'");
				}
				
				document.retain(new Slice(slice.getStart() + 1, slice.getEnd() - 1));
			} else {
				Slice slice = Slice.fromString(options.getString("lines", ":"));
				document.retain(slice);
			}
			
			// Apply any formatting specific to the output source code language
			Language language = processor.getOutputLanguage(this);
						
			if (!options.getBoolean("preserveComments", false)) {
				document.transform(language::stripComments);
				document.removeLeadingAndTrailingBlankLines();
			}

			if (!options.getBoolean("preserveIndentation", false)) {
				document.removeIndentation();
			}
			
			if (!options.getBoolean("preserveTabs", false)) {
				document.replaceTabsWithSpaces();
			}
			
			if (options.getBoolean("showEllipsis", false)) {
				document.append("...");
			}
			
			fileFormatter.formatCodeBlock(document, language, this);
		}

		public Document formatImage(String path) {
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
	interface Processor {
		
		/**
		 * Runs the processor.
		 * 
		 * @param document the document being processed
		 * @param instruction the processor instruction being executed
		 * @return {@code true} if the content changed
		 * @throws IOException if an I/O error occurred
		 */
		public boolean run(Document document, ProcessorInstruction instruction) throws IOException;
		
		/**
		 * Returns the {@link Language} of the generated code.  This typically matches
		 * {@link #getSourceFileLanguage(ProcessorInstruction)} but can be overridden.
		 * 
		 * @param instruction the processor instruction being executed
		 * @return the language of the generated code
		 */
		public Language getOutputLanguage(ProcessorInstruction instruction);
		
	}
	
	/**
	 * Processor that copies and formats source code.
	 */
	class CodeProcessor implements Processor {
		
		private final Map<String, Document> cache;
		
		public CodeProcessor() {
			super();
			cache = new HashMap<>();
		}
		
		@Override
		public boolean run(Document document, ProcessorInstruction instruction) throws IOException {
			Document newContent = loadSource(instruction);
			instruction.formatCode(newContent);
			
			TextMatcher matcher = instruction.getFileFormatter().getCodeBlockMatcher();
			Slice slice = matcher.scan(instruction.getLineNumber() + 1, document);
			
			if (slice == null) {
				throw new ParsingException(instruction.getLineNumber(), "No code block found following the instruction");
			}
			
			for (int i = instruction.getLineNumber() + 1; i < slice.getStart(); i++) {
				if (!document.get(i).isBlank()) {
					throw new ParsingException(i, "Found non-empty line when code block expected");
				}
			}

			Document oldContent = document.extract(slice);
			boolean changed = oldContent.diff(newContent, System.out);
			
			document.replace(slice, newContent);			
			return changed;
		}
		
		@Override
		public Language getOutputLanguage(ProcessorInstruction instruction) {
			if (instruction.getOptions().contains("language")) {
				return getLanguageForExtension(instruction.getOptions().getString("language"));
			} else {
				return instruction.getSourceFileLanguage();
			}
		}
		
		private Document loadSource(ProcessorInstruction instruction) throws IOException {
			String source = instruction.getOptions().getString("src");
			
			if (cache.containsKey(source)) {
				return cache.get(source).copy();
			}
			
			URI uri = null;
			
			try {
				uri = URI.create(source);
			} catch (IllegalArgumentException e) {
				uri = null;
			}
			
			if (uri == null || uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("file")) {
				Document document = new Document(Files.readString(Path.of(source), StandardCharsets.UTF_8));
				cache.put(source, document);
				return document.copy();
			} else if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
				if (!uri.getHost().equalsIgnoreCase("raw.githubusercontent.com") ||
						!uri.getPath().startsWith("/MOEAFramework/")) {
					throw new IOException("Invalid path '" + source + "', unsupport host or path");
				}
				
				Document document = new Document(IOUtils.toString(uri.toURL(), StandardCharsets.UTF_8));
				cache.put(source, document);
				return document.copy();
			} else {
				throw new IOException("Invalid path '" + source + "', unsupported scheme");
			}
		}
		
	}
	
	/**
	 * Processor that compiles and executes a Java file.
	 */
	class ExecProcessor implements Processor {
		
		@Override
		public boolean run(Document document, ProcessorInstruction instruction) throws IOException {
			Document newContent = new Document(execute(instruction));
			instruction.formatCode(newContent);
						
			TextMatcher matcher = instruction.getFileFormatter().getCodeBlockMatcher();
			Slice slice = matcher.scan(instruction.getLineNumber() + 1, document);
			
			if (slice == null) {
				throw new ParsingException(instruction.getLineNumber(), "No code block found following the instruction");
			}
			
			for (int i = instruction.getLineNumber() + 1; i < slice.getStart(); i++) {
				if (!document.get(i).isBlank()) {
					throw new ParsingException(i, "Found non-empty line when code block expected");
				}
			}
			
			Document oldContent = document.extract(slice);
			boolean changed = oldContent.diff(newContent, System.out);
			
			document.replace(slice, newContent);			
			return changed;
		}
		
		@Override
		public Language getOutputLanguage(ProcessorInstruction instruction) {
			return new Plaintext();
		}
		
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
		public boolean run(Document document, ProcessorInstruction instruction) throws IOException {			
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
				
				boolean imageChanged = !destFile.exists() || Files.mismatch(tempFile.toPath(), destFile.toPath()) >= 0;
				
				if (imageChanged) {
					System.out.println("      ! Plot '" + dest + "' changed!");
					
					if (update) {
						Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} else {
						Files.deleteIfExists(tempFile.toPath());
					}					
				}
				
				// Update the image reference
				Document newContent = instruction.getFileFormatter().formatImage(dest, instruction);
				
				TextMatcher matcher = instruction.getFileFormatter().getImageMatcher();
				Slice slice = matcher.scan(instruction.getLineNumber() + 1, document);
				
				if (slice == null) {
					throw new ParsingException(instruction.getLineNumber(), "No image found following the instruction");
				}
				
				for (int i = instruction.getLineNumber() + 1; i < slice.getStart(); i++) {
					if (!document.get(i).isBlank()) {
						throw new ParsingException(i, "Found non-empty line when image expected");
					}
				}
				
				Document oldContent = document.extract(slice);
				boolean contentChanged = oldContent.diff(newContent, System.out);
				
				document.replace(slice, newContent);			
				return contentChanged || imageChanged;
			} finally {
				PlotBuilder.setDisplayDriver(oldDisplayDriver);
			}
		}
		
	}
	
	/**
	 * Processor that no-ops.
	 */
	class DisabledProcessor implements Processor {

		@Override
		public boolean run(Document document, ProcessorInstruction instruction) throws IOException {
			return false;
		}

		@Override
		public Language getOutputLanguage(ProcessorInstruction instruction) {
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
				
		public String stripComments(String content) {
			return content;
		}

		public TextMatcher getSnippetMatcher(String id) {
			throw new UnsupportedOperationException("Matching code snippets is not supported by " +
					getClass().getSimpleName());
		}
		
		public TextMatcher getMethodMatcher(String methodName) {
			throw new UnsupportedOperationException("Matching methods is not supported by " +
					getClass().getSimpleName());
		}
		
		public String execute(ProcessorInstruction instruction) throws IOException {
			throw new UnsupportedOperationException("Executing source code is not supported by " +
					getClass().getSimpleName());
		}
		
	}
	
	/**
	 * Handles plain text or content without a defined programming language.
	 */
	class Plaintext extends Language {

		@Override
		public TextMatcher getSnippetMatcher(String id) {
			return new BlockMatcher(
					s -> s.equalsIgnoreCase("# begin-example: " + id),
					s -> s.equalsIgnoreCase("# end-example: " + id));
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
			
			JavaBuilder builder = new JavaBuilder();
			builder.clean(clean);
			builder.buildPath(buildPath);
			builder.sourcePath(sourcePath.toArray(File[]::new));
			
			if (!builder.compile(sourceFile)) {
				throw new IOException("Failed to compile " + sourceFile);
			}
			
			String className = builder.getFullyQualifiedClassName(sourceFile);
			String methodName = instruction.getOptions().getString("method", "main");
			boolean isMain = methodName.equals("main");
			boolean isStatic = instruction.getOptions().getBoolean("static", isMain);
			
			PrintStream oldOut = System.out;
			PRNG.setSeed(instruction.getSeed());
						
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream newOut = new PrintStream(baos)) {
				System.setOut(newOut);

				Class<?> cls = Class.forName(className, true, builder.getClassLoader());
				
				String[] args = instruction.getOptions().getStringArray("args", new String[0]);
				
				if (CommandLineUtility.class.isAssignableFrom(cls)) {
					Settings.PROPERTIES.setString(Settings.KEY_CLI_EXECUTABALE, "./cli " + cls.getSimpleName());
				}
				
				if (isMain) {
					ReflectionUtils.invokeStaticMethod(cls, methodName, (Object)args);
				} else if (isStatic) {
					ReflectionUtils.invokeStaticMethod(cls, methodName, (Object[])args);
				} else {
					ReflectionUtils.invokeMethod(cls.getConstructor().newInstance(), methodName, (Object[])args);
				}

				newOut.close();
				return baos.toString();
			} catch (IllegalAccessException | InstantiationException e) {
				throw new IOException("Failed to create instance of class " + className + " in " + sourceFile, e);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalArgumentException e) {
				throw new IOException("Failed to execute method " + methodName + " in " + sourceFile, e);
			} catch (InvocationTargetException e) {
				throw new IOException("Failed during execution of method " + methodName + " in " + sourceFile, e.getCause());
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
			return new BlockMatcher(
					s -> s.equalsIgnoreCase("// begin-example: " + id),
					s -> s.equalsIgnoreCase("// end-example: " + id));
		}
		
		@Override
		public TextMatcher getMethodMatcher(String methodName) {
			return new MethodMatcher(methodName);
		}

	}
	
	/**
	 * Formatter for a specific file type that determines how content is searched and rendered.
	 */
	abstract class FileFormatter {

		private static final Pattern INSTRUCTION_REGEX = Pattern.compile("<!--\\s+\\:([a-zA-Z]+)\\:\\s+(.*)\\s+-->");
		
		FileFormatter() {
			super();
		}

		/**
		 * Determines if the current line represents a processor instruction and, if so, parses it.
		 * 
		 * @param file the file currently being processed
		 * @param document the content of the file
		 * @param lineNumber the current line number
		 * @return the processor instruction, or {@code null} if the line is not an instruction
		 */
		public ProcessorInstruction tryParseProcessorInstruction(File file, Document document, int lineNumber) {
			Matcher matcher = INSTRUCTION_REGEX.matcher(document.get(lineNumber));
			
			if (matcher.matches()) {
				Processor processor = getProcessor(matcher.group(1));
				
				ProcessorInstruction instruction = new ProcessorInstruction(processor, this, file, lineNumber);
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
		 * Renders the code block in the format required by this file type.
		 * 
		 * @param document the document representing the code block
		 * @param instruction the instruction being executed
		 */
		public abstract void formatCodeBlock(Document document, Language language, ProcessorInstruction instruction);
		
		/**
		 * Renders the image path or URL in the format required by this file type.
		 * 
		 * @param path the image path, either relative to the source document or an absolute URL
		 * @param instruction the instruction being executed
		 * @return the formatted image
		 */
		public abstract Document formatImage(String path, ProcessorInstruction instruction);
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
			return new BlockMatcher(s -> s.startsWith("```"), s -> s.endsWith("```"));
		}
		
		public TextMatcher getImageMatcher() {
			return new BlockMatcher(s -> s.startsWith("<p align=\"center\">"), s -> s.endsWith("</p>"));
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
		public void formatCodeBlock(Document document, Language language, ProcessorInstruction instruction) {
			document.prepend("```" + getBrush(language));
			document.append("```");
		}
		
		@Override
		public Document formatImage(String path, ProcessorInstruction instruction) {
			Document document = new Document();
			document.append("<p align=\"center\">");
			document.append("\t<img src=\"" + path + "\"" + (instruction.getOptions().contains("width") ?
						" width=\"" + instruction.getOptions().getString("width") + "\"" : "") + " />");
			document.append("</p>");
			return document;
		}
		
	}
	
	/**
	 * Matches a section of a document.
	 */
	interface TextMatcher {
		
		public Slice scan(int lineNumber, Document document);
		
	}
	
	/**
	 * Matches a block of text identified by starting and ending lines.
	 */
	static class BlockMatcher implements TextMatcher {
		
		private final Predicate<String> startPredicate;
		
		private final Predicate<String> endPredicate;
				
		public BlockMatcher(Predicate<String> startPredicate, Predicate<String> endPredicate) {
			super();
			this.startPredicate = startPredicate;
			this.endPredicate = endPredicate;
		}

		@Override
		public Slice scan(int lineNumber, Document document) {
			int matchStart = -1;
			
			while (lineNumber <= document.size()) {
				String line = document.get(lineNumber).trim();
								
				if (matchStart > 0) {	
					if (endPredicate.test(line)) {
						return new Slice(matchStart, lineNumber);
					}
				} else {		
					if (startPredicate.test(line)) {
						matchStart = lineNumber;
					}
				}
				
				lineNumber += 1;
			}
			
			if (matchStart < 0) {
				return null;
			} else {
				throw new ParsingException(matchStart, "Reached end of file scanning for end of block");
			}
		}
		
	}
	
	/**
	 * Matches a C or Java method, with some simplifying assumptions regarding how the method is formatted, especially
	 * with respect to opening and closing braces.
	 */
	static class MethodMatcher implements TextMatcher {
		
		private static final Pattern FUNCTION_REGEX = Pattern.compile("[\\w\\<\\>\\[\\]]+(?<!new)\\s+(\\w+)\\s*\\([^\\)]*\\)\\s*(\\{?|[^;])");
		
		private final String methodName;
				
		public MethodMatcher(String methodName) {
			super();
			this.methodName = methodName;
		}

		@Override
		public Slice scan(int lineNumber, Document document) {
			int matchStart = -1;
			int bracesLevel = 0;
			
			while (lineNumber <= document.size()) {
				String line = document.get(lineNumber).trim();
								
				if (matchStart > 0) {
					for (int i = 0; i < line.length(); i++) {
						if (line.charAt(i) == '{') {
							bracesLevel += 1;
						} else if (line.charAt(i) == '}') {
							bracesLevel -= 1;
						}
						
						if (bracesLevel == 0) {
							return new Slice(matchStart, lineNumber);
						}
					}
				} else {
					Matcher matcher = FUNCTION_REGEX.matcher(line);
					
					if (matcher.find() && matcher.group(1).equals(methodName)) {
						matchStart = lineNumber;
						
						if (matcher.group(2).equals("{")) {
							bracesLevel += 1;
						}
					}
				}
				
				lineNumber += 1;
			}
			
			if (matchStart < 0) {
				return null;
			} else {
				throw new ParsingException(matchStart, "Reached end of file scanning for end of method");
			}
		}
		
	}

	/**
	 * Represents a slice of an array, similar to the Python notation.  The two main differences are (1) the end index
	 * is inclusive, and (2) steps are not supported.
	 */
	static class Slice {
		
		private static final int UNDEFINED_START = 0;
		
		private static final int UNDEFINED_END = Integer.MAX_VALUE;
		
		private final int start;
		
		private final int end;
		
		public Slice(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}
		
		public int getStart() {
			return start;
		}
		
		public int getEnd() {
			return end;
		}
		
		public Slice resolve(Document document) {
			int resolvedStart = start;
			int resolvedEnd = end;
			
			if (resolvedStart == 0) {
				resolvedStart = 1;
			} else if (resolvedStart < 0) {
				resolvedStart += document.size() + 1;
			} else if (resolvedStart > document.size()) {
				resolvedStart = document.size();
			}
			
			if (start == end) {
				resolvedEnd = resolvedStart;
			} else if (resolvedEnd < 0) {
				resolvedEnd += document.size();
			} else if (resolvedEnd > document.size()) {
				resolvedEnd = document.size();
			}
			
			return new Slice(resolvedStart, resolvedEnd);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			if (start != UNDEFINED_START) {
				sb.append(start);
			}
			
			if (start != end) {
				sb.append(":");
				
				if (end != UNDEFINED_END) {
					sb.append(end);
				}
			}
			
			return sb.toString();
		}
		
		public static Slice fromString(String str) {
			String[] tokens = str.split(":", 2);
			int start = UNDEFINED_START;
			int end = UNDEFINED_END;
						
			if (!tokens[0].isBlank()) {
				start = Integer.parseInt(tokens[0]);
			}

			if (tokens.length > 1 && !tokens[1].isBlank()) {
				end = Integer.parseInt(tokens[1]);
			} else if (tokens.length == 1 && !tokens[0].isBlank()) {
				end = Integer.parseInt(tokens[0]);
			}
						
			return new Slice(start, end);
		}
		
	}
	
	/**
	 * Encapsulates a document that is being modified by this code.  Line numbers are 1-based.
	 */
	static class Document implements Copyable<Document>, Displayable {
		
		private static final String DEFAULT_LINE_SEPARATOR = "\n";
		
		private final LinkedList<String> lines;
		
		private final String lineSeparator;
		
		public Document() {
			this(List.of(), DEFAULT_LINE_SEPARATOR);
		}
				
		public Document(File file) throws IOException {
			this(Files.readString(file.toPath()));
		}
		
		public Document(String content) {
			this(content.lines().toList(), determineLineSeparator(content));
		}
		
		protected Document(List<String> lines, String lineSeparator) {
			super();
			this.lines = new LinkedList<>(lines);
			this.lineSeparator = lineSeparator;
		}
		
		public int size() {
			return lines.size();
		}
		
		public String get(int lineNumber) {
			return lines.get(lineNumber - 1);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < lines.size(); i++) {
				sb.append(lines.get(i));
				sb.append(lineSeparator);
			}
			
			return sb.toString();
		}
		
		public void display(PrintStream out) {
			for (int i = 0; i < lines.size(); i++) {
				out.print(String.format("%1$4s", i + 1));
				out.print(" ");
				out.print(lines.get(i));
				out.print(lineSeparator);
			}
		}
		
		@Override
		public Document copy() {
			return new Document(lines, lineSeparator);
		}
		
		public void insert(int lineNumber, List<String> insertedLines) {
			lines.addAll(lineNumber - 1, insertedLines);
		}
		
		public void insert(int lineNumber, String newLine) {
			insert(lineNumber, List.of(newLine));
		}
		
		public void prepend(String newLine) {
			insert(1, newLine);
		}
		
		public void append(String newLine) {
			insert(size() + 1, newLine);
		}
		
		public void remove(int lineNumber) {
			lines.remove(lineNumber - 1);
		}
		
		public void remove(Slice slice) {
			Slice resolvedSlice = slice.resolve(this);
			int size = resolvedSlice.getEnd() - resolvedSlice.getStart() + 1;
			
			while (size > 0) {
				remove(resolvedSlice.getStart());
				size -= 1;
			}
		}
		
		public void replace(Slice slice, Document document) {
			replace(slice, document.lines);
		}
		
		public void replace(Slice slice, List<String> replacementLines) {
			Slice resolvedSlice = slice.resolve(this);
			remove(resolvedSlice);
			insert(resolvedSlice.getStart(), replacementLines);
		}
		
		public void retain(Slice slice) {
			Slice resolvedSlice = slice.resolve(this);
			int removeStart = resolvedSlice.getStart() - 1;
			int removeEnd = size() - resolvedSlice.getEnd();
			
			while (removeStart > 0) {
				lines.removeFirst();
				removeStart -= 1;
			}
			
			while (removeEnd > 0) {
				lines.removeLast();
				removeEnd -= 1;
			}
		}
		
		public Document extract(Slice slice) {
			Slice resolvedSlice = slice.resolve(this);
			List<String> subList = lines.subList(resolvedSlice.getStart() - 1, resolvedSlice.getEnd());
			return new Document(subList, lineSeparator);
		}
		
		public void transform(Function<String, String> documentTransformer) {
			String oldContent = String.join(lineSeparator, lines); // avoid toString() as it adds an extra new line
			String newContent = documentTransformer.apply(oldContent);

			lines.clear();
			lines.addAll(newContent.lines().toList());
		}
		
		public void transformLines(Function<String, String> lineTransformer) {
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lineTransformer.apply(lines.get(i)));
			}
		}
		
		public void removeLeadingAndTrailingBlankLines() {
			while (lines.peekLast().isBlank()) {
				lines.removeLast();
			}
			
			while (lines.peekFirst().isBlank()) {
				lines.removeFirst();
			}
		}
		
		public void removeIndentation() {
			transform(String::stripIndent);
		}
		
		public void replaceTabsWithSpaces() {
			transformLines(s -> s.replaceAll("[\\t]", "    "));
		}
		
		public void save(File file) throws IOException {
			Files.writeString(file.toPath(), toString());
		}
		
		public boolean diff(Document other, PrintStream output) {
			// This is a simple line-by-line comparison that's sufficient for this purpose.  Could be improved using
			// an LCS algorithm (e.g. Myers).
			boolean result = false;
						
			for (int i = 0; i < Math.max(lines.size(), other.lines.size()); i++) {
				if (i >= lines.size()) {
					output.println("      ! ++ " + other.lines.get(i));
					result = true;
				} else if (i >= other.lines.size()) {
					output.println("      ! -- " + lines.get(i));
					result = true;
				} else if (!lines.get(i).equals(other.lines.get(i))) {
					output.println("      ! -- " + lines.get(i));
					output.println("      ! ++ " + other.lines.get(i));
					result = true;
				}
			}
			
			return result;
		}
		
		private static String determineLineSeparator(String content) {
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
				
	}
	
	static class ParsingException extends FrameworkException {
		
		private static final long serialVersionUID = 5588701884639018328L;

		public ParsingException(int lineNumber, String message) {
			super("Line " + lineNumber + ": " + message);
		}
		
		public ParsingException(int lineNumber, String message, Throwable cause) {
			super("Line " + lineNumber + ": " + message, cause);
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
