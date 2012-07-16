package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests if algorithm state can be saved and restored for various algorithms.
 */
public class StandardAlgorithmsResumeTest {

	@Test
	public void testNSGAIIResult() throws IOException {
		testResult("NSGAII");
	}
	
	@Test
	public void testNSGAIIContinuousRun() throws IOException {
		testContinuousRun("NSGAII");
	}
	
	@Test
	public void testGDE3Result() throws IOException {
		testResult("GDE3");
	}
	
	@Test
	public void testGDE3ContinuousRun() throws IOException {
		testContinuousRun("GDE3");
	}
	
	@Test
	public void testMOEADResult() throws IOException {
		testResult("MOEAD");
	}
	
	@Test
	public void testMOEADContinuousRun() throws IOException {
		testContinuousRun("MOEAD");
	}
	
	@Test
	public void testeNSGAIIResult() throws IOException {
		testResult("eNSGAII");
	}
	
	@Test
	public void testeNSGAIIContinuousRun() throws IOException {
		testContinuousRun("eNSGAII");
	}
	
	@Test
	public void testeMOEAResult() throws IOException {
		testResult("eMOEA");
	}
	
	@Test
	public void testeMOEAContinuousRun() throws IOException {
		testContinuousRun("eMOEA");
	}
	
	/**
	 * Tests if the specified algorithm retains the same result before and
	 * after resuming.
	 * 
	 * @param algorithmName the name of the algorithm to test
	 * @throws IOException if an I/O error occurred
	 */
	protected void testResult(String algorithmName) throws IOException {
		File file = TestUtils.createTempFile();
		NondominatedPopulation lastResult = new NondominatedPopulation();
		int lastNFE = 0;
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Checkpoints checkpoints = null;

		for (int i = 0; i < 200; i++) {
			checkpoints = new Checkpoints(
					AlgorithmFactory.getInstance().getAlgorithm(
							algorithmName, new Properties(), problem),
							file, 0);

			Assert.assertEquals(lastNFE, checkpoints.getNumberOfEvaluations());
			TestUtils.assertEquals(lastResult, checkpoints.getResult());

			checkpoints.step();

			lastNFE = checkpoints.getNumberOfEvaluations();
			lastResult = checkpoints.getResult();
		}
	}
	
	/**
	 * This is a more strict test to see if a resumed algorithm produces the
	 * same outcome as one that never resumed.
	 * 
	 * @param algorithmName the name of the algorithm to test
	 * @throws IOException if an I/O error occurred
	 */
	protected void testContinuousRun(String algorithmName) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		long seed = PRNG.getRandom().nextLong();
		
		// first, run the algorithm normally
		PRNG.setSeed(seed);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, new Properties(), problem);
		
		for (int i = 0; i < 200; i++) {
			// Due to how NondominatedSortingPopulation automatically
			// recalculates ranks and crowding distances, the checkpoint
			// version slightly differs due to an extra update.  This hack
			// allows this test to align with the checkpoint version.
			if (algorithm instanceof EvolutionaryAlgorithm) {
				Population population = 
						((EvolutionaryAlgorithm)algorithm).getPopulation();
							
				if (population instanceof NondominatedSortingPopulation) {
					((NondominatedSortingPopulation)population).update();
				}
			}
			
			algorithm.step();
		}
		
		NondominatedPopulation normalResult = algorithm.getResult();
		
		// second, run the algorithm using checkpoints
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = null;
		PRNG.setSeed(seed);

		for (int i = 0; i < 200; i++) {
			checkpoints = new Checkpoints(
					AlgorithmFactory.getInstance().getAlgorithm(
							algorithmName, new Properties(), problem),
							file, 0);

			checkpoints.step();
		}
		
		NondominatedPopulation checkpointResult = checkpoints.getResult();
		
		// finally, compare the two results
		TestUtils.assertEquals(normalResult, checkpointResult);
	}
	
}
