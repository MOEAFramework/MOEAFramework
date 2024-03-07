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
 * <ul>
 * This utility can be run in validate-only mode or update mode.  In validate mode, any changes to the files will
 * result in an error.  This is useful in CI to validate the docs are up-to-date.  In update mode, the files are
 * updated with any changes.
 */
public class UpdateCodeSamples extends CommandLineUtility {
		
	private static final long SEED = 123456;
	
	private static final String[] DEFAULT_CLASSPATH = new String[] { "lib/*", "build", "examples" };
	
	private static final File[] DEFAULT_PATHS = new File[] { new File("docs/"), new File("website/xslt") };
	
	private static final Pattern REGEX = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:([^\\s]+)(?:\\s+\\[([0-9]+)?[:\\-]([0-9]+)?\\])?(?:\\s+\\{([a-zA-Z0-9;,]+)\\})?\\s+-->");
	
	private boolean update;
	
	private String[] classpath;
	
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

		return options;
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		update = commandLine.hasOption("update");
		classpath = commandLine.hasOption("classpath") ? commandLine.getOptionValues("classpath") : DEFAULT_CLASSPATH;
		
		System.out.println("Using classpath \"" + getClassPath(classpath) + "\"");
		
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
				throw new IOException("Detected changes to code samples!");
			}
		}
	}
	
	private boolean scan(File file) throws Exception {
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
	
	private boolean process(File file) throws Exception {
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

					FormattingOptions options = new FormattingOptions(language,
							matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : FormattingOptions.FIRST_LINE,
							matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : FormattingOptions.LAST_LINE);
					options.parseFlags(matcher.group(5));
					
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
					
					boolean contentChanged = diffContent(oldContent, newContent);
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
	
	private boolean diffContent(List<String> first, List<String> second) {
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
	
	private List<String> getNextCodeBlock(BufferedReader reader, BufferedWriter writer, FileType fileType) throws Exception {
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
	
	private String getClassPath(String... entries) {
		return String.join(SystemUtils.IS_OS_WINDOWS ? ";" : ":", entries);
	}
	
	private String getClassName(String filename) {
		Path path = Paths.get(FilenameUtils.removeExtension(filename));
		
		if (path.startsWith("examples") || path.startsWith("src") || path.startsWith("test")) {
			path = path.subpath(1, path.getNameCount());
		}
		
		return path.toString().replaceAll("[\\\\/]", ".");
	}
		
	private void compile(String filename) throws Exception {
		String extension = FilenameUtils.getExtension(filename);
		
		if (extension.equalsIgnoreCase("java")) {
			ProcessBuilder processBuilder = new ProcessBuilder("javac",
					"-classpath", getClassPath(classpath),
					filename);
			
			RedirectStream.invoke(processBuilder);
		} else {
			throw new IOException("Unsupported file extension " + extension);
		}
	}
	
	private String execute(String filename) throws Exception {
		String extension = FilenameUtils.getExtension(filename);
		
		if (extension.equalsIgnoreCase("java")) {
			ProcessBuilder processBuilder = new ProcessBuilder("java",
					"-classpath", getClassPath(classpath),
					"-D" + Settings.KEY_PRNG_SEED + "=" + SEED,
					getClassName(filename));
			
			return RedirectStream.capture(processBuilder);
		} else {
			throw new IOException("Unsupported file extension " + extension);
		}
	}
	
	private List<String> format(String content, FormattingOptions options, FileType fileType) throws IOException {
		return format(splitIntoLines(content), options, fileType);
	}
	
	private List<String> format(List<String> lines, FormattingOptions options, FileType fileType) throws IOException {
		lines = lines.subList(options.startingLine - 1, Math.min(lines.size(), options.endingLine));
		
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
	
	private class FormattingOptions {
		
		public static final int FIRST_LINE = 1;
		
		public static final int LAST_LINE = Integer.MAX_VALUE;
		
		private Language language;
		
		private final int startingLine;
		
		private final int endingLine;
		
		private final EnumSet<FormatFlag> formatFlags;
				
		public FormattingOptions(Language language, int startingLine, int endingLine) {
			super();
			this.language = language;
			this.startingLine = startingLine;
			this.endingLine = endingLine;
			
			formatFlags = EnumSet.noneOf(FormatFlag.class);
		}
		
		public void parseFlags(String str) throws IOException {
			formatFlags.addAll(FormatFlag.fromFormatString(str));
		}
		
		public boolean stripComments() {
			return !formatFlags.contains(FormatFlag.KeepComments);
		}
		
		public boolean stripIndentation() {
			return !formatFlags.contains(FormatFlag.KeepIndentation);
		}
		
		public boolean replaceTabsWithSpaces() {
			return !formatFlags.contains(FormatFlag.KeepTabs);
		}

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
	
	private enum Language {
		
		Java,
		
		Text,
		
		Bash,
		
		Output;
		
		public static Language fromString(String str) {
			for (Language language : values()) {
				if (language.name().equalsIgnoreCase(str)) {
					return language;
				}
			}
			
			throw new IllegalArgumentException("Unrecognized language '" + str + "'");
		}
		
		public List<String> stripComments(List<String> lines) throws IOException {
			String content = String.join(System.lineSeparator(), lines);
			content = stripComments(content);
			return splitIntoLines(content);
		}
		
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
	
	private enum FormatFlag {
		
		KeepComments,
		
		KeepIndentation,
		
		KeepTabs;
		
		public static FormatFlag fromString(String str) {
			for (FormatFlag formatFlag : values()) {
				if (formatFlag.name().equalsIgnoreCase(str)) {
					return formatFlag;
				}
			}
			
			throw new IllegalArgumentException("Unrecognized formatting flag '" + str + "'");
		}
		
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
		
		public static String toFormatString(EnumSet<FormatFlag> flags) {
			return "{" + flags.stream().map(f -> f.toString()).collect(Collectors.joining(";")) + "}";
		}
	}
	
	private enum FileType {
		
		Markdown("md"),
		
		Html("html", "xml");
		
		private final String[] extensions;
		
		private FileType(String... extensions) {
			this.extensions = extensions;
		}
		
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
	
	public static void main(String[] args) throws Exception {
		new UpdateCodeSamples().start(args);
	}

}
