/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;

/**
 * Utility to update code samples and output found in various documents, including Markdown and HTML / XSLT files.
 * The aim is to inject real Java examples into the documentation so that all code snippets can be tested and
 * validated.  This works by embedding a special comment before the code block in one of the supported
 * {@link FileType}.  When processing the document, the code block immediately following the comment is validated
 * or updated from the referenced code.
 * <pre>{@code
 *   <!-- java:examples/Example1.java -->
 * 
 *   ```java
 *   ... code block updated from referenced Java file ...
 *   ```}</pre>
 * <p>
 * The format of the comment is:
 * <pre>{@code <!-- <language>:<filename> [<startingLine>:<endingLine>] {<flag>,...} -->}</pre>
 * <ul>
 *   <li>{@code <language>} is the name of the programming language.  A special case is {@code output}, which
 *       compiles, executes, and captures the output of the program.
 *   <li>{@code [<startingLine>:<endingLine>]} specifies the line numbers, starting at index 1, to extract from the
 *       file.  If no line numbers are provided, the entire content is copied.
 *   <li>{@code {<flag>,...}} specifies additional formatting options, such as {@code {keepComments}}.
 * </ul>
 * This utility can be run in validate-only mode or update mode.  In validate mode, any changes to the files will
 * result in an error.  This is useful in CI to validate the docs are up-to-date.  In update mode, the files are
 * updated with any changes.
 */
public class UpdateCodeSamples extends CommandLineUtility {
	
	// TODO: Ideas for improvements:
	//
	//   1. Support multiple line number ranges, such as [1:5,10:15], and optionally insert "..." between these slices.
	//
	//   2. Line numbers often need to be updated if the file changes.  Could specify a tag name and locate the code
	//      by searching for that tag.  For example, [foo] would select the content of:
	//
	//           // start:foo
	//           ... code that is copied ...
	//           // end:foo
	
	private static final long DEFAULT_SEED = 123456;

	private static final String[] DEFAULT_CLASSPATH = new String[] { "lib/*", "build", "examples" };
	
	private static final File[] DEFAULT_PATHS = new File[] { new File("docs/"), new File("website/xslt") };
	
