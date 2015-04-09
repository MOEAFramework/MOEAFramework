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

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.MockBinaryProblem;
import org.moeaframework.problem.MockPermutationProblem;
import org.moeaframework.problem.MockRealProblem;

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
		properties = new Properties();
		
		properties.setProperty("maxEvaluations", "1000");
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		realProblem = null;
		binaryProblem = null;
		permutationProblem = null;
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
	public void testRandomSearch_Real() {
		test("Random", realProblem);
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
	
	@Test(expected = ProviderNotFoundException.class)
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
	public void testRandomSearch_Binary() {
		test("Random", binaryProblem);
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
	
	@Test(expected = ProviderNotFoundException.class)
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
	public void testRandomSearch_Permutation() {
		test("Random", permutationProblem);
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
