package org.moeaframework.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.Settings;

/**
 * Utility to update code samples and output found in Markdown files.  This allows us to have complete
 * Markdown files that are kept in-sync with working examples.
 */
public class UpdateDocs {
	
	private static final String CHARSET = "UTF8";
	
	private static final long SEED = 123456;
	
	private static final String[] CLASSPATH = new String[] { "lib/*", "build", "examples" };
	
	private static final File DOCS_PATH = new File("docs/");
	
	private static final Pattern REGEX = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:([^\\s]+)(?:\\s+\\[([0-9]+)?[:\\-]([0-9]+)?\\])?\\s+-->");
	
	private final boolean update;
	
	public UpdateDocs(boolean update) {
		super();
		this.update = update;
	}
	
	public void scan(File file) throws Exception {
		if (file.isDirectory()) {
			System.out.println("Scanning directory " + file);
			for (File nestedFile : file.listFiles()) {
				scan(nestedFile);
			}
		} else if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("md")) {
			System.out.println("Processing " + file);
			process(file);
		} else {
			System.out.println("Skipping " + file + ", not a Markdown file");
		}
	}
	
	public void process(File file) throws Exception {
		File tempFile = File.createTempFile("markdown", "md");
		
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
					
					FormattingOptions options = new FormattingOptions(startingLine, endingLine);
					String content = "";
					
					System.out.println("    > Updating " + language + " code block: " + filename + " " + options);
					
					if (language.equalsIgnoreCase("output")) {
						language = "";

						compile(filename);
						content = execute(filename);
					} else {
						content = FileUtils.readFileToString(new File(filename), CHARSET);
					}
					
					// write updated code block to output
					writer.newLine();
					writer.write("```" + language);
					writer.newLine();
					writer.write(format(content, options));
					writer.newLine();
					writer.write("```");
					writer.newLine();
					
					skipNextCodeBlock(reader);
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
			file.delete();
			tempFile.renameTo(file);
		} else {
			tempFile.delete();
		}
	}
	
	private void skipNextCodeBlock(BufferedReader reader) throws Exception {
		String line = null;
		boolean inCodeBlock = false;
		
		while ((line = reader.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue;
			}
			
			if (line.startsWith("```")) {
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
			Process process = new ProcessBuilder("javac",
					"-classpath", getClassPath(CLASSPATH),
					filename).start();
			
			RedirectStream.redirect(process.getInputStream(), System.out);
			RedirectStream.redirect(process.getErrorStream(), System.err);
			
			if (process.waitFor() != 0) {
				throw new IOException("Process exited with non-zero status (" + process.exitValue() + ")");
			}
		} else {
			throw new IOException("Unsupported file extension " + extension);
		}
	}
	
	private String execute(String filename) throws Exception {
		String extension = FilenameUtils.getExtension(filename);
		
		if (extension.equalsIgnoreCase("java")) {
			Process process = new ProcessBuilder("java",
					"-classpath", getClassPath(CLASSPATH),
					"-D" + Settings.KEY_PRNG_SEED + "=" + SEED,
					getClassName(filename)).start();
			
			try (OutputStream out = new ByteArrayOutputStream()) {
				RedirectStream.redirect(process.getInputStream(), out);
				RedirectStream.redirect(process.getErrorStream(), System.err);
				
				if (process.waitFor() != 0) {
					throw new IOException("Process exited with non-zero status (" + process.exitValue() + ")");
				}
				
				return out.toString();
			}
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
		
		return String.join(System.lineSeparator(), lines);
	}
	
	private class FormattingOptions {
		
		public static final int FIRST_LINE = 1;
		
		public static final int LAST_LINE = Integer.MAX_VALUE;
		
		public int startingLine;
		
		public int endingLine;
		
		public boolean stripIndentation = true;
		
		public boolean stripComments = true;
		
		public boolean replaceTabs = true;
		
		public FormattingOptions(int startingLine, int endingLine) {
			super();
			this.startingLine = startingLine;
			this.endingLine = endingLine;
		}
		
		public String toString() {
			return "[" + (startingLine == FIRST_LINE ? "" : startingLine) + ":" +
					(endingLine == LAST_LINE ? "" : endingLine) + "]";
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		boolean update = args.length > 0 && args[0].equals("update");
		new UpdateDocs(update).scan(DOCS_PATH);
	}

}
