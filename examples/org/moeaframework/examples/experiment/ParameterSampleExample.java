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

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.SampledResults;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.stream.Groupings;
import org.moeaframework.analysis.stream.Measures;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates using parameters to run an algorithm with different inputs, collect the results, and generate a plot.
 */
public class ParameterSampleExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		
		// Sampling population size with 10 random seeds
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
		Enumeration<Long> seed = Parameter.named("seed").asLong().random(0, Long.MAX_VALUE, 10);
		
		// Enumerate the samples
		ParameterSet parameters = new ParameterSet(populationSize, seed);
		Samples samples = parameters.enumerate();
		
		// Use streams to evaluate and analyze the results	
		SampledResults<NondominatedPopulation> results = new SampledResults<>(parameters);
				
		for (Sample sample : samples) {
			PRNG.setSeed(sample.getLong("seed"));
			
			NSGAII algorithm = new NSGAII(problem);
			algorithm.applyConfiguration(sample);
			algorithm.run(10000);
			
			results.add(sample, algorithm.getResult());
		}
										
		// Calculate the average hypervolume for population size
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));

		Partition<Integer, Double> avgHypervolume = results
				.map(hypervolume::evaluate)
				.groupBy(Groupings.exactValue(populationSize))
				.measureEach(Measures.average())
				.sorted();
		
		// Display plot
		new Plot()
			.line("Average", avgHypervolume)
			.setXLabel("Population Size")
			.setYLabel("Hypervolume")
			.show();
	}

}
