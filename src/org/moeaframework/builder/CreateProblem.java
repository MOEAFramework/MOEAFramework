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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.SourceVersion;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.text.StringSubstitutor;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line tool for creating new natively-compiled problems.  This tool will create a folder containing all the
 * files needed to write and compile the problem, and package them in a JAR file that can be used within the MOEA
 * Framework.
 */
public class CreateProblem extends CommandLineUtility {
	
	// TODO: Take an optional package argument and structure the Java files correctly
	
	/**
	 * The supported language options.
	 */
	public static final List<String> LANGUAGES = List.of("c");
	
	/**
	 * Creates a new instance of this command line tool.
	 */
	public CreateProblem() {
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
		
		options.addOption(Option.builder("f")
				.longOpt("functionName")
				.hasArg()
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String problemName = commandLine.getOptionValue("problemName");
		String language = commandLine.getOptionValue("language").toLowerCase();
		String functionName = commandLine.getOptionValue("functionName", "evaluate");

		if (!SourceVersion.isIdentifier(problemName)) {
			throw new FrameworkException("'" + problemName + "' is not a valid Java class name");
		}

		if (!SourceVersion.isIdentifier(problemName)) {
			throw new FrameworkException("'" + functionName + "' is not a valid function name");
		}
		
		if (!LANGUAGES.contains(language)) {
			throw new FrameworkException("'" + language + "' is not a supported language");
		}

		Map<String, Object> mappings = new HashMap<>();
		mappings.put("problemName", problemName);
		mappings.put("functionName", functionName);
		mappings.put("language", language);
		mappings.put("numberOfVariables", Integer.parseInt(commandLine.getOptionValue("numberOfVariables")));
		mappings.put("numberOfObjectives", Integer.parseInt(commandLine.getOptionValue("numberOfObjectives")));
		mappings.put("numberOfConstraints", Integer.parseInt(commandLine.getOptionValue("numberOfConstraints", "0")));
		mappings.put("java.home", System.getProperty("java.home"));

		StringSubstitutor substitutor = new StringSubstitutor(mappings);
		substitutor.setEnableSubstitutionInVariables(true);

		Path directory = Path.of("native", problemName);
		
		if (directory.toFile().exists()) {
			throw new FrameworkException(directory + " already exists, delete this folder or choose a different name");
		}
		
		processManifest(Path.of("org", "moeaframework", "builder", language), directory, substitutor);
		
		System.out.println(problemName + " created in " + directory + ".  To use:");
		System.out.println("  1. Go to this directory, edit the source files and implement your problem");
		System.out.println("  2. Run 'make' from this directory");
		System.out.println("  3. Copy '" + problemName + ".jar' into the MOEA Framework lib/ directory");
		System.out.println("  4. Add '" + problemName + ".jar to the Java classpath");
		System.out.println("  5. Write a program using this problem, for example:");
		System.out.println();
		System.out.println("         Problem problem = new " + problemName + "();");
		System.out.println();
		System.out.println("         NSGAII algorithm = new NSGAII(problem);");
		System.out.println("         algorithm.run(10000);");
		System.out.println();
		System.out.println("         algorithm.getResult().display();");
	}

	private void processManifest(Path root, Path targetDirectory, StringSubstitutor substitutor) throws IOException {
		String manifest = loadResourceAsString(root.resolve("Manifest").toString());
		manifest = substitutor.replace(manifest);

		for (String line : manifest.lines().toList()) {
			String[] tokens = line.split("\\s*\\->\\s*");
			Path source = root.resolve(tokens[0]);
			Path target = targetDirectory.resolve(tokens[1]);

			Files.createDirectories(target.getParent());

			extractFile(source, target, substitutor);
		}
	}

	private void extractFile(Path path, Path targetFile, StringSubstitutor substitutor) throws IOException {
		String content = loadResourceAsString(path.toString());
		content = substitutor.replace(content);
		Files.writeString(targetFile, content, StandardCharsets.UTF_8);
	}

	private String loadResourceAsString(String path) throws IOException {
		path = path.replaceAll("\\\\", "/");

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		return Files.readString(file.toPath(), StandardCharsets.UTF_8);
	}

	/**
	 * Starts this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new CreateProblem().start(args);
	}

}