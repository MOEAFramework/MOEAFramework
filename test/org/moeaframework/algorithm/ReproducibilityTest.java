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
	 * Tests CMA-ES for reproducibility.
	 */
	@Test
	public void testCMAES() {
		test("CMA-ES");
	}
	
	/**
	 * Tests SPEA2 for reproducibility.
	 */
	@Test
	public void testSPEA2() {
		test("SPEA2");
	}
	
	/**
	 * Tests PAES for reproducibility.
	 */
	@Test
	public void testPAES() {
		test("PAES");
	}
	
	/**
	 * Tests PESA2 for reproducibility.
	 */
	@Test
	public void testPESA2() {
		test("PESA2");
	}
	
	/**
	 * Tests OMOPSO for reproducibility.
	 */
	@Test
	public void testOMOPSO() {
		test("OMOPSO");
	}
	
	/**
	 * Tests SMPSO for reproducibility.
	 */
	@Test
	public void testSMPSO() {
		test("SMPSO");
	}
	
	/**
	 * Tests IBEA for reproducibility.
	 */
	@Test
	public void testIBEA() {
		test("IBEA");
	}

	/**
	 * Tests SMSEMOA for reproducibility.
	 */
	@Test
	public void testSMSEMOA() {
		test("SMS-EMOA");
	}
	
	/**
	 * Tests VEGA for reproducibility.
	 */
	@Test
	public void testVEGA() {
		test("VEGA");
	}

	/**
	 * Tests RVEA for reproducibility.
	 */
	@Test
	public void testRVEA() {
		test("RVEA");
	}
	
	/**
	 * Tests random search for reproducibility.
	 */
	@Test
	public void testRandom() {
		test("Random");
	}
	
	/**
	 * Tests GA for reproducibility.
	 */
	@Test
	public void testGA() {
		test("GA");
	}
	
	/**
	 * Tests ES for reproducibility.
	 */
	@Test
	public void testES() {
		test("ES");
	}
	
	/**
	 * Tests DE for reproducibility.
	 */
	@Test
	public void testDE() {
		test("DE");
	}
	
	/**
	 * Tests RSO for reproducibility.
	 */
	@Test
	public void testRSO() {
		test("RSO");
	}
	
	/**
	 * Tests MSOPS for reproducibility.
	 */
	@Test
	public void testMSOPS() {
		test("MSOPS");
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
