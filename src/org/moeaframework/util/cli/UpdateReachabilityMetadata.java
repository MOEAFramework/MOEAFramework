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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.analysis.tools.Main;
import org.moeaframework.analysis.tools.ResultFileViewer;

/**
 * Updates META-INF/native-image/reachability-metadata.json with latest resource and class reflection configuration.
 */
public class UpdateReachabilityMetadata extends CommandLineUtility {
	
	private UpdateReachabilityMetadata() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder()
				.longOpt("exclude-gui")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		List<Class<?>> reflections = scanReflections(commandLine.hasOption("exclude-gui"));
		List<Path> resources = scanResources();

		Files.createDirectories(Path.of("META-INF/native-image"));

		try (PrintWriter writer = new PrintWriter("META-INF/native-image/reachability-metadata.json")) {
			writer.println("{");
			writer.println("  \"reflection\": [");
			
			for (int i = 0; i < reflections.size(); i++) {
				writer.println("    {");
				writer.print("      \"type\": \"");
				writer.print(reflections.get(i).getCanonicalName());
				writer.println("\",");
				writer.println("      \"allDeclaredConstructors\": true,");
				writer.println("      \"allPublicConstructors\": true,");
				writer.println("      \"allDeclaredMethods\": true,");
				writer.println("      \"allPublicMethods\": true");
				writer.println("    },");
			}
			
			writer.println("    {");
			writer.println("      \"type\": \"org.moeaframework.algorithm.extension.ProgressExtension$ProgressListener[]\",");
			writer.println("      \"unsafeAllocated\": true");
			writer.println("    },");
			writer.println("    {");
			writer.println("      \"type\": {");
			writer.println("        \"proxy\": [\"org.moeaframework.algorithm.extension.ProgressExtension$ProgressListener\"]");
			writer.println("      }");
			writer.println("    }");
			writer.println("  ],");
			writer.println("  \"resources\": [");
			
			for (int i = 0; i < resources.size(); i++) {
				writer.println("    {");
				writer.print("      \"glob\": \"");
				writer.print(resources.get(i).toString().replace('\\', '/'));
				writer.println("\"");
				writer.print("    }");
				
				if (i < resources.size() - 1) {
					writer.println(",");
				} else {
					writer.println();
				}
			}
			
			writer.println("  ],");
			writer.println("  \"bundles\": []");
			writer.println("}");
		}
	}
	
	private static List<Path> scanResources() throws IOException {
		List<Path> result = new ArrayList<>();
		
		try (Stream<Path> stream = Files.walk(Path.of("META-INF"))) {
			result.addAll(stream
				.filter(p -> !Files.isDirectory(p))
				.toList());
		}
		
		try (Stream<Path> stream = Files.walk(Path.of("src"))) {
			result.addAll(stream
				.filter(p -> !Files.isDirectory(p))
				.filter(p -> !FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
				.map(p -> p.subpath(1, p.getNameCount()))
				.toList());
		}
		
		// TODO: Better filtering
		result.remove(Path.of("META-INF/native-image/reachability-metadata.json"));
		result.remove(Path.of("cli.sh"));
		result.remove(Path.of("cli.cmd"));
		result.remove(Path.of("pom.xml.template"));
		result.remove(Path.of("macros.xml"));
		result.remove(Path.of("README.md.template"));
		result.remove(Path.of("overview.html"));
		
		return result;
	}
	
	private static List<Class<?>> scanReflections(boolean excludeGui) throws Exception {
		List<Class<?>> result = new ArrayList<>();
		result.addAll(scanReflectionClasses(Main.class));
		
		if (excludeGui) {
			result.remove(LaunchDiagnosticTool.class);
			result.remove(ResultFileViewer.class);
		}
		
		return result;
	}
	
	private static List<Class<?>> scanReflectionClasses(Class<? extends CommandLineUtility> type) throws Exception {
		List<Class<?>> result = new ArrayList<>();
		result.add(type);
		
		Constructor<? extends CommandLineUtility> constructor = type.getDeclaredConstructor();
		constructor.setAccessible(true);
			
		CommandLineUtility commandInstance = constructor.newInstance();
		
		for (Command command : commandInstance.getCommands()) {
			result.addAll(scanReflectionClasses(command.getImplementation()));
		}
		
		return result;
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new UpdateReachabilityMetadata().start(args);
	}

}