	private static final Pattern REGEX = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:([^\\s]+)(?:\\s+\\[([^\\]]+)\\])?(?:\\s+\\{([^\\}]+)\\})?\\s+-->");
	
	/**
	 * {@code true} if running in update mode; {@code false} for validate mode.
	 */
	private boolean update;
	
	/**
	 * The classpath used when compiling and running Java examples.
	 */
	private String[] classpath;
	
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
		
		options.addOption(Option.builder("u")
				.longOpt("update")
				.build());
		options.addOption(Option.builder("c")
				.longOpt("classpath")
				.hasArgs()
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.build());

		return options;
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		update = commandLine.hasOption("update");
		classpath = commandLine.hasOption("classpath") ? commandLine.getOptionValues("classpath") : DEFAULT_CLASSPATH;
		seed = commandLine.hasOption("seed") ? Long.parseLong(commandLine.getOptionValue("seed")) : DEFAULT_SEED;
						
		System.out.println("Running in " + (update ? "update" : "validate") + " mode");
		System.out.println("Using classpath \"" + getClassPath(classpath) + "\"");
		System.out.println("Using seed " + seed);
		
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
		
		if (!file.exists()) {
			System.out.println("Skipping " + file + ", does not exist");
		}
		
		if (file.isDirectory()) {
			System.out.println("Scanning directory " + file);
			for (File nestedFile : file.listFiles()) {
				fileChanged |= scan(nestedFile);
			}
		} else {
			fileChanged |= process(file);
		}
		
		return fileChanged;
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
	private boolean process(File file) throws IOException, InterruptedException {
		boolean fileChanged = false;
		FileType fileType = FileType.fromExtension(FilenameUtils.getExtension(file.getName()));
		
		if (fileType == null) {
			System.out.println("Skipping " + file + ", not a recognized extension");
			return fileChanged;
		}
		
		System.out.println("Processing " + file);
		File tempFile = File.createTempFile("temp", null);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file));
			 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
				
				Matcher matcher = REGEX.matcher(line);
				
				if (matcher.matches()) {
					Language language = Language.fromString(matcher.group(1));
					String filename = matcher.group(2);

					FormattingOptions options = new FormattingOptions(language);
					options.parseLineNumbers(matcher.group(3));
					options.parseFlags(matcher.group(4));
					
					String content = "";
					System.out.println("    > Updating " + language + " block: " + filename + " " + options);
					
					switch (language) {
					case Output:
						compile(filename);
						content = execute(filename);
						break;
					default:
						content = FileUtils.readUTF8(new File(filename));
						break;
					}
					
					// compare old and new content
					List<String> newContent = format(content, options, fileType);
					List<String> oldContent = getNextCodeBlock(reader, writer, fileType);
					
					boolean contentChanged = diff(oldContent, newContent);
					fileChanged |= contentChanged;
					
					writer.write(String.join(System.lineSeparator(), newContent));
					writer.newLine();
				}
			}
		}
		
		if (update) {
			FileUtils.move(tempFile, file);
		} else {
			FileUtils.delete(tempFile);
		}
		
		return fileChanged;
	}
	
	/**
	 * Determines if any differences exist between the two code blocks, displaying any differences in the terminal.
	 * This will flag whitespace differences, but excludes the end of line characters.
	 * 
	 * @param first the first code block
	 * @param second the second code block
	 * @return {@code true} if any differences were detected
	 */
	private boolean diff(List<String> first, List<String> second) {
		boolean result = false;
		
		for (int i = 0; i < Math.max(first.size(), second.size()); i++) {
			if (i >= first.size()) {
				System.out.println("      ! >> " + second.get(i));
				result = true;
			} else if (i >= second.size()) {
				System.out.println("      ! << " + first.get(i));
				result = true;
			} else if (!first.get(i).equals(second.get(i))) {
				System.out.println("      ! << " + first.get(i));
				System.out.println("      ! >> " + second.get(i));
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Locates and reads the next code block, including the lines marking the start and end of the code block.
	 * Any empty lines between the comment and code block are skipped (but are copied to the writer), but any
	 * non-empty line that is not a code block will result in an error.
	 * 
	 * @param reader the reader for the original file
	 * @param writer the writer for the modified file
	 * @param fileType the file type
	 * @return the code block
	 * @throws IOException if an I/O error occurred while reading the file
	 */
	private List<String> getNextCodeBlock(BufferedReader reader, BufferedWriter writer, FileType fileType)
			throws IOException {
		List<String> content = new ArrayList<String>();
		String line = null;
		boolean inCodeBlock = false;
		
		while ((line = reader.readLine()) != null) {		
			if (!inCodeBlock && fileType.isStartOfCodeBlock(line)) {
				content.add(line);
				inCodeBlock = true;
			} else if (inCodeBlock) {
				content.add(line);
				
				if (fileType.isEndOfCodeBlock(line)) {
					return content;
				}
			} else {
				writer.write(line);
				writer.newLine();
				
				if (!line.trim().isEmpty()) {
					throw new IOException("Expected code block but found '" + line + "'");
				}
			}
		}
		
		throw new IOException("Reached end of file before finding code block");
	}
	
	/**
	 * Returns the classpath used to start the JVM, using the appropriate separator for the host operating system.
	 * 
	 * @param entries the entries in the classpath
	 * @return the formatted classpath
	 */
	private String getClassPath(String... entries) {
		return String.join(SystemUtils.IS_OS_WINDOWS ? ";" : ":", entries);
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
	
	/**
	 * Invokes the Java compiler in a separate process.
	 * 
	 * @param filename the Java file to compile
	 * @throws IOException if a I/O error occurred while running the process
	 * @throws InterruptedException if the process was interrupted
	 */
	private void compile(String filename) throws IOException, InterruptedException {
		String extension = FilenameUtils.getExtension(filename);
		
		if (extension.equalsIgnoreCase("java")) {
			ProcessBuilder processBuilder = new ProcessBuilder("javac",
					"-classpath", getClassPath(classpath),
					filename);
			
			RedirectStream.invoke(processBuilder);
		} else {
			throw new IllegalArgumentException("Unsupported file extension " + extension);
		}
	}
	
	/**
	 * Runs the Java program in a separate process.
	 * 
	 * @param filename the Java file to run
	 * @return the standard output produced by the program
	 * @throws IOException if an I/O error occurred while running the process
	 * @throws InterruptedException if the process was interrupted
	 */
	private String execute(String filename) throws IOException, InterruptedException {
		String extension = FilenameUtils.getExtension(filename);
		
		if (extension.equalsIgnoreCase("java") || extension.equalsIgnoreCase("class")) {
			ProcessBuilder processBuilder = new ProcessBuilder("java",
					"-classpath", getClassPath(classpath),
					"-D" + Settings.KEY_PRNG_SEED + "=" + seed,
					getClassName(filename));
			
			return RedirectStream.capture(processBuilder);
		} else {
			throw new IllegalArgumentException("Unsupported file extension " + extension);
		}
	}
	
	/**
	 * Formats the given code block based on the options and target file type.
	 * 
	 * @param content the code block to format
	 * @param options the formatting options
	 * @param fileType the target file type where the code block is inserted
	 * @return the formatted code block
	 * @throws IOException if an I/O error occurred
	 */
	private List<String> format(String content, FormattingOptions options, FileType fileType) throws IOException {
		return format(splitIntoLines(content), options, fileType);
	}
	
	/**
	 * Formats the given code block based on the options and target file type.
	 * 
	 * @param lines the code block to format
	 * @param options the formatting options
	 * @param fileType the target file type where the code block is inserted
	 * @return the formatted code block
	 * @throws IOException if an I/O error occurred
	 */
	private List<String> format(List<String> lines, FormattingOptions options, FileType fileType) throws IOException {
		int startingLine = options.startingLine;
		int endingLine = options.endingLine;
		
		if (startingLine < 0) {
			startingLine += lines.size() + 1;
		} else if (startingLine == 0) {
			startingLine = 1;
		}
		
		if (endingLine < 0) {
			endingLine += lines.size();
		}
		
		lines = lines.subList(Math.max(0, startingLine - 1), Math.min(lines.size(), endingLine));
		
		if (options.stripIndentation()) {
			// TODO: can use String#stripIndent() after updating to Java 12+
			boolean stripFirstChar = true;
			
			while (stripFirstChar) {
				char charToRemove = lines.get(0).charAt(0);
				
				if (!Character.isWhitespace(charToRemove)) {
					break;
				}
				
				for (int i = 1; i < lines.size(); i++) {
					String line = lines.get(i);
					
					if (line.length() > 0 && line.charAt(0) != charToRemove) {
						stripFirstChar = false;
						break;
					}
				}
				
				if (stripFirstChar) {
					for (int i = 0; i < lines.size(); i++) {
						String line = lines.get(i);
						lines.set(i, line.length() > 0 ? line.substring(1) : "");
					}
				}
			}
		}
		
		if (options.stripComments()) {
			lines = options.language.stripComments(lines);
		}
		
		if (options.replaceTabsWithSpaces()) {
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				line = line.replaceAll("[\\t]", "    ");
				lines.set(i, line);
			}
		}
		
		fileType.wrapInCodeBlock(lines, options);
		
		return lines;
	}
	
	/**
	 * Splits the given string into individual lines.
	 * 
	 * @param content the string to split
	 * @return the lines
	 * @throws IOException if an I/O error occurred
	 */
	private static List<String> splitIntoLines(String content) throws IOException {
		List<String> lines = new ArrayList<String>();
		
		try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		
		return lines;
	}
	
	/**
	 * Formatting options for the code block.
	 */
	private class FormattingOptions {
		
		/**
		 * Constant representing the first line in the file.
		 */
		private static final int FIRST_LINE = 1;
		
		/**
		 * Constant representing the last line in the file.
		 */
		private static final int LAST_LINE = Integer.MAX_VALUE;
		
		/**
		 * The programming language for the code block.
		 */
		private final Language language;
		
		/**
		 * The starting line number.  Line numbers start at index {@code 1}.
		 */
		private int startingLine;
		
		/**
		 * The ending line number.  Line numbers start at index {@code 1}.  If the value exceeds the length of the
		 * file, will include all lines up to the end of the file.
		 */
		private int endingLine;
		
		/**
		 * Any additional formatting flags.
		 */
		private final EnumSet<FormatFlag> formatFlags;
				
		/**
		 * Constructs default formatting options for the given language.
		 * 
		 * @param language the programming language
		 */
		public FormattingOptions(Language language) {
			super();
			this.language = language;
			
			startingLine = FIRST_LINE;
			endingLine = LAST_LINE;
			formatFlags = EnumSet.noneOf(FormatFlag.class);
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
		 * 
		 * @param str the string representation of the line numbers
		 */
		public void parseLineNumbers(String str) {
			final Pattern lineNumbers = Pattern.compile("(\\-?[0-9]+)?[:\\\\-](\\-?[0-9]+)?");
			
			if (str == null) {
				startingLine = FIRST_LINE;
				endingLine = LAST_LINE;
				return;
			}
			
			if (str.startsWith("[") && str.endsWith("]")) {
				str = str.substring(1, str.length()-1);
			}
			
			Matcher matcher = lineNumbers.matcher(str);
			
			if (matcher.matches()) {
				startingLine = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : FIRST_LINE;
				endingLine = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : LAST_LINE;
			} else {
				throw new IllegalArgumentException("Unrecognized line number range '" + str + "'");
			}
		}
		
		/**
		 * Parses additional format flags given as {@code {<flag1>;<flag2>;...}}.
		 * 
		 * @param str the string representation of the format flags
		 */
		public void parseFlags(String str) {
			formatFlags.addAll(FormatFlag.fromFormatString(str));
		}
		
		/**
		 * Returns {@code true} if comments are stripped from code blocks.
		 * 
		 * @return {@code true} if comments are stripped from code blocks
		 */
		public boolean stripComments() {
			return !formatFlags.contains(FormatFlag.KeepComments);
		}
		
		/**
		 * Returns {@code true} if indentation is removed from code blocks.
		 * 
		 * @return {@code true} if indentation is removed from code blocks
		 */
		public boolean stripIndentation() {
			return !formatFlags.contains(FormatFlag.KeepIndentation);
		}
		
		/**
		 * Returns {@code true} if tabs are replaced by spaces in code blocks.
		 * 
		 * @return {@code true} if tabs are replaced by spaces in code blocks
		 */
		public boolean replaceTabsWithSpaces() {
			return !formatFlags.contains(FormatFlag.KeepTabs);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(startingLine == FIRST_LINE ? "" : startingLine);
			sb.append(":");
			sb.append(endingLine == LAST_LINE ? "" : endingLine);
			sb.append("]");
			
			if (!formatFlags.isEmpty()) {
				sb.append(" ");
				sb.append(FormatFlag.toFormatString(formatFlags));
			}
			
			return sb.toString();
		}
		
	}
	
	/**
	 * The language shown in the code block, used to ensure the appropriate syntax highlighting is configured.
	 */
	private enum Language {
		
		/**
		 * Java source code.
		 */
		Java,
		
		/**
		 * Plain text.
		 */
		Text,
		
		/**
		 * Bash or terminal commands.
		 */
		Bash,
		
		/**
		 * Special mode where the output of the program is captured and displayed in the code block.
		 */
		Output;
		
		/**
		 * Determine the language from its string representation using case-insensitive matching.
		 * 
		 * @param str the string representation of the language
		 * @return the language
		 * @throws IllegalArgumentException if the language is not supported
		 */
		public static Language fromString(String str) {
			for (Language language : values()) {
				if (language.name().equalsIgnoreCase(str)) {
					return language;
				}
			}
			
			throw new IllegalArgumentException("Unrecognized language '" + str + "'");
		}
		
		/**
		 * Strips comments out of the code block.
		 * 
		 * @param lines the code block
		 * @return the code block without comments
		 * @throws IOException if an I/O error occurred
		 */
		public List<String> stripComments(List<String> lines) throws IOException {
			String content = String.join(System.lineSeparator(), lines);
			content = stripComments(content);
			return splitIntoLines(content);
		}
		
		/**
		 * Strips comments out of the code block.  This also removes any duplicate blank lines that may result
		 * when removing the comments.
		 * 
		 * @param content the code block
		 * @return the code block without comments
		 */
		public String stripComments(String content) {
			switch (this) {
			case Java:
				// Remove C-style // comments
				content = content.replaceAll("//[^\\n]*", "");                              
				
				// Remove C-style /* */ comments
				content = content.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");
				
				 // Replace multiple blank lines with just one
				content = content.replaceAll("(?:\\s*\\r?\\n){2,}", System.lineSeparator() + System.lineSeparator());
				return content;
			default:
				return content;
			}
		}
		
	}
	
	/**
	 * Additional formatting flags.
	 */
	private enum FormatFlag {
		
		/**
		 * Retain all comments.  By default, the formatter removes comments.
		 */
		KeepComments,
		
		/**
		 * Keep the original indentation.  By default, the formatter removes any indentation so code blocks are all
		 * left-aligned.
		 */
		KeepIndentation,
		
		/**
		 * Keeps tabs.  By default, the formatter replaces tabs with spaces.
		 */
		KeepTabs;
		
		/**
		 * Determine the format flag from its string representation using case-insensitive matching.
		 * 
		 * @param str the string representation
		 * @return the format flag
		 * @throws IllegalArgumentException if the format flag is not supported
		 */
		public static FormatFlag fromString(String str) {
			for (FormatFlag formatFlag : values()) {
				if (formatFlag.name().equalsIgnoreCase(str)) {
					return formatFlag;
				}
			}
			
			throw new IllegalArgumentException("Unrecognized formatting flag '" + str + "'");
		}
		
		/**
		 * Parses all format flags from the input string, typically given as {@code {<flag1>;<flag2>;...}}.
		 * 
		 * @param str the string containing format flags
		 * @return the parsed format flags
		 * @throws IllegalArgumentException if any of the format flags are not supported
		 */
		public static EnumSet<FormatFlag> fromFormatString(String str) {
			EnumSet<FormatFlag> result = EnumSet.noneOf(FormatFlag.class);
			
			if (str == null) {
				return result;
			}
			
			if (str.startsWith("{") && str.endsWith("}")) {
				str = str.substring(1, str.length()-1);
			}
			
			for (String token : str.split("[;,]")) {
				result.add(fromString(token.trim()));
			}
			
			return result;
		}
		
		/**
		 * Returns the string representation of the format flags.
		 * 
		 * @param flags the format flags
		 * @return the string representation
		 */
		public static String toFormatString(EnumSet<FormatFlag> flags) {
			return "{" + flags.stream().map(f -> f.toString()).collect(Collectors.joining(";")) + "}";
		}
	}
	
	/**
	 * Supported file types that are processed by this utility.  This defines how code blocks are identified and
	 * formatted for a particular file format.
	 */
	private enum FileType {
		
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
		private FileType(String... extensions) {
			this.extensions = extensions;
		}
		
		/**
		 * Determine the file type from the file extension.
		 * 
		 * @param extension the file extension, excluding the {@code "."}
		 * @return the file type, or {@code null} if the file type is not recognized
		 */
		public static FileType fromExtension(String extension) {
			for (FileType fileType : values()) {
				for (String fileExtension : fileType.extensions) {
					if (fileExtension.equalsIgnoreCase(extension)) {
						return fileType;
					}
				}
			}
			
			return null;
		}
		
		/**
		 * Returns {@code true} if the line indicates the start of a code block.
		 * 
		 * @param line the line read from the file
		 * @return {@code true} if the line is the start of a code block; {@code false} otherwise
		 */
		public boolean isStartOfCodeBlock(String line) {
			switch (this) {
			case Markdown:
				return line.startsWith("```");
			case Html:
				return line.startsWith("<pre");
			default:
				return false;
			}
		}
		
		/**
		 * Returns {@code true} if the line indicates the end of a code block.
		 * 
		 * @param line the line read from the file
		 * @return {@code true} if the line is the end of a code block; {@code false} otherwise
		 */
		public boolean isEndOfCodeBlock(String line) {
			switch (this) {
			case Markdown:
				return line.startsWith("```");
			case Html:
				return line.startsWith("</pre>");
			default:
				return false;
			}
		}
		
		/**
		 * Returns the name of the "brush" that provides the appropriate syntax highlighting for the language.
		 * Defaults to plain text if the language is not recognized.
		 * 
		 * @param language the language displayed in the code block
		 * @return the name of the "brush"
		 */
		public String getBrush(Language language) {
			final Set<String> markdownBrush = new HashSet<String>(Arrays.asList("java", "bash", "text"));
			final Set<String> htmlBrush = new HashSet<String>(Arrays.asList("java"));
			
			String brushName = language.name().toLowerCase();
			
			switch (this) {
			case Markdown:
				return markdownBrush.contains(brushName) ? brushName : "";
			case Html:
				return htmlBrush.contains(brushName) ? brushName : "plain";
			default:
				return "";
			}
		}
		
		/**
		 * Wraps the code block in the starting / ending lines appropriate for the file type.
		 * 
		 * @param lines the code block
		 * @param options the formatting options
		 */
		public void wrapInCodeBlock(List<String> lines, FormattingOptions options) {
			switch (this) {
			case Markdown:
				lines.add(0, "```" + getBrush(options.language));
				lines.add("```");
				break;
			case Html:
				lines.add(0, "<pre class=\"brush: " + getBrush(options.language) + "; toolbar: false;\">");
				lines.add(1, "<![CDATA[");
				lines.add("]]>");
				lines.add("</pre>");
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Invokes the command-line utility to update code samples.
	 * 
	 * @param args the command-line arguments
	 * @throws Exception if an error occurred running the utility
	 */
	public static void main(String[] args) throws Exception {
		new UpdateCodeSamples().start(args);
	}

}
