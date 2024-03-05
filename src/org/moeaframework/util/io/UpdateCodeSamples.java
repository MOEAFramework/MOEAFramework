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
import org.apache.commons.io.FileUtils;
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
 * Supported comments include:
 * <pre>{@code
 *   <!-- java:examples/Example1.java -->              # Embed entire file in document
 *   <!-- java:examples/Example1.java [25:35] -->      # Embed lines 25 to 35 in the document
 *   <!-- output:examples/Example1.java -->            # Compile, run, and embed output in document
 *   <!-- output.examples/Example1.java [:10] -->      # Compile, run, and embed first 10 lines from output in document}</pre>
 * <p>
 * Can be run in validate-only mode or update mode.  In validate mode, it simply checks of the code blocks changed
 * and returns a non-zero exit code.  In update mode, it updates the code block in the file.
 */
public class UpdateCodeSamples extends CommandLineUtility {
	
	private static final String CHARSET = "UTF8";
	
	private static final long SEED = 123456;
	
	private static final String[] DEFAULT_CLASSPATH = new String[] { "lib/*", "build", "examples" };
	
	private static final File[] DEFAULT_PATHS = new File[] { new File("docs/"), new File("website/xslt") };
	
	private static final Pattern REGEX = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:([^\\s]+)(?:\\s+\\[([0-9]+)?[:\\-]([0-9]+)?\\])?\\s+-->");
	
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

		if (commandLine.getArgs().length == 0) {
			for (File path : DEFAULT_PATHS) {
				scan(path);
			}
		} else {
			for (String arg : commandLine.getArgs()) {
				scan(new File(arg));
			}
		}
	}
	
	private void scan(File file) throws Exception {
		if (!file.exists()) {
			System.out.println("Skipping " + file + ", does not exist");
		}
		
		if (file.isDirectory()) {
			System.out.println("Scanning directory " + file);
			for (File nestedFile : file.listFiles()) {
				scan(nestedFile);
			}
		} else {
			process(file);
		}
	}
	
	private void process(File file) throws Exception {
		FileType fileType = FileType.fromExtension(FilenameUtils.getExtension(file.getName()));
		
		if (fileType == null) {
			System.out.println("Skipping " + file + ", not a recognized extension");
			return;
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
					
					FormattingOptions options = new FormattingOptions(language, startingLine, endingLine);
					options.fileType = fileType;
					
					String content = "";
					System.out.println("    > Updating " + language + " block: " + filename + " " + options);
					
					if (language.equalsIgnoreCase("output")) {
						options.language = null;

						compile(filename);
						content = execute(filename);
					} else {
						content = FileUtils.readFileToString(new File(filename), CHARSET);
					}
					
					// write updated code block to output
					writer.write(format(content, options));
					writer.newLine();
					
					skipNextCodeBlock(reader, options.fileType);
				}
			}
		}

		if (!FileUtils.contentEqualsIgnoreEOL(file, tempFile, CHARSET)) {
			System.out.println("    > File changed!");
			
			if (!update) {
				throw new IOException("Detected changes to files!");
			}
		}
		
		if (update) {
			org.moeaframework.util.io.FileUtils.move(tempFile, file);
		} else {
			org.moeaframework.util.io.FileUtils.delete(tempFile);
		}
	}
	
	private void skipNextCodeBlock(BufferedReader reader, FileType fileType) throws Exception {
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
			line = line.trim();
			
			if (line.isEmpty()) {
				continue;
			}
			
			if (isCodeBlock.test(line)) {
				if (inCodeBlock) {
					return;
				}
				
				inCodeBlock = true;
			}
			
			if (!inCodeBlock) {
				throw new IOException("Expected code block but found '" + line + "'");
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
	
	private String format(String content, FormattingOptions options) throws Exception {
		List<String> lines = new ArrayList<String>();
		
		try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		
		return format(lines, options);
	}
	
	private String format(List<String> lines, FormattingOptions options) {
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
		
		if (options.replaceTabs) {
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				line = line.replaceAll("[\\t]", "    ");
				lines.set(i, line);
			}
		}
		
		switch (options.fileType) {
			case Markdown:
				lines.add(0, "");
				lines.add(1, "```" + (options.language == null ? "" : options.language));
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
		
		return String.join(System.lineSeparator(), lines);
	}
	
	private class FormattingOptions {
		
		public static final int FIRST_LINE = 1;
		
		public static final int LAST_LINE = Integer.MAX_VALUE;
		
		public String language;
		
		public int startingLine;
		
		public int endingLine;
		
		public boolean stripIndentation = true;
		
		public boolean stripComments = true;
		
		public boolean replaceTabs = true;
		
		public FileType fileType;
		
		public FormattingOptions(String language, int startingLine, int endingLine) {
			super();
			this.language = language;
			this.startingLine = startingLine;
			this.endingLine = endingLine;
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
