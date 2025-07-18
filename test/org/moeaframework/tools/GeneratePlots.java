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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for generating plots shown in docs.  Requires {@code gnuplot}.
 */
public class GeneratePlots extends CommandLineUtility {
	
	private static final Path IMAGES_PATH = Path.of("docs/imgs/pf/");
	private static final Path PF_PATH = Path.of("pf/");

	private GeneratePlots() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, true);
		
		try (Stream<Path> stream = Files.walk(PF_PATH)) {
			stream
				.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("pf"))
				.forEach(p -> generatePlot(p));
		}
	}

	private static void generatePlot(Path path) {
		try {
			System.out.print("Processing " + path + "...");
			
			NondominatedPopulation referenceSet = NondominatedPopulation.load(path.toFile());
			File tempFile = File.createTempFile("plot", "pf");
			
			try {
				int numberOfObjectives = referenceSet.get(0).getNumberOfObjectives();
				
				if (numberOfObjectives < 2 || numberOfObjectives > 3) {
					System.out.println("skipped (unsupported number of objectives)");
					return;
				}
				
				try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
					for (Solution solution : referenceSet) {
						for (int i = 0; i < numberOfObjectives; i++) {
							if (i > 0) {
								writer.print(" ");
							}
							
							writer.print(solution.getObjectiveValue(i));
						}
						
						writer.println();
					}
				}
				
				ProcessBuilder builder = new ProcessBuilder("gnuplot");
				builder.redirectOutput(Redirect.INHERIT);
				builder.redirectError(Redirect.INHERIT);
				
				Process process = builder.start();
				
				try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream()))) {
					writer.print("set term png size ");
					writer.print(numberOfObjectives == 2 ? "320,240" : "480,360");
					writer.println();
					writer.print("set format x ''");
					writer.println();
					writer.print("set format y ''");
					writer.println();
					
					if (numberOfObjectives == 3) {
						writer.print("set format z ''");
						writer.println();
						writer.print("set ticslevel 0");
						writer.println();
						writer.print("set view 60,100,1.2,1.2");
						writer.println();
					}
					
					writer.print("set output '");
					writer.print(IMAGES_PATH.resolve(FilenameUtils.getBaseName(path.getFileName().toString()) + ".png").toString());
					writer.print("'");
					writer.println();
					
					if (numberOfObjectives == 2) {
						writer.print("plot '");
						writer.print(path.toString());
						writer.print("' with points pointtype 7 lc rgb \"black\" notitle");
					} else {
						writer.print("splot '");
						writer.print(path.toString());
						writer.print("' with points pointtype 7 lc rgb \"black\" notitle");
					}

					writer.println();
				}
			} finally {
				tempFile.delete();
			}
			
			System.out.println("done!");
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to generate plot for " + path, e);
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new GeneratePlots().start(args);
	}

}
