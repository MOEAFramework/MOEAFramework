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
package org.moeaframework.examples.experiment;

import java.io.File;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * A "result file" is a file format used to store approximation sets along with any associated metadata.
 * They are designed to store large amounts of data, and in particular, allow resuming from any interrupted
 * runs.  Observe below how we check {@code writer.getNumberOfEntries()} to determine the starting point.
 * 
 * In this example, we store the results from solving UF1 with NSGA-II with various population sizes, then
 * compute the hypervolume metric for those results. Try running this example multiple times to see it resume
 * from any previous run.
 */
public class ResultFileExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new UF1();
		File resultFile = new File("NSGAII_UF1_ResultFile.txt");

		// First, solve UF1 using NSGA-II using various population sizes.  We store the result and configuration
		// to the result file.
		try (ResultFileWriter writer = ResultFileWriter.append(problem, resultFile)) {
			int startingPopulationSize = 100;
			int endingPopulationSize = 1000;
			
			if (writer.getNumberOfEntries() > 0) {
				System.out.println("Resuming from previous run!");
				startingPopulationSize = 100 * (writer.getNumberOfEntries() + 1);
			}

			for (int populationSize = startingPopulationSize; populationSize <= endingPopulationSize; populationSize += 100) {
				System.out.println("Solving UF1 using NSGA-II with populationSize=" + populationSize);
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.setInitialPopulationSize(populationSize);
				algorithm.run(100000);
				
				writer.write(new ResultEntry(algorithm.getResult(), algorithm.getConfiguration()));
			}
		}
		
		// Then, we can process the file to compute the Hypervolume metric.
		try (ResultFileReader reader = ResultFileReader.open(problem, resultFile)) {
			Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("./pf/UF1.dat"));
			
			while (reader.hasNext()) {
				ResultEntry entry = reader.next();
				
				double value = hypervolume.evaluate(new NondominatedPopulation(entry.getPopulation()));
				int populationSize = entry.getProperties().getInt("populationSize");
				
				System.out.println("Hypervolume for populationSize=" + populationSize + " => " + value);
			}
		}
	}

}
