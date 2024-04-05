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
package org.moeaframework.builder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.lang.model.SourceVersion;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.text.StringSubstitutor;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.io.CommentedLineReader;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Command line tool for creating new natively-compiled problems.  This tool will create a folder containing all the
 * files needed to write and compile the problem, package everything into a JAR, and display instructions for using
 * the generated files.
 * <p>
 * To define a new language:
 * <ol>
 *   <li>Create a nested package with the name of the language.
 *   <li>Create a Manifest file with lines formatted as {@code <sourceFile> -> <destinationFile>}.  This controls how
 *       the files are extracted into the destination folder.
 *   <li>Create the individual files, typically with the extension {@code .template} to prevent compilation errors.
 *       These files can use {@code ${key}} string substitutions.
 *   <li>Add the name of the language to {@link #LANGUAGES}.
 * </ol>
 */
public class BuildProblem extends CommandLineUtility {
	
	/**
	 * The supported language options.
	 */
	public static final Map<String, String> LANGUAGES;
	
	/**
	 * The variable used in Makefiles specifying the platform-specific classpath separator.
	 */
	public static final String PATH_SEPARATOR = "$(SEPARATOR)";
	
	static {
		LANGUAGES = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		LANGUAGES.put("c", "c");
		LANGUAGES.put("cpp", "cpp");
		LANGUAGES.put("c++", "cpp");
		LANGUAGES.put("fortran", "fortran");
		LANGUAGES.put("java", "java");
		LANGUAGES.put("python", "python");
		LANGUAGES.put("external", "external");
	}
	
	/**
	 * Creates a new instance of this command line tool.
	 */
	public BuildProblem() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("p")
				.longOpt("problemName")
				.hasArg()
				.required()
				.build());
		
		options.addOption(Option.builder("l")
				.longOpt("language")
				.hasArg()
				.required()
				.build());
		
		options.addOption(Option.builder("n")
				.longOpt("numberOfVariables")
				.hasArg()
				.required()
				.build());
		
		options.addOption(Option.builder("o")
				.longOpt("numberOfObjectives")
				.hasArg()
				.required()
				.build());
		
		options.addOption(Option.builder("c")
				.longOpt("numberOfConstraints")
				.hasArg()
				.build());
		
		options.addOption(Option.builder("l")
				.longOpt("lowerBound")
				.hasArg()
				.build());
		
		options.addOption(Option.builder("u")
				.longOpt("upperBound")
				.hasArg()
				.build());
		
		options.addOption(Option.builder("f")
				.longOpt("functionName")
				.hasArg()
				.build());
		
		options.addOption(Option.builder("d")
				.longOpt("directory")
				.hasArg()
				.build());
		
		options.addOption(Option.builder()
				.longOpt("classpath")
				.hasArg()
				.build());
		
		options.addOption(Option.builder()
				.longOpt("overwrite")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String problemName = commandLine.getOptionValue("problemName");
		String language = commandLine.getOptionValue("language");
		String functionName = commandLine.getOptionValue("functionName", "evaluate");

		if (!SourceVersion.isIdentifier(problemName)) {
			throw new FrameworkException("'" + problemName + "' is not a valid Java class name");
		}
		
		if (problemName.equalsIgnoreCase("Example")) {
			throw new FrameworkException("'" + problemName +"' is reserved and can not be used as a problem name");
		}

		if (!SourceVersion.isIdentifier(functionName)) {
			throw new FrameworkException("'" + functionName + "' is not a valid function name");
		}
		
		if (LANGUAGES.containsKey(language)) {
			language = LANGUAGES.get(language);
		} else {
			throw new FrameworkException("'" + language + "' is not a supported language");
		}
		
		Path directory = Path.of(commandLine.getOptionValue("directory", "native"), problemName);
		
		if (directory.toFile().exists()) {
			if (commandLine.hasOption("overwrite")) {
				deleteDirectory(directory);
			} else {
				throw new FrameworkException(directory + " already exists, delete this folder or choose a different name");
			}
		}
		
		String[] classpath = new String[] {
				tryRelativize(directory, Path.of(".")).resolve("lib").normalize().toString() + File.separator + "*",
				tryRelativize(directory, Path.of(".")).resolve("bin").normalize().toString(),
				problemName + ".jar",
				"."
		};		

		Map<String, Object> mappings = new HashMap<>();
		mappings.put("problemName", problemName);
		mappings.put("functionName", functionName);
		mappings.put("language", language);
		mappings.put("numberOfVariables", Integer.parseInt(commandLine.getOptionValue("numberOfVariables")));
		mappings.put("numberOfObjectives", Integer.parseInt(commandLine.getOptionValue("numberOfObjectives")));
		mappings.put("numberOfConstraints", Integer.parseInt(commandLine.getOptionValue("numberOfConstraints", "0")));
		mappings.put("lowerBound", Double.parseDouble(commandLine.getOptionValue("lowerBound", "0.0")));
		mappings.put("upperBound", Double.parseDouble(commandLine.getOptionValue("upperBound", "1.0")));
		mappings.put("relativePath", tryRelativize(directory, Path.of(".")).toString());
		mappings.put("java.home", System.getProperty("java.home"));
		mappings.put("java.class.path", commandLine.getOptionValue("classpath", String.join(PATH_SEPARATOR, classpath)));

		StringSubstitutor substitutor = new StringSubstitutor(mappings);
		substitutor.setEnableSubstitutionInVariables(true);
		
		processManifest(Path.of(language), directory, substitutor);
		
		System.out.println(problemName + " created in " + directory + ".  To use:");
		System.out.println("  1. Go to this directory, edit the source files and implement your problem");
		System.out.println("  2. Run 'make' to compile and package the files");
		System.out.println("  3. Run 'make run' to run the example");
		System.out.println("  4. Add '" + problemName + ".jar' to the Java classpath, typically by placing the JAR in the lib/ folder");
	}

	private void processManifest(Path root, Path targetDirectory, StringSubstitutor substitutor) throws IOException {
		String manifest = loadResourceAsString(root, "Manifest");
		manifest = substitutor.replace(manifest);

		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(manifest))) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\s*\\->\\s*");
				String source = tokens[0];
				Path target = targetDirectory.resolve(tokens[1]);
	
				Files.createDirectories(target.getParent());
	
				extractFile(root, source, target, substitutor);
			}
		}
	}

	private void extractFile(Path root, String resource, Path targetFile, StringSubstitutor substitutor) throws IOException {
		String content = loadResourceAsString(root, resource);
		content = substitutor.replace(content);
		Files.writeString(targetFile, content, StandardCharsets.UTF_8);
	}
	
	private Path tryRelativize(Path first, Path second) {
		try {
			return first.relativize(second);
		} catch (IllegalArgumentException e) {
			return second.toAbsolutePath();
		}
	}
	
	static void deleteDirectory(Path directory) throws IOException {
		Files.walk(directory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

	private String loadResourceAsString(Path root, String resource) throws IOException {
		if (resource.startsWith("!")) {
			// references a file relative to the MOEA Framework root folder
			return Files.readString(new File(resource.substring(1)).toPath(), StandardCharsets.UTF_8);
		} else {
			return Resources.readString(getClass(), root.resolve(resource).toString(), ResourceOption.REQUIRED);
		}
	}

	/**
	 * Starts this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new BuildProblem().start(args);
	}

}
