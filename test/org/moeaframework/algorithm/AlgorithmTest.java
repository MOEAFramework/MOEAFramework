/* Copyright 2009-2019 David Hadka
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

import java.util.Properties;

import org.junit.Assert;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.spi.AlgorithmFactory;

/**
 * Methods for comparing two algorithm implementations statistically.
 */
public abstract class AlgorithmTest {
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default
	 * {@link AlgorithmFactory} is used to create instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 */
	public void test(String problem, String algorithm1, String algorithm2) {
		test(problem, algorithm1, algorithm2, false,
				AlgorithmFactory.getInstance());
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default
	 * {@link AlgorithmFactory} is used to create instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework
	 *        algorithm exceeds the performance
	 */
	public void test(String problem, String algorithm1, String algorithm2,
			boolean allowBetterPerformance) {
		test(problem, algorithm1, algorithm2, allowBetterPerformance,
				AlgorithmFactory.getInstance());
	}

	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param factory the factory used to construct the algorithms
	 */
	public void test(String problem, String algorithm1, String algorithm2, 
			AlgorithmFactory factory) {
		test(problem, algorithm1, algorithm2, false, factory);
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework
	 *        algorithm exceeds the performance
	 * @param factory the factory used to construct the algorithms
	 */
	public void test(String problem, String algorithm1, String algorithm2, 
			boolean allowBetterPerformance, AlgorithmFactory factory) {
		test(problem, algorithm1, new Properties(), algorithm2,
				new Properties(), allowBetterPerformance, factory);
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param properties1 the properties used by the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param properties2 the properties used by the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework
	 *        algorithm exceeds the performance
	 * @param factory the factory used to construct the algorithms
	 */
	public void test(String problem, String algorithm1, Properties properties1,
			String algorithm2, Properties properties2,
			boolean allowBetterPerformance, AlgorithmFactory factory) {
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeAllMetrics()
				.showAggregate()
				.showStatisticalSignificance();
		
		Executor executor = new Executor()
				.withProblem(problem)
				.usingAlgorithmFactory(factory)
				.distributeOnAllCores();
		
		analyzer.addAll("A", 
				executor.withAlgorithm(algorithm1)
						.withProperties(properties1)
						.withMaxEvaluations(10000)
						.runSeeds(10));
		analyzer.addAll("B", 
				executor.withAlgorithm(algorithm2)
						.withProperties(properties2)
						.withMaxEvaluations(10000)
						.runSeeds(10));
		
		Analyzer.AnalyzerResults analyzerResults = analyzer.getAnalysis();
		Analyzer.AlgorithmResult algorithmResult =
				analyzerResults.get("A");

		int indifferences = 0;
		
		for (String indicator : algorithmResult.getIndicators()) {
			indifferences += algorithmResult.get(indicator)
					.getIndifferentAlgorithms().size();
		}
		
		if (indifferences < 5) {
			if (allowBetterPerformance) {
				int outperformance = 0;
				
				for (String indicator : algorithmResult.getIndicators()) {
					double value1 = analyzerResults.get("A")
							.get(indicator).getMedian();
					double value2 = analyzerResults.get("B")
							.get(indicator).getMedian();
					
					if (indicator.equals("Spacing") ||
							indicator.equals("Hypervolume") ||
							indicator.equals("Contribution") ||
							indicator.equals("R1Indicator")) {
						if (value1 >= value2) {
							outperformance++;
						}
					} else {
						if (value1 <= value2) {
							outperformance++;
						}
					}
				}

				if (outperformance < 5) {
					Assert.fail("algorithms show different performance");
				}
			} else {
				Assert.fail("algorithms show statistical difference");
			}
		}
	}

}
