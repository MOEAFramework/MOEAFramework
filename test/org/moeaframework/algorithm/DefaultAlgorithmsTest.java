/* Copyright 2009-2024 David Hadka
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
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.algorithm.sa.AMOSA;
import org.moeaframework.analysis.EpsilonHelper;
import org.moeaframework.analysis.collector.IndicatorCollector;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.analysis.collector.Observation;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.core.variable.Program;
import org.moeaframework.mock.MockBinaryProblem;
import org.moeaframework.mock.MockGrammarProblem;
import org.moeaframework.mock.MockPermutationProblem;
import org.moeaframework.mock.MockProgramProblem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSubsetProblem;
import org.moeaframework.util.TypedProperties;

/**
 * Tests basic functionality of all default algorithms, including:
 * <ol>
 *   <li>Validates the algorithm can be constructed and run on each problem type;
 *   <li>Validates that the configuration can be saved and reloaded; and
 *   <li>Validates the algorithm is resumable.
 * </ol>
 * Note that this does not test for correctness of results.  Additional tests should be added to test specific
 * features.
 */
public class DefaultAlgorithmsTest {
	
	/**
	 * The number of NFE to run each test.
	 */
	private final int NFE = 1000;
	
	/**
	 * The number of steps to perform during resumability and instrumenter tests.  Must be > 100 to test
	 * {@link AdaptiveTimeContinuation}.
	 */
	private static final int STEPS = 200;
	
	protected Problem real;
	protected Problem binary;
	protected Problem permutation;
	protected Problem subset;
	protected Problem grammar;
	protected Problem program;
	protected List<Problem> allProblems;
	protected TypedProperties properties;

	@Before
	public void setUp() throws IOException {
		real = new MockRealProblem(2);
		binary = new MockBinaryProblem(2);
		permutation = new MockPermutationProblem(2);
		subset = new MockSubsetProblem(2);
		grammar = new MockGrammarProblem(2);
		program = new MockProgramProblem(2);
		
		allProblems = List.of(real, binary, permutation, subset, grammar, program);
				
		properties = new TypedProperties();
		properties.setInt("maxEvaluations", NFE);
		properties.setInt("instances", 10); // for RSO: maxEvaluations (1000) / instances (10) == GA population size (100)
	}

	@After
	public void tearDown() {
		real = null;
		binary = null;
		permutation = null;
		subset = null;
		grammar = null;
		program = null;
		allProblems = null;
		properties = null;
	}
	
