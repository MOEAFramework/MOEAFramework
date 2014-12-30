/* Copyright 2009-2015 David Hadka
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

import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests each of the standard algorithms to ensure reusing random seeds results
 * in reproducible results. This also serves to test the accessibility of
 * the standard algorithms via {@link AlgorithmFactory} and their basic use.
 */
public class ReproducibilityTest {

	/**
	 * Tests NSGAII for reproducibility.
	 */
	@Test
	public void testNSGAII() {
		test("NSGAII");
	}
	
	/**
	 * Tests NSGAIII for reproducibility.  Right now, since the weights and
	 * other settings aren't stored, NSGA-III isn't reproducible.
	 */
	@Test
	public void testNSGAIII() {
		test("NSGAIII");
	}

	/**
	 * Tests MOEA/D for reproducibility.
	 */
	@Test
	public void testMOEAD() {
		test("MOEAD");
	}

	/**
	 * Tests GDE3 for reproducibility.
	 */
	@Test
	public void testGDE3() {
		test("GDE3");
	}

	/**
	 * Tests &epsilon;-NSGA-II for reproducibility.
	 */
	@Test
	public void testEpsilonNSGAII() {
		test("eNSGAII");
	}

	/**
	 * Tests &epsilon;-MOEA for reproducibility.
	 */
	@Test
	public void testEpsilonMOEA() {
		test("eMOEA");
	}
	
	/**
	 * Tests random search for reproducibility.
	 */
	@Test
	public void testRandom() {
		test("Random");
	}

	/**
	 * Tests the algorithm for reproducibility. The algorithm must be available
	 * via the {@link AlgorithmFactory}.
	 * 
	 * @param algorithmName the algorithm name
	 */
	public void test(String algorithmName) {
		NondominatedPopulation resultSet = null;
		long seed = PRNG.getRandom().nextLong();

		for (int i = 0; i < 2; i++) {
			PRNG.setSeed(seed);

			Problem problem = ProblemFactory.getInstance().getProblem(
					"DTLZ2_2");
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					algorithmName, new Properties(), problem);

			while (!algorithm.isTerminated()
					&& (algorithm.getNumberOfEvaluations() < 10000)) {
				algorithm.step();
			}

			if (resultSet == null) {
				resultSet = algorithm.getResult();
			} else {
				TestUtils.assertEquals(resultSet, algorithm.getResult());
			}
		}
	}

}
