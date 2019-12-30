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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.IndicatorCollector;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
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
	public void testNSGAIII() throws IOException {
		test("NSGAIII");
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
	public void testCMAES() throws IOException {
		test("CMA-ES");
	}
	
	@Test
	public void testSPEA2() throws IOException {
		test("SPEA2");
	}

	@Test
	public void testPAES() throws IOException {
		test("PAES");
	}

	@Test
	@Ignore("occasionally fails due to the AdaptiveGridArchive not being serialized properly")
	public void testPESA2() throws IOException {
		test("PESA2");
	}
	
	@Test
	public void testOMOPSO() throws IOException {
		test("OMOPSO");
	}
	
	@Test
	public void testSMPSO() throws IOException {
		test("SMPSO");
	}
	
	@Test
	public void testIBEA() throws IOException {
		test("IBEA");
	}
	
	@Test
	public void testSMSEMOA() throws IOException {
		test("SMS-EMOA");
	}
	
	@Test
	public void testVEGA() throws IOException {
		test("VEGA");
	}
	
	@Test
	public void testRVEA() throws IOException {
		test("RVEA");
	}
	
	@Test
	public void testGA() throws IOException {
		test("GA");
	}
	
	@Test
	public void testES() throws IOException {
		test("ES");
	}
	
	@Test
	public void testDE() throws IOException {
		test("DE");
	}
	
	@Test
	@Ignore("currently resume capability is not supported")
	public void testRSO() throws IOException {
		test("RSO");
	}
	
	@Test
	@Ignore("currently resume capability is not supported")
	public void testMSOPS() throws IOException {
		test("MSOPS");
	}
	
	@Test
	public void testInstrumentedAlgorithm() throws IOException {
		testInstrumented("NSGAII");
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
	
	@Test
	public void testEpsilonProgressContinuation() throws IOException {
		AlgorithmFactory.setInstance(new TestAlgorithmFactory());
		test("EpsilonProgressContinuationTest");
		AlgorithmFactory.setInstance(new AlgorithmFactory());
	}
	
	private static class TestAlgorithmFactory extends AlgorithmFactory {

		@Override
		public synchronized Algorithm getAlgorithm(String name, Properties properties,
				Problem problem) {
			if (name.equalsIgnoreCase("EpsilonProgressContinuationTest")) {
				Initialization initialization = new RandomInitialization(
						problem, 100);
	
				NondominatedSortingPopulation population = 
						new NondominatedSortingPopulation(
								new ParetoDominanceComparator());
	
				EpsilonBoxDominanceArchive archive = 
						new EpsilonBoxDominanceArchive(
								EpsilonHelper.getEpsilon(problem));
	
				TournamentSelection selection = new TournamentSelection(2, 
						new ChainedComparator(
								new ParetoDominanceComparator(),
								new CrowdingComparator()));
	
				Variation variation = 
						OperatorFactory.getInstance().getVariation(null,
								new Properties(), problem);
	
				NSGAII nsgaii = new NSGAII(problem, population, archive,
						selection, variation, initialization);
	
				return new EpsilonProgressContinuation(nsgaii, 100, 100, 4.0,
						100, 10000, new UniformSelection(), new UM(1.0));
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}
	
}
