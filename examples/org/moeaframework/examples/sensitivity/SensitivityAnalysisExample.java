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
package org.moeaframework.examples.sensitivity;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.plot.SensitivityPlotBuilder;
import org.moeaframework.analysis.sample.SampledResults;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.sensitivity.SobolSensitivityAnalysis;
import org.moeaframework.analysis.sensitivity.SobolSensitivityAnalysis.SobolSensitivityResult;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates performing sensitivity analysis.
 */
public class SensitivityAnalysisExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));
		
		// configure the parameters being analyzed
		Parameter<Integer> populationSize = Parameter.named("populationSize").asInt().sampledBetween(10, 500);
		Parameter<Double> sbxRate = Parameter.named("sbx.rate").asDouble().sampledBetween(0.0, 1.0);
		Parameter<Double> sbxDistributionIndex = Parameter.named("sbx.distributionIndex").asDouble().sampledBetween(1.0, 50.0);
		Parameter<Double> pmRate = Parameter.named("pm.rate").asDouble().sampledBetween(0.0, 1.0);
		Parameter<Double> pmDistributionIndex = Parameter.named("pm.distributionIndex").asDouble().sampledBetween(1.0, 50.0);
		
		ParameterSet parameters = new ParameterSet(populationSize, sbxRate, sbxDistributionIndex, pmRate, pmDistributionIndex);
		
		// generate the samples
		SobolSensitivityAnalysis sensitivityAnalysis = new SobolSensitivityAnalysis(parameters, 500);
		
		Samples samples = sensitivityAnalysis.generateSamples();
		
		// run the algorithm with each sample, computing the hypervolume
		SampledResults<Double> results = samples.evaluateAll(sample -> {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.applyConfiguration(sample);
			algorithm.run(10000);
			
			return hypervolume.evaluate(algorithm.getResult());
		});
		
		// calculate and display the sensitivity analysis results
		SobolSensitivityResult sensitivityResults = sensitivityAnalysis.evaluate(results);
		
		sensitivityResults.display();
		
		new SensitivityPlotBuilder(sensitivityResults).show();
	}

}
