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
package org.moeaframework.examples.experiment;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.analysis.sample.SampledResults;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.stream.Groupings;
import org.moeaframework.analysis.stream.Measures;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Generates a control map plot showing the effects of population size and the number of function evaluations.
 */
public class PlotControlMap {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		
		// Enumerating the population size and max evaluations, running 10 seeds of each
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
		Enumeration<Integer> maxEvaluations = Parameter.named("maxEvaluations").asInt().range(1000, 10000, 1000);
		Enumeration<Long> seeds = Parameter.named("seed").asLong().random(0, Long.MAX_VALUE, 10);
		
		// Enumerate the samples
		ParameterSet parameters = new ParameterSet(populationSize, maxEvaluations, seeds);
		Samples samples = parameters.enumerate();
		
		// Evaluate each sample
		SampledResults<NondominatedPopulation> results = samples.evaluateAll(sample -> {
			System.out.println("Running " + sample.getReference());
			PRNG.setSeed(seeds.readValue(sample));
			
			NSGAII algorithm = new NSGAII(problem);
			algorithm.applyConfiguration(sample);
			algorithm.run(maxEvaluations.readValue(sample));
			return algorithm.getResult();
		});
		
		// Calculate the hypervolume and create a 2D grouping of the average value
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));
		
		Partition<Pair<Integer, Integer>, Double> controlMap = results
			.map(hypervolume::evaluate)
			.groupBy(Groupings.pair(Groupings.bucket(populationSize, 10), Groupings.bucket(maxEvaluations, 1000)))
			.measureEach(Measures.average());
		
		new Plot()
			.heatMap("Hypervolume", controlMap)
			.setXLabel(populationSize.getName())
			.setYLabel(maxEvaluations.getName())
			.show();
	}

}
