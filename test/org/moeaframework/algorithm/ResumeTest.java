package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Tests if algorithm state can be saved and restored for various algorithms.
 */
public class ResumeTest {
	
	public static class ErrorProneProblem extends DTLZ2 {

		public ErrorProneProblem(int numberOfObjectives) {
			super(numberOfObjectives);
		}

		@Override
		public void evaluate(Solution solution) {
			if (PRNG.nextDouble() <= 0.001) {
				throw new RuntimeException();
			} else {
				super.evaluate(solution);
			}
		}
		
	}

	@Test
	public void testNSGAII() throws IOException {
		test("NSGAII");
	}
	
	@Test
	public void testGDE3() throws IOException {
		test("GDE3");
	}
	
	@Test
	public void testMOEAD() throws IOException {
		test("MOEAD");
	}
	
	@Test
	public void testeNSGAII() throws IOException {
		test("eNSGAII");
	}
	
	@Test
	public void testeMOEA() throws IOException {
		test("eMOEA");
	}
	
	/**
	 * Tests if the specified algorithm is able to save and restore its state
	 * from a file.
	 * 
	 * @param algorithmName the name of the algorithm to test
	 * @throws IOException if an I/O error occurred
	 */
	protected void test(String algorithmName) throws IOException {
		File file = TestUtils.createTempFile();
		NondominatedPopulation lastResult = new NondominatedPopulation();
		int lastNFE = 0;
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Algorithm algorithm = null;
		Checkpoints checkpoints = null;

		for (int i = 0; i < 10; i++) {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					algorithmName, new Properties(), problem);
			checkpoints = new Checkpoints(algorithm, file, 0);

			Assert.assertEquals(lastNFE, checkpoints.getNumberOfEvaluations());
			TestUtils.assertEquals(lastResult, checkpoints.getResult());

			checkpoints.step();

			lastNFE = checkpoints.getNumberOfEvaluations();
			lastResult = checkpoints.getResult();
		}
	}
	
	/**
	 * This is a more strict test to see if a resumed algorithm produces the
	 * same outcome as one that never resumed.  There are issues with NSGAII
	 * and eNSGAII where non-dominated sorting is computed immediately after
	 * restoring the state, which differs slightly from the algorithm is it
	 * never resumes.  For this reason, this test is currently not in use.
	 * 
	 * @param algorithmName the name of the algorithm to test
	 * @throws IOException if an I/O error occurred
	 */
	protected void testStrict(String algorithmName) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Algorithm algorithm = null;
		NondominatedPopulation normalResult = null;
		NondominatedPopulation checkpointResult = null;
		PRNG.setRandom(new Random());
		
		//first, run the algorithm normally
		PRNG.setSeed(1337);
		algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName,
				new Properties(), problem);
		
		for (int i = 0; i < 10; i++) {
			algorithm.step();
		}
		
		normalResult = algorithm.getResult();
		
		//second, run the algorithm using checkpoints
		File file = TestUtils.createTempFile();
		PRNG.setSeed(1337);

		for (int i = 0; i < 10; i++) {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					algorithmName, new Properties(), problem);
			algorithm = new Checkpoints(algorithm, file, 0);

			algorithm.step();
		}
		
		checkpointResult = algorithm.getResult();
		
		//finally, compare the two results
		TestUtils.assertEquals(normalResult, checkpointResult);
	}

}
