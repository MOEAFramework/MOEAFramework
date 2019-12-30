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

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.MockBinaryProblem;
import org.moeaframework.problem.MockPermutationProblem;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.problem.MockSubsetProblem;

/**
 * Tests the {@link StandardAlgorithms} class.
 */
public class StandardAlgorithmsTest {
	
	/**
	 * The real encoded test problem.
	 */
	protected Problem realProblem;
	
	/**
	 * The binary encoded test problem.
	 */
	protected Problem binaryProblem;
	
	/**
	 * The permutation test problem.
	 */
	protected Problem permutationProblem;
	
	/**
	 * The subset test problem.
	 */
	protected Problem subsetProblem;
	
	/**
	 * The properties for controlling the test problems.
	 */
	protected Properties properties;

	/**
	 * Creates the shared problem.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Before
	public void setUp() throws IOException {
		realProblem = new MockRealProblem();
		binaryProblem = new MockBinaryProblem();
		permutationProblem = new MockPermutationProblem();
		subsetProblem = new MockSubsetProblem();
		properties = new Properties();
		
		properties.setProperty("maxEvaluations", "1000");
		properties.setProperty("instances", "10"); // for RSO: maxEvaluations (1000) / instances (10) == GA population size (100)
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		realProblem = null;
		binaryProblem = null;
		permutationProblem = null;
		subsetProblem = null;
		properties = null;
	}
	
	@Test
	public void testEpsilonMOEA_Real() {
		test("eMOEA", realProblem);
	}
	
	@Test
	public void testNSGAII_Real() {
		test("NSGAII", realProblem);
	}
	
	@Test
	public void testNSGAIII_Real() {
		test("NSGAIII", realProblem);
	}
	
	@Test
	public void testMOEAD_Real() {
		test("MOEAD", realProblem);
	}
	
	@Test
	public void testGDE3_Real() {
		test("GDE3", realProblem);
	}
	
	@Test
	public void testEpsilonNSGAII_Real() {
		test("eNSGAII", realProblem);
	}
	
	@Test
	public void testCMAES_Real() {
		test("CMA-ES", realProblem);
	}
	
	@Test
	public void testSPEA2_Real() {
		test("SPEA2", realProblem);
	}

	@Test
	public void testPAES_Real() {
		test("PAES", realProblem);
	}

	@Test
	public void testPESA2_Real() {
		test("PESA2", realProblem);
	}
	
	@Test
	public void testOMOPSO_Real() {
		test("OMOPSO", realProblem);
	}
	
	@Test
	public void testSMPSO_Real() {
		test("SMPSO", realProblem);
	}
	
	@Test
	public void testIBEA_Real() {
		test("IBEA", realProblem);
	}

	@Test
	public void testSMSEMOA_Real() {
		test("SMS-EMOA", realProblem);
	}
	
	@Test
	public void testVEGA_Real() {
		test("VEGA", realProblem);
	}
	
	@Test
	public void testRVEA_Real() {
		test("RVEA", realProblem);
	}
	
	@Test
	public void testRandomSearch_Real() {
		test("Random", realProblem);
	}

	@Test
	public void testGA_Real() {
		test("GA", realProblem);
	}
	
	@Test
	public void testES_Real() {
		test("ES", realProblem);
	}
	
	@Test
	public void testDE_Real() {
		test("DE", realProblem);
	}
	
	@Test
	public void testRSO_Real() {
		test("RSO", realProblem);
	}
	
	@Test
	public void testMSOPS_Real() {
		test("MSOPS", realProblem);
	}
	
	@Test
	public void testEpsilonMOEA_Binary() {
		test("eMOEA", binaryProblem);
	}
	
	@Test
	public void testNSGAII_Binary() {
		test("NSGAII", binaryProblem);
	}
	
	@Test
	public void testNSGAIII_Binary() {
		test("NSGAIII", binaryProblem);
	}
	
	public void testMOEAD_Binary() {
		test("MOEAD", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testGDE3_Binary() {
		test("GDE3", binaryProblem);
	}
	
	@Test
	public void testEpsilonNSGAII_Binary() {
		test("eNSGAII", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testCMAES_Binary() {
		test("CMA-ES", binaryProblem);
	}
	
	@Test
	public void testSPEA2_Binary() {
		test("SPEA2", binaryProblem);
	}

	@Test
	public void testPAES_Binary() {
		test("PAES", binaryProblem);
	}

	@Test
	public void testPESA2_Binary() {
		test("PESA2", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testOMOPSO_Binary() {
		test("OMOPSO", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testSMPSO_Binary() {
		test("SMPSO", binaryProblem);
	}
	
	@Test
	public void testIBEA_Binary() {
		test("IBEA", binaryProblem);
	}

	@Test
	public void testSMSEMOA_Binary() {
		test("SMS-EMOA", binaryProblem);
	}
	
	@Test
	public void testVEGA_Binary() {
		test("VEGA", binaryProblem);
	}
	
	@Test
	@Ignore("current does not work since RVEA requires at least two objectives")
	public void testRVEA_Binary() {
		test("RVEA", binaryProblem);
	}
	
	@Test
	public void testRandomSearch_Binary() {
		test("Random", binaryProblem);
	}
	
	@Test
	public void testGA_Binary() {
		test("GA", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testES_Binary() {
		test("ES", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testDE_Binary() {
		test("DE", binaryProblem);
	}
	
	@Test
	public void testRSO_Binary() {
		test("RSO", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testMSOPS_Binary() {
		test("MSOPS", binaryProblem);
	}
	
	@Test
	public void testEpsilonMOEA_Permutation() {
		test("eMOEA", permutationProblem);
	}
	
	@Test
	public void testNSGAII_Permutation() {
		test("NSGAII", permutationProblem);
	}
	
	@Test
	public void testNSGAIII_Permutation() {
		test("NSGAIII", permutationProblem);
	}
	
	public void testMOEAD_Permutation() {
		test("MOEAD", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testGDE3_Permutation() {
		test("GDE3", permutationProblem);
	}
	
	@Test
	public void testEpsilonNSGAII_Permutation() {
		test("eNSGAII", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testCMAES_Permutation() {
		test("CMA-ES", permutationProblem);
	}
	
	@Test
	public void testSPEA2_Permutation() {
		test("SPEA2", permutationProblem);
	}

	@Test
	public void testPAES_Permutation() {
		test("PAES", permutationProblem);
	}

	@Test
	public void testPESA2_Permutation() {
		test("PESA2", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testOMOPSO_Permutation() {
		test("OMOPSO", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testSMPSO_Permutation() {
		test("SMPSO", permutationProblem);
	}

	@Test
	public void testIBEA_Permutation() {
		test("IBEA", permutationProblem);
	}
	
	@Test
	public void testSMSEMOA_Permutation() {
		test("SMS-EMOA", permutationProblem);
	}
	
	@Test
	public void testVEGA_Permutation() {
		test("VEGA", permutationProblem);
	}
	
	@Test
	@Ignore("currently broken as RVEA requires at least two objectives")
	public void testRVEA_Permutation() {
		test("RVEA", permutationProblem);
	}
	
	@Test
	public void testRandomSearch_Permutation() {
		test("Random", permutationProblem);
	}
	
	@Test
	public void testGA_Permutation() {
		test("GA", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testES_Permutation() {
		test("ES", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testDE_Permutation() {
		test("DE", permutationProblem);
	}
	
	@Test
	public void testRSO_Permutation() {
		test("RSO", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testMSOPS_Permutation() {
		test("MSOPS", permutationProblem);
	}
	
	@Test
	public void testEpsilonMOEA_Subset() {
		test("eMOEA", subsetProblem);
	}
	
	@Test
	public void testNSGAII_Subset() {
		test("NSGAII", subsetProblem);
	}
	
	@Test
	public void testNSGAIII_Subset() {
		test("NSGAIII", subsetProblem);
	}
	
	public void testMOEAD_Subset() {
		test("MOEAD", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testGDE3_Subset() {
		test("GDE3", subsetProblem);
	}
	
	@Test
	public void testEpsilonNSGAII_Subset() {
		test("eNSGAII", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testCMAES_Subset() {
		test("CMA-ES", subsetProblem);
	}
	
	@Test
	public void testSPEA2_Subset() {
		test("SPEA2", subsetProblem);
	}

	@Test
	public void testPAES_Subset() {
		test("PAES", subsetProblem);
	}

	@Test
	public void testPESA2_Subset() {
		test("PESA2", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testOMOPSO_Subset() {
		test("OMOPSO", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testSMPSO_Subset() {
		test("SMPSO", subsetProblem);
	}

	@Test
	public void testIBEA_Subset() {
		test("IBEA", subsetProblem);
	}
	
	@Test
	public void testSMSEMOA_Subset() {
		test("SMS-EMOA", subsetProblem);
	}
	
	@Test
	public void testVEGA_Subset() {
		test("VEGA", subsetProblem);
	}
	
	@Test
	@Ignore("currently broken as RVEA requires at least two objectives")
	public void testRVEA_Subset() {
		test("RVEA", subsetProblem);
	}
	
	@Test
	public void testRandomSearch_Subset() {
		test("Random", subsetProblem);
	}
	
	@Test
	public void testGA_Subset() {
		test("GA", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testES_Subset() {
		test("ES", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testDE_Subset() {
		test("DE", subsetProblem);
	}
	
	@Test
	public void testRSO_Subset() {
		test("RSO", subsetProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testMSOPS_Subset() {
		test("MSOPS", subsetProblem);
	}
	
	/**
	 * Tests if the given algorithm operates correctly.
	 * 
	 * @param algorithm the algorithm
	 */
	protected void test(String name, Problem problem) {
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, 
				properties, problem);
		
		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());
		Assert.assertEquals(0, algorithm.getResult().size());
		Assert.assertFalse(algorithm.isTerminated());
		
		while (algorithm.getNumberOfEvaluations() < 1000) {
			algorithm.step();
		}
		
		algorithm.terminate();
		
		Assert.assertTrue((algorithm.getNumberOfEvaluations() - 1000) < 100);
		Assert.assertTrue(algorithm.getResult().size() > 0);
		Assert.assertTrue(algorithm.isTerminated());
	}

}
