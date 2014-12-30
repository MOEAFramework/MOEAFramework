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
package org.moeaframework.algorithm.jmetal;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;

import jmetal.util.JMException;

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
import org.moeaframework.util.distributed.DistributedProblem;
import org.moeaframework.util.distributed.FutureSolution;

/**
 * Tests the {@link JMetalAlgorithms} class to ensure the JMetal algorithms
 * can be constructed and used correctly.
 */
public class JMetalAlgorithmsTest {

	/**
	 * The real encoded test problem.
	 */
	private Problem realProblem;

	/**
	 * The binary encoded test problem.
	 */
	private Problem binaryProblem;
	
	/**
	 * The permutation test problem.
	 */
	private Problem permutationProblem;
	
	/**
	 * The properties for controlling the test problems.
	 */
	private Properties properties;

	/**
	 * Creates the shared JMetal algorithm provider instance.
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
	public void testAbYSS_Real() {
		test("AbYSS", realProblem);
	}

	@Test
	public void testCellDE_Real() {
		test("CellDE", realProblem);
	}

	@Test
	public void testDENSEA_Real() {
		test("DENSEA", realProblem);
	}

	@Test
	public void testFastPGA_Real() {
		test("FastPGA", realProblem);
	}
	
	@Test
	public void testGDE3_Real() {
		test("GDE3-JMetal", realProblem);
	}

	@Test
	public void testIBEA_Real() {
		test("IBEA", realProblem);
	}

	@Test
	public void testMOCell_Real() {
		test("MOCell", realProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testMOCHC_Real() {
		test("MOCHC", realProblem);
	}
	
	@Test
	public void testNSGAII_Real() {
		test("NSGAII-JMetal", realProblem);
	}

	@Test
	public void testOMOPSO_Real() {
		test("OMOPSO", realProblem);
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
	public void testSMPSO_Real() {
		test("SMPSO", realProblem);
	}
	
	@Test
	public void testSMSEMOA_Real() {
		test("SMSEMOA", realProblem);
	}

	@Test
	public void testSPEA2_Real() {
		test("SPEA2", realProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testAbYSS_Binary() {
		test("AbYSS", binaryProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testCellDE_Binary() {
		test("CellDE", binaryProblem);
	}

	@Test
	public void testDENSEA_Binary() {
		test("DENSEA", binaryProblem);
	}

	@Test
	public void testFastPGA_Binary() {
		test("FastPGA", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testGDE3_Binary() {
		test("GDE3-JMetal", binaryProblem);
	}

	@Test
	public void testIBEA_Binary() {
		test("IBEA", binaryProblem);
	}

	@Test
	public void testMOCell_Binary() {
		test("MOCell", binaryProblem);
	}

	@Test
	public void testMOCHC_Binary() {
		test("MOCHC", binaryProblem);
	}
	
	@Test
	public void testNSGAII_Binary() {
		test("NSGAII-JMetal", binaryProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testOMOPSO_Binary() {
		test("OMOPSO", binaryProblem);
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
	public void testSMPSO_Binary() {
		test("SMPSO", binaryProblem);
	}
	
	@Test
	public void testSMSEMOA_Binary() {
		test("SMSEMOA", binaryProblem);
	}

	@Test
	public void testSPEA2_Binary() {
		test("SPEA2", binaryProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testAbYSS_Permutation() {
		test("AbYSS", permutationProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testCellDE_Permutation() {
		test("CellDE", permutationProblem);
	}

	@Test
	public void testDENSEA_Permutation() {
		test("DENSEA", permutationProblem);
	}

	@Test
	public void testFastPGA_Permutation() {
		test("FastPGA", permutationProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testGDE3_Permutation() {
		test("GDE3-JMetal", permutationProblem);
	}

	@Test
	public void testIBEA_Permutation() {
		test("IBEA", permutationProblem);
	}

	@Test
	public void testMOCell_Permutation() {
		test("MOCell", permutationProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testMOCHC_Permutation() {
		test("MOCHC", permutationProblem);
	}
	
	@Test
	public void testNSGAII_Permutation() {
		test("NSGAII-JMetal", permutationProblem);
	}

	@Test(expected = ProviderNotFoundException.class)
	public void testOMOPSO_Permutation() {
		test("OMOPSO", permutationProblem);
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
	public void testSMPSO_Permutation() {
		test("SMPSO", permutationProblem);
	}
	
	@Test
	public void testSMSEMOA_Permutation() {
		test("SMSEMOA", permutationProblem);
	}

	@Test
	public void testSPEA2_Permutation() {
		test("SPEA2", permutationProblem);
	}

	/**
	 * Tests if the given JMetal algorithm operates correctly.
	 * 
	 * @param name the name of the algorithm
	 * @param problem the problem
	 */
	private void test(String name, Problem problem) {
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, 
				properties, problem);
		
		Assert.assertTrue(algorithm instanceof JMetalAlgorithmAdapter);
		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());
		Assert.assertEquals(0, algorithm.getResult().size());
		Assert.assertFalse(algorithm.isTerminated());
		algorithm.step();
		Assert.assertEquals(1000, algorithm.getNumberOfEvaluations());
		Assert.assertTrue(algorithm.getResult().size() > 0);
		Assert.assertTrue(algorithm.isTerminated());
	}
	
	/**
	 * Unfortunately, the JMetal translation process does not permit
	 * parallel evaluation, but this test ensures the translation still works
	 * without error.
	 * 
	 * @throws ClassNotFoundException should not occur
	 * @throws JMException should not occur
	 */
	@Test
	public void testDistributedProblem() throws ClassNotFoundException, 
	JMException {
		JMetalProblemAdapter adapter = new JMetalProblemAdapter(
				new DistributedProblem(realProblem, 
						Executors.newSingleThreadExecutor()));
		jmetal.core.Solution solution = new jmetal.core.Solution(adapter);
		
		//this will throw an exception if the distributed problem is not 
		//correctly handled
		adapter.evaluate(solution);
		
		Assert.assertTrue(adapter.translate(solution) instanceof FutureSolution);
	}

}