	@Test
	public void testEpsilonMOEA() {
		test("eMOEA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testNSGAII() {
		test("NSGAII", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testNSGAIII() {
		test("NSGAIII", real, binary, permutation, subset, grammar, program);
	}

	@Test
	public void testMOEAD() {
		test("MOEAD", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testGDE3() {
		test("GDE3", real);
	}
	
	@Test
	public void testEpsilonNSGAII() {
		test("eNSGAII", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testCMAES() {
		test("CMA-ES", real);
	}
	
	@Test
	public void testSPEA2() {
		test("SPEA2", real, binary, permutation, subset, grammar, program);
	}

	@Test
	public void testPAES() {
		test("PAES", real, binary, permutation, subset, grammar, program);
	}

	@Test
	public void testPESA2() {
		test("PESA2", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testOMOPSO() {
		test("OMOPSO", real);
	}
	
	@Test
	public void testSMPSO() {
		test("SMPSO", real);
	}
	
	@Test
	public void testIBEA() {
		test("IBEA", real, binary, permutation, subset, grammar, program);
	}

	@Test
	public void testSMSEMOA() {
		test("SMS-EMOA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testVEGA() {
		test("VEGA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testRVEA() {
		test("RVEA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testRandomSearch() {
		test("Random", real, binary, permutation, subset, grammar, program);
	}

	@Test
	public void testGA() {
		test("GA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testES() {
		test("ES", real);
	}
	
	@Test
	public void testDE() {
		test("DE", real);
	}
	
	@Test
	public void testSA() {
		test("SA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testRSO() {
		test("RSO", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testMSOPS() {
		test("MSOPS", real);
	}
	
	@Test
	public void testAMOSA() {
		test("AMOSA", real, binary, permutation, subset, grammar, program);
	}
	
	@Test
	public void testEpsilonProgressContinuation() throws IOException {
		AlgorithmFactory.setInstance(new TestAlgorithmFactory());
		test("EpsilonProgressContinuation", real);
		AlgorithmFactory.setInstance(new AlgorithmFactory());
	}
	
	@Test
	public void testInstrumentedResumable() throws IOException {
		testInstrumentedResumable("NSGAII");
	}
	
	protected void test(String name, Problem... supportedProblems) {
		for (Problem problem : allProblems) {
			// test if the algorithm can be instantiated and run against the problem type
			try {
				testRun(name, problem);
			} catch (ProviderNotFoundException e) {
				if (List.of(supportedProblems).contains(problem)) {
					throw new AssertionError(name + " failed test run on " + problem.getName(), e);
				}
				
				return;
			} catch (AssertionError e) {
				throw new AssertionError(name + " failed test run on " + problem.getName(), e);
			}
			
			// test if the configuration can be saved and reloaded without error
			try {
				testConfiguration(name, problem);
			} catch (AssertionError e) {
				throw new AssertionError(name + " failed configuration test on " + problem.getName(), e);
			}
			
			// test if the algorithm is resumable
			try {
				testResumable(name, problem);
			} catch (IOException | AssertionError e) {
				throw new AssertionError(name + " failed resumable test on " + problem.getName(), e);
			}
		}
	}

	private void testRun(String name, Problem problem) {
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, properties, problem);
		
		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());
		Assert.assertEquals(0, algorithm.getResult().size());
		Assert.assertFalse(algorithm.isTerminated());
		
		while (algorithm.getNumberOfEvaluations() < NFE) {
			algorithm.step();
		}
		
		algorithm.terminate();
		
		if (!(algorithm instanceof AMOSA)) {
			Assert.assertTrue((algorithm.getNumberOfEvaluations() - NFE) < 200);
		}
		
		Assert.assertTrue(algorithm.getResult().size() > 0);
		Assert.assertTrue(algorithm.isTerminated());
	}
	
	private void testConfiguration(String name, Problem problem) {
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, properties, problem);
			
		if (algorithm instanceof Configurable configurable) {
			TypedProperties properties = configurable.getConfiguration();
			configurable.applyConfiguration(properties);
		}
	}
	
	private void testResumable(String name, Problem problem) throws IOException {
		if (problem.isType(Program.class)) {
			// Programs are not serializable!
			return;
		}
		
		long seed = PRNG.getRandom().nextLong();
		
		// first, run the algorithm normally
		PRNG.setSeed(seed);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, properties, problem);
		
		for (int i = 0; i < STEPS && !algorithm.isTerminated(); i++) {
			algorithm.step();
		}
		
		NondominatedPopulation normalResult = algorithm.getResult();
		
		// second, run the algorithm using checkpoints
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = null;
		PRNG.setSeed(seed);

		for (int i = 0; i < STEPS && (checkpoints == null || !checkpoints.isTerminated()); i++) {
			checkpoints = new Checkpoints(AlgorithmFactory.getInstance().getAlgorithm(name, properties, problem), file, 0);
			checkpoints.step();
		}
		
		NondominatedPopulation checkpointResult = checkpoints.getResult();
		
		// finally, compare the two results
		TestUtils.assertEquals(normalResult, checkpointResult);
	}
	
	private void testInstrumentedResumable(String algorithmName) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		long seed = PRNG.getRandom().nextLong();
		
		// first, run the algorithm normally
		PRNG.setSeed(seed);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, new TypedProperties(), problem);
		InstrumentedAlgorithm instrumentedAlgorithm = new InstrumentedAlgorithm(algorithm, 100);
		instrumentedAlgorithm.addCollector(new IndicatorCollector(new Hypervolume(problem, referenceSet)).attach(algorithm));
		
		for (int i = 0; i < STEPS; i++) {
			instrumentedAlgorithm.step();
		}

		Observations normalResult = instrumentedAlgorithm.getObservations();
		
		// second, run the algorithm using checkpoints
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = null;
		PRNG.setSeed(seed);

		for (int i = 0; i < STEPS; i++) {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, new TypedProperties(), problem);
			instrumentedAlgorithm = new InstrumentedAlgorithm(algorithm, 100);
			instrumentedAlgorithm.addCollector(new IndicatorCollector(new Hypervolume(problem, referenceSet)).attach(algorithm));
			checkpoints = new Checkpoints(instrumentedAlgorithm, file, 0);

			checkpoints.step();
		}
		
		Observations checkpointResult = instrumentedAlgorithm.getObservations();

		// finally, compare the two observations
		Assert.assertEquals(normalResult.keys(), checkpointResult.keys());
		Assert.assertEquals(normalResult.size(), checkpointResult.size());
		
		for (String key : normalResult.keys()) {
			for (Observation normalObservation : normalResult) {
				Observation checkpointObservation = checkpointResult.at(normalObservation.getNFE());
				Assert.assertEquals(normalObservation.get(key), checkpointObservation.get(key));
			}
		}
	}
	
	private static class TestAlgorithmFactory extends AlgorithmFactory {

		@Override
		public synchronized Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
			if (name.equalsIgnoreCase("EpsilonProgressContinuation")) {
				Initialization initialization = new RandomInitialization(problem);
	
				NondominatedSortingPopulation population = new NondominatedSortingPopulation(
						new ParetoDominanceComparator());
	
				EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
						EpsilonHelper.getEpsilons(problem));
	
				TournamentSelection selection = new TournamentSelection(2, new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()));
	
				Variation variation = OperatorFactory.getInstance().getVariation(null, new TypedProperties(), problem);
	
				NSGAII nsgaii = new NSGAII(problem, 100, population, archive, selection, variation, initialization);
	
				return new EpsilonProgressContinuation(nsgaii, 100, 100, 4.0, 100, 10000, new UniformSelection(), new UM(1.0));
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}

}
