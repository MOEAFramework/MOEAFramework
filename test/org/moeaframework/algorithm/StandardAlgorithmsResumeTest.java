package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.IndicatorCollector;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests if algorithm state can be saved and restored for various algorithms.
 */
public class StandardAlgorithmsResumeTest {
	
	/**
	 * The number of steps to perform.  Must be > 100 to test
	 * {@link AdaptiveTimeContinuation}.
	 */
	private static final int N = 200;

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
	public void testEpsilonNSGAII() throws IOException {
		test("eNSGAII");
	}
	
	@Test
	public void testEpsilonMOEA() throws IOException {
		test("eMOEA");
	}
	
	@Test
	public void testInstrumentedNSGAII() throws IOException {
		testInstrumented("NSGAII");
	}
	
	@Test
	public void testInstrumentedGDE3() throws IOException {
		testInstrumented("GDE3");
	}
	
	@Test
	public void testInstrumentedMOEAD() throws IOException {
		testInstrumented("MOEAD");
	}
	
	@Test
	public void testInstrumentedEpsilonNSGAII() throws IOException {
		testInstrumented("eNSGAII");
	}
	
	@Test
	public void testInstrumentedEpslonMOEA() throws IOException {
		testInstrumented("eMOEA");
	}
	
	/**
	 * Tests if a resumed algorithm produces the same outcome as one that never
	 * resumed.
	 * 
	 * @param algorithmName the name of the algorithm to test
	 * @throws IOException if an I/O error occurred
	 */
	protected void test(String algorithmName) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		long seed = PRNG.getRandom().nextLong();
		
		// first, run the algorithm normally
		PRNG.setSeed(seed);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, new Properties(), problem);
		
		for (int i = 0; i < N; i++) {
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

		for (int i = 0; i < N; i++) {
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
	
	protected void testInstrumented(String algorithmName) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = 
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		long seed = PRNG.getRandom().nextLong();
		
		// first, run the algorithm normally
		PRNG.setSeed(seed);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, new Properties(), problem);
		InstrumentedAlgorithm instrumentedAlgorithm = new InstrumentedAlgorithm(
				algorithm, 100);
		instrumentedAlgorithm.addCollector(new IndicatorCollector(
				new Hypervolume(problem, referenceSet)).attach(algorithm));
		
		for (int i = 0; i < N; i++) {
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
			
			instrumentedAlgorithm.step();
		}

		Accumulator normalResult = instrumentedAlgorithm.getAccumulator();
		
		// second, run the algorithm using checkpoints
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = null;
		PRNG.setSeed(seed);

		for (int i = 0; i < N; i++) {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					algorithmName, new Properties(), problem);
			instrumentedAlgorithm = new InstrumentedAlgorithm(
					algorithm, 100);
			instrumentedAlgorithm.addCollector(new IndicatorCollector(
					new Hypervolume(problem, referenceSet)).attach(algorithm));
			checkpoints = new Checkpoints(instrumentedAlgorithm, file, 0);

			checkpoints.step();
		}
		
		Accumulator checkpointResult = instrumentedAlgorithm.getAccumulator();

		// finally, compare the two accumulators
		Assert.assertTrue(normalResult.keySet().equals(
				checkpointResult.keySet()));
		
		for (String key : normalResult.keySet()) {
			Assert.assertEquals(normalResult.size(key),
					checkpointResult.size(key));
			
			for (int i = 0; i < normalResult.size(key); i++) {
				Assert.assertEquals(normalResult.get(key, i),
						checkpointResult.get(key, i));
			}
		}
	}
	
}
