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
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * The MOEA Framework stores results in a file format called a "result file".  This is a custom file format designed to
 * store one or more populations or approximation sets together with any metadata.  Here we demonstrate saving the
 * end-of-run results from sampling different population sizes, then using that result file to compute the hypervolume
 * metric.
 */
public class ResultFileExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new UF1();
		File resultFile = new File("NSGAII_UF1_Results.txt");
		
		// Sample the population size from 100 to 1000 with a step size of 50
		Parameter<Integer> populationSize = Parameter.named("populationSize").asInt().range(100, 1000, 50);
		
		ParameterSet parameterSet = new ParameterSet(populationSize);
		Samples samples = parameterSet.enumerate();

		// Run NSGA-II for each population size, storing the result to the file
		try (ResultFileWriter writer = ResultFileWriter.open(problem, resultFile)) {
			for (Sample sample : samples) {
				System.out.println("Solving UF1 using NSGA-II with populationSize=" + populationSize.readValue(sample));
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.applyConfiguration(sample);
				algorithm.run(10000);
				
				writer.write(new ResultEntry(algorithm.getResult(), algorithm.getConfiguration()));
			}
		}
		
		// Process the result file and evaluate the hypervolume
		try (ResultFileReader reader = ResultFileReader.open(problem, resultFile)) {
			Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("pf/UF1.pf"));
			
			while (reader.hasNext()) {
				ResultEntry entry = reader.next();
				
				double value = hypervolume.evaluate(new NondominatedPopulation(entry.getPopulation()));
				System.out.println("Hypervolume for populationSize=" + populationSize.readValue(entry.getProperties()) +
						" => " + value);
			}
		}
	}

}
