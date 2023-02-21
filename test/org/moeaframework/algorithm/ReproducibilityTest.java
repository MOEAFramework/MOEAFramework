/* Copyright 2009-2023 David Hadka
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.IgnoreOnCI;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Tests each of the built in algorithms to ensure reusing random seeds results
 * in reproducible results. This also serves to test the accessibility of
 * the built in algorithms via {@link AlgorithmFactory} and their basic use.
 */
@RunWith(CIRunner.class)
public class ReproducibilityTest {
	
	@Test
	public void testNSGAII() {
		test("NSGAII");
	}
	
	@Test
	public void testNSGAIII() {
		test("NSGAIII");
	}

	@Test
	public void testMOEAD() {
		test("MOEAD");
	}

	@Test
	public void testGDE3() {
		test("GDE3");
	}

	@Test
	public void testEpsilonNSGAII() {
		test("eNSGAII");
	}

	@Test
	public void testEpsilonMOEA() {
		test("eMOEA");
	}
	
	@Test
	public void testCMAES() {
		test("CMA-ES");
	}

	@Test
	public void testSPEA2() {
		test("SPEA2");
	}
	
	@Test
	public void testPAES() {
		test("PAES");
	}

	@Test
	public void testPESA2() {
		test("PESA2");
	}

	@Test
	public void testOMOPSO() {
		test("OMOPSO");
	}

	@Test
	@IgnoreOnCI("Failing on CI with zulu distribution, passing elsewhere")
	public void testSMPSO() {
		test("SMPSO");
	}

	@Test
	public void testIBEA() {
		test("IBEA");
	}

	@Test
	public void testSMSEMOA() {
		test("SMS-EMOA");
	}

	@Test
	public void testVEGA() {
		test("VEGA");
	}

	@Test
	public void testRVEA() {
		test("RVEA");
	}

	@Test
	public void testRandom() {
		test("Random");
	}
	
	@Test
	public void testGA() {
		test("GA");
	}
	
	@Test
	public void testES() {
		test("ES");
	}
	
	@Test
	public void testDE() {
		test("DE");
	}

	@Test
	public void testRSO() {
		test("RSO");
	}

	@Test
	public void testMSOPS() {
		test("MSOPS");
	}
	
	@Test
	public void testAMOSA() {
		test("AMOSA");
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
					algorithmName, new TypedProperties(), problem);

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
