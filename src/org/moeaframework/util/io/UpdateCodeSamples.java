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
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <p>
 * Language is the name of the programming language.  A special case is {@code output}, which compiles, executes, and
 * captures the output of the program.
 * <p>
 * If no line numbers are provided, the entire content is copied.  The line numbers start at index 1.  The starting
 * or ending line number can be excluded, which case it copies the content from the start or end, respectively.
 * <p>
 * Flags provide additional formatting options, such as {@code {keepComments}} to keep any Java comments in the
 * example.
 * <p>
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
					String language = matcher.group(1);
					String filename = matcher.group(2);
					int startingLine = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : FormattingOptions.FIRST_LINE;
					int endingLine = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : FormattingOptions.LAST_LINE;
					String flags = matcher.group(5);
					
					FormattingOptions options = new FormattingOptions(language, startingLine, endingLine);
					options.parseFlags(flags);
					options.fileType = fileType;
					
					String content = "";
					System.out.println("    > Updating " + language + " block: " + filename + " " + options);
					
					if (language.equalsIgnoreCase("output")) {
						options.language = null;

						compile(filename);
						content = execute(filename);
					} else {
						content = FileUtils.readUTF8(new File(filename));
					}
					
					// compare old and new content
					List<String> newContent = format(content, options);
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
		
		Predicate<String> isCodeBlock = str -> {
			switch (fileType) {
				case Markdown:
					return str.startsWith("```");
				case Html:
					return str.startsWith("<pre") || str.startsWith("</pre>");
				default:
					return false;
			}
		};
		
		while ((line = reader.readLine()) != null) {		
			if (isCodeBlock.test(line)) {
				content.add(line);
				
				if (inCodeBlock) {
					return content;
				}
				
				inCodeBlock = true;
			} else if (inCodeBlock) {
				content.add(line);
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
	
	private List<String> format(String content, FormattingOptions options) throws Exception {
		List<String> lines = new ArrayList<String>();
		
		try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		
		return format(lines, options);
	}
	
	private List<String> format(List<String> lines, FormattingOptions options) {
		lines = lines.subList(options.startingLine - 1, Math.min(lines.size(), options.endingLine));
		
		if (options.stripIndentation) {
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
		
		if (options.stripComments) {
			lines.removeIf(s -> s.trim().startsWith("//"));
		}
		
		if (options.replaceTabsWithSpaces) {
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				line = line.replaceAll("[\\t]", "    ");
				lines.set(i, line);
			}
		}
		
		switch (options.fileType) {
			case Markdown:
				lines.add(0, "```" + (options.language == null ? "" : options.language));
				lines.add("```");
				break;
			case Html:
				lines.add(0, "<pre class=\"brush: " + (options.language == null ? "plain" : options.language) + "; toolbar: false;\">");
				lines.add(1, "<![CDATA[");
				lines.add("]]>");
				lines.add("</pre>");
				break;
			default:
				break;
		}
		
		return lines;
	}
	
	private class FormattingOptions {
		
		public static final int FIRST_LINE = 1;
		
		public static final int LAST_LINE = Integer.MAX_VALUE;
		
		public String language;
		
		public final int startingLine;
		
		public final int endingLine;
		
		public boolean stripIndentation = true;
		
		public boolean stripComments = true;
		
		public boolean replaceTabsWithSpaces = true;
		
		public FileType fileType;
		
		public FormattingOptions(String language, int startingLine, int endingLine) {
			super();
			this.language = language;
			this.startingLine = startingLine;
			this.endingLine = endingLine;
		}
		
		public void parseFlags(String flags) throws IOException {
			if (flags == null || flags.trim().isEmpty()) {
				return;
			}
			
			for (String token : flags.split("[;,]")) {
				if (token.equalsIgnoreCase("keepComments")) {
					stripComments = false;
				} else if (token.equalsIgnoreCase("keepIndentation")) {
					stripIndentation = false;
				} else if (token.equalsIgnoreCase("keepTabs")) {
					replaceTabsWithSpaces = false;
				} else {
					throw new IOException("Unrecognized formatting flag '" + token + "'");
				}
			}
		}

		public String toString() {
			return "[" + (startingLine == FIRST_LINE ? "" : startingLine) + ":" +
					(endingLine == LAST_LINE ? "" : endingLine) + "]";
		}
		
	}
	
	private enum FileType {
		
		Markdown("md"),
		
		Html("html", "xml");
		
		public final String[] extensions;
		
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
	}
	
	public static void main(String[] args) throws Exception {
		new UpdateCodeSamples().start(args);
	}

}
