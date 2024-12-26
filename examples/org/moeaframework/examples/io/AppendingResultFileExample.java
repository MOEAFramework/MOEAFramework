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
package org.moeaframework.examples.io;

import java.io.File;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * Results files are resumable, meaning we can detect where any previous run left off and append new entries.  Note the
 * difference when using open, which overwrites any existing file, and append.
 * 
 * Try interrupting this example while it is running (e.g., press Ctrl+C) and run it a second time.  To restart from
 * the beginning, delete the result file.
 */
public class AppendingResultFileExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new UF1();
		File resultFile = new File("NSGAII_UF1_Append.txt");
		
		// Sample the population size from 100 to 1000
		Parameter<Integer> populationSize = Parameter.named("populationSize").asInt().range(100, 1000, 10);
		
		ParameterSet parameterSet = new ParameterSet(populationSize);
		Samples samples = parameterSet.enumerate();

		// Append to the result file, skipping any existing entries
		try (ResultFileWriter writer = ResultFileWriter.append(problem, resultFile)) {
			int existingEntries = writer.getNumberOfEntries();
			
			if (existingEntries > 0) {
				System.out.println("Appending to " + resultFile + ", resuming after " + existingEntries + " entries!");
			}

			for (Sample sample : samples.skip(existingEntries)) {
				System.out.println("Solving UF1 using NSGA-II with populationSize=" + populationSize.readValue(sample));
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.applyConfiguration(sample);
				algorithm.run(10000);
				
				writer.write(new ResultEntry(algorithm.getResult(), algorithm.getConfiguration()));
			}
		}
	}

}
