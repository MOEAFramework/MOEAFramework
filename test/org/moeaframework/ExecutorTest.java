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
package org.moeaframework;

import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

/**
 * Tests the {@link Executor} class.
 */
public class ExecutorTest {

	private AlgorithmFactoryTestWrapper algorithmFactory;
	
	private ProblemFactoryTestWrapper problemFactory;
	
	@Before
	public void setUp() {
		algorithmFactory = new AlgorithmFactoryTestWrapper();
		problemFactory = new ProblemFactoryTestWrapper();
	}
	
	@After
	public void tearDown() {
		algorithmFactory = null;
		problemFactory = null;
	}
	
	@Test
	public void testRun() throws IOException {
		new Executor()
				.usingAlgorithmFactory(algorithmFactory)
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2")
				.withAlgorithm("NSGAII")
				.withProperty("populationSize", 100)
				.withProperty("maxEvaluations", 1000)
				.distributeOnAllCores()
				.checkpointEveryIteration()
				.withCheckpointFile(TestUtils.createTempFile())
				.run();
		
		Assert.assertEquals(1, algorithmFactory.getTerminateCount());
		Assert.assertEquals(1, problemFactory.getCloseCount());
	}
	
	@Test
	public void testRunSeeds() throws IOException {
		//the checkpoint should be ignored, possibly emitting a warning
		Assert.assertEquals(10, new Executor()
				.usingAlgorithmFactory(algorithmFactory)
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2")
				.withAlgorithm("NSGAII")
				.withProperty("populationSize", 100)
				.withProperty("maxEvaluations", 1000)
				.distributeOnAllCores()
				.checkpointEveryIteration()
				.withCheckpointFile(TestUtils.createTempFile())
				.runSeeds(10).size());
		
		Assert.assertEquals(10, algorithmFactory.getTerminateCount());
		Assert.assertEquals(10, problemFactory.getCloseCount());
	}
	
	@Test
	public void testProgressListenerSingleSeed() {
		TestProgressListener listener = new TestProgressListener();
		
		new Executor()
			.withProblem("DTLZ2_2")
			.withAlgorithm("NSGAII")
			.withProgressListener(listener)
			.withMaxEvaluations(1000)
			.run();
		
		Assert.assertEquals(1, listener.getSeedCount());
		Assert.assertEquals(11, listener.getCallCount());
		Assert.assertEquals(0, listener.getLastEvent().getCurrentNFE()); // resets to 0 after calling nextSeed
		Assert.assertEquals(1000, listener.getLastEvent().getMaxNFE());
		Assert.assertEquals(1, listener.getLastEvent().getTotalSeeds());
		Assert.assertEquals(2, listener.getLastEvent().getCurrentSeed()); // TODO: is this OK
		Assert.assertEquals(1.0, listener.getLastEvent().getPercentComplete(), 0.0);
		Assert.assertEquals(0.0, listener.getLastEvent().getRemainingTime(), 0.0);
	}
	
	@Test
	public void testProgressListenerMultipleSeed() {
		TestProgressListener listener = new TestProgressListener();
		
		new Executor()
			.withProblem("DTLZ2_2")
			.withAlgorithm("NSGAII")
			.withProgressListener(listener)
			.withMaxEvaluations(1000)
			.runSeeds(5);
		
		Assert.assertEquals(5, listener.getSeedCount());
		Assert.assertEquals(55, listener.getCallCount());
		Assert.assertEquals(0, listener.getLastEvent().getCurrentNFE()); // resets to 0 after calling nextSeed
		Assert.assertEquals(1000, listener.getLastEvent().getMaxNFE());
		Assert.assertEquals(5, listener.getLastEvent().getTotalSeeds());
		Assert.assertEquals(6, listener.getLastEvent().getCurrentSeed());
		Assert.assertEquals(1.0, listener.getLastEvent().getPercentComplete(), 0.0);
		Assert.assertEquals(0.0, listener.getLastEvent().getRemainingTime(), 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoProblem() {
		new Executor().withAlgorithm("NSGAII").run();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoAlgorithm() {
		new Executor().withProblem("DTLZ2_2").run();
	}
	
}
