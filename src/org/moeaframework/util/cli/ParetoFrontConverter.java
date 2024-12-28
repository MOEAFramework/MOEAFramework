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

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.io.ResultWriter;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.core.spi.RegisteredProblemProvider;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.SerializationUtils;
import org.moeaframework.util.io.LineReader;

/**
 * Command line utility for converting legacy formatted Pareto front files into the new result file format.
 */
public class ParetoFrontConverter extends CommandLineUtility  {
	
	private ParetoFrontConverter() {
		super();
	}
	
	public void run(CommandLine commandLine) throws Exception {
		Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, true);
		
		ServiceLoader<ProblemProvider> providers = ServiceLoader.load(ProblemProvider.class);
		
		for (ProblemProvider provider : providers) {
			if (provider instanceof RegisteredProblemProvider registeredProvider) {
				Field referenceSetField = FieldUtils.getField(registeredProvider.getClass(), "referenceSetMap", true);
				
				Map<String, String> referenceSetMap = SerializationUtils.castMap(String.class, String.class,
						HashMap::new, referenceSetField.get(registeredProvider));
				
				for (String problemName : referenceSetMap.keySet()) {					
					Problem problem = registeredProvider.getProblem(problemName);
					String referenceSet = referenceSetMap.get(problemName);
					
					if (referenceSet == null) {
						continue;
					}
					
					System.out.print("Converting " + problemName + " (" + referenceSet + ")...");
					
					File referenceSetFile = new File(referenceSet);
					File intermediateFile = File.createTempFile("intermediate", "pf");
					File resultFile = File.createTempFile("result", "pf");
					
					// pre-process file to clean up any whitespace (e.g. replace '\t' with ' ') and duplicate lines
					try (LineReader reader = LineReader.wrap(new FileReader(referenceSetFile)).skipComments().skipBlanks().trim();
							PrintWriter writer = new PrintWriter(intermediateFile)) {
						Set<String> lines = new HashSet<>();
						
						while (reader.hasNext()) {
							String line = reader.next();
							String newLine = line.replaceAll("\\s+", " ");
							
							if (!line.equals(newLine)) {
								System.out.println("  > Replaced '" + line + "' with '" + newLine + "'");
							}
							
							if (lines.contains(newLine)) {
								System.out.println("  > Skipping '" + line + "', duplicate line");
								continue;
							}
							
							writer.println(newLine);
							lines.add(newLine);
						}
					}
					
					// convert to result file
					try (ResultFileReader reader = ResultFileReader.openLegacy(problem, intermediateFile);
							ResultFileWriter writer = ResultFileWriter.open(problem, resultFile)) {
						writer.write(new ResultEntry(stripVariablesAndConstraints(reader.next().getPopulation())));
					}
					
					ResultWriter.replace(resultFile, referenceSetFile);
					
					System.out.println("done!");
				}
			}
		}
	}
	
	private static Population stripVariablesAndConstraints(Population population) {
		Population result = new Population();
		
		for (Solution solution : population) {
			Solution newSolution = new Solution(0, solution.getNumberOfObjectives());
			
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				newSolution.setObjective(i, solution.getObjective(i));
			}
			
			result.add(newSolution);
		}
		
		return result;
	}
	
	/**
	 * Starts the command line utility for converting Pareto front files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ParetoFrontConverter().start(args);
	}

}