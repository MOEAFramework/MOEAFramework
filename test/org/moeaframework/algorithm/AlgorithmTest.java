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
package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

/**
 * Methods for comparing two algorithm implementations statistically.
 */
public abstract class AlgorithmTest {
	
	/**
	 * The number of function evaluations per seed.
	 */
	public static final int MAX_EVALUATIONS = 10000;
	
	/**
	 * The number of seeds to run each algorithm.
	 */
	public static final int SEEDS = 10;
	
	/**
	 * The number of quality indicators that must have similar or better performance in order to pass the test.
	 */
	public static final int THRESHOLD = 5;
	
	/**
	 * Call from any test to skip if JMetal does not exist.
	 */
	public void assumeJMetalExists() {
		Assume.assumeTrue("JMetal-Plugin required to run test", 
				AlgorithmFactory.getInstance().hasProvider("org.moeaframework.algorithm.jmetal.JMetalAlgorithms"));
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default {@link AlgorithmFactory} is used to create
	 * instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 */
	public void test(String problem, String algorithm1, String algorithm2) {
		test(problem, algorithm1, algorithm2, false);
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default {@link AlgorithmFactory} is used to create
	 * instances.
	 * 
	 * @param problemName the name of the problem to test
	 * @param algorithm1Name the name of the first algorithm to test
	 * @param algorithm2Name the name of the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework algorithm exceeds the performance
	 */
	public void test(String problemName, String algorithm1Name, String algorithm2Name,
			boolean allowBetterPerformance) {
		test(problemName, algorithm1Name, new TypedProperties(), algorithm2Name, new TypedProperties(),
				allowBetterPerformance, AlgorithmFactory.getInstance());
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problemName the name of the problem to test
	 * @param algorithm1Name the name of the first algorithm to test
	 * @param properties1 the properties used by the first algorithm to test
	 * @param algorithm2Name the name of the second algorithm to test
	 * @param properties2 the properties used by the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework algorithm exceeds the performance
	 * @param factory the factory used to construct the algorithms
	 */
	public void test(String problemName, String algorithm1Name, TypedProperties properties1, String algorithm2Name,
			TypedProperties properties2, boolean allowBetterPerformance, AlgorithmFactory factory) {
		// if running the same algorithm with different settings, differentiate the names
		String suffix1 = "";
		String suffix2 = "";
		
		if (algorithm1Name.equalsIgnoreCase(algorithm2Name)) {
			suffix1 = " (1)";
			suffix2 = " (2)";
		}
		
		// JMetal-Plugin needs to know the max evaluations ahead of time, so always pass it along
		if (algorithm1Name.endsWith("-JMetal")) {
			properties1.setInt("maxEvaluations", MAX_EVALUATIONS);
		}
		
		if (algorithm2Name.endsWith("-JMetal")) {
			properties2.setInt("maxEvaluations", MAX_EVALUATIONS);
		}
		
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
		Indicators indicators = Indicators.all(problem, referenceSet);
		
		List<NondominatedPopulation> results1 = new ArrayList<>();
		List<NondominatedPopulation> results2 = new ArrayList<>();
		
		for (int seed = 0; seed < SEEDS; seed++) {
			Algorithm algorithm1 = factory.getAlgorithm(algorithm1Name, properties1, problem);
			algorithm1.run(MAX_EVALUATIONS);
			results1.add(algorithm1.getResult());
			
			Algorithm algorithm2 = factory.getAlgorithm(algorithm2Name, properties2, problem);
			algorithm2.run(MAX_EVALUATIONS);
			results2.add(algorithm2.getResult());
		}
		
		int equivalentIndicators = 0;
		
		for (StandardIndicator indicator : indicators.getSelectedIndicators()) {
			IndicatorStatistics statistics = new IndicatorStatistics(indicators.getIndicator(indicator));
			statistics.addAll(algorithm1Name + suffix1, results1);
			statistics.addAll(algorithm2Name + suffix2, results2);
				
			if (Settings.isVerbose()) {
				System.out.println(indicator.name() + ":");
				statistics.display();
				System.out.println();
			}
		
			if (statistics.getStatisticallySimilar(algorithm1Name + suffix1, 0.05).isEmpty()) {
				if (allowBetterPerformance) {
					double median1 = statistics.getMedian(algorithm1Name + suffix1);
					double median2 = statistics.getMedian(algorithm2Name + suffix2);
					
					equivalentIndicators += indicator.areLargerValuesPreferred() ?
							(median1 >= median2 ? 1 : 0) : 
							(median1 <= median2 ? 1 : 0);
				}
			} else {	
				equivalentIndicators += 1;
			}
		}
		
		if (equivalentIndicators < THRESHOLD) {
			Assert.fail("Detected statistical difference in results, only " + equivalentIndicators +
					" indicators showed similar results, requires " + THRESHOLD + " to pass");
		}
	}

}
