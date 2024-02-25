import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * Simple utility to update code samples found in Markdown files.
 */
public class UpdateDocs {
	
	public static final File DEFAULT_PATH = new File("docs/");
	
	public static final Pattern REGEX = Pattern.compile("<!--\\s+([a-zA-Z]+)\\:([a-zA-Z0-9\\.\\\\\\/]+)(?:\\s+\\[([0-9]+)?\\-([0-9]+)?\\])?\\s+-->");
	
	private final File path;
	
	private final boolean update;
	
	public UpdateDocs(File path, boolean update) {
		super();
		this.path = path;
		this.update = update;
	}
	
	public void run() throws IOException {
		scan(path);
	}
	
	private void scan(File file) throws IOException {
		if (file.isDirectory()) {
			System.out.println("Scanning directory " + file);
			for (File nestedFile : file.listFiles()) {
				scan(nestedFile);
			}
		} else if (file.getName().endsWith(".md")) {
			System.out.println("Processing " + file);
			process(file);
		} else {
			System.out.println("Skipping " + file + ", not a Markdown file");
		}
	}
	
	private void process(File file) throws IOException {
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
					int startingLine = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
					int endingLine = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : Integer.MAX_VALUE;
					
					System.out.println("    > Updating code block [" + language + " " + filename + " " + startingLine + "-" + endingLine + "]");
					
					// write updated code block to output
					writer.newLine();
					writer.write("```" + language);
					writer.newLine();
					writer.write(loadCodeSample(filename, startingLine, endingLine));
					writer.newLine();
					writer.write("```");
					writer.newLine();
					
					// skip over code block in input
					while ((line = reader.readLine()) != null) {
						if (line.equals("```")) {
							break;
						}
					}
				}
			}
		}
		
		if (update) {
			file.delete();
			tempFile.renameTo(file);
		} else if (!FileUtils.contentEquals(file, tempFile)) {
			System.out.println("    > Detected difference in file!");
			System.exit(-1);
		}
	}
	
	private String loadCodeSample(String filename, int startingLine, int endingLine) throws IOException {
		List<String> lines = new ArrayList<String>();
		
		// load the requested lines
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line = null;
			int lineCount = 0;
			
			while ((line = reader.readLine()) != null) {
				lineCount++;

				if (lineCount >= startingLine && lineCount <= endingLine) {
					lines.add(line);
				}
			}
		}
		
		// strip any indentation - can use String#stripIndent() after updating to newer Java version
		boolean stripFirstChar = true;
		
		while (stripFirstChar) {
			char charToRemove = lines.get(0).charAt(0);
			
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
		
		// convert to string
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < lines.size(); i++) {
			if (i > 0) {
				sb.append(System.lineSeparator());
			}
			
			sb.append(lines.get(i));
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		boolean update = args.length > 0 && args[0].equals("update");
		new UpdateDocs(DEFAULT_PATH, update).run();
	}

}
