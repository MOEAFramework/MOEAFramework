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
package org.moeaframework.examples.plots;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.SampledResults;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Generates a control map plot showing the effects of Max Evaluations and Population Size parameters on the
 * performance of NSGA-II when solving the 2-objective DTLZ2 problem.
 */
public class PlotControlMap {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.loadReferenceSet("./pf/DTLZ2.2D.pf"));
		
		Enumeration<Integer> maxEvaluations = Parameter.named("maxEvaluations").asInt().range(100, 5000, 100);
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(2, 100, 4);

		ParameterSet parameters = new ParameterSet(maxEvaluations, populationSize);
		Samples samples = parameters.enumerate();
		
		SampledResults<Double> results = new SampledResults<>(parameters);
				
		for (Sample sample : samples) {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.setInitialPopulationSize(sample.get(populationSize));
			algorithm.run(sample.get(maxEvaluations));
			
			results.add(sample, hypervolume.evaluate(algorithm.getResult()));
		}
		
		// TODO: Return a better data type?
		Triple<List<Integer>, List<Integer>, List<List<Double>>> projection =
				results.project(maxEvaluations, populationSize);
		
		new Plot()
			.heatMap("Hypervolume", projection.getLeft(), projection.getMiddle(), projection.getRight())
			.setXLabel("Max Evaluations")
			.setYLabel("Population Size")
			.show();
	}

}
