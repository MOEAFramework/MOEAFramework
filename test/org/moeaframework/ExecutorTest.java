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
package org.moeaframework;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;
import org.moeaframework.mock.MockMultiTypeProblem;
import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;

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
				.withCheckpointFile(TempFiles.createFile())
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
				.withCheckpointFile(TempFiles.createFile())
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
		Assert.assertEquals(2, listener.getLastEvent().getCurrentSeed()); // TODO: is this OK?
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

	@Test(expected = FrameworkException.class)
	public void testNoProblem() {
		new Executor().withAlgorithm("NSGAII").run();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoAlgorithm() {
		new Executor().withProblem("DTLZ2_2").run();
	}
	
	@Test
	public void testMixedTypesWithDefaultOperator() {
		new Executor()
			.withProblem(new MockMultiTypeProblem())
			.withAlgorithm("NSGAII")
			.withMaxEvaluations(1000)
			.run();
	}
	
	@Test
	public void testMixedTypesWithSuppliedOperator() {
		new Executor()
			.withProblem(new MockMultiTypeProblem())
			.withAlgorithm("NSGAII")
			.withProperty("operator", "2x")
			.withMaxEvaluations(1000)
			.run();
	}
	
	private static class TestProgressListener implements ProgressListener {

		private int seedCount = 0;
		
		private int callCount = 0;
		
		private ProgressEvent lastEvent = null;
		
		@Override
		public void progressUpdate(ProgressEvent event) {
			if (event.isSeedFinished()) {
				Assert.assertNull(event.getCurrentAlgorithm());
				Assert.assertTrue(event.getCurrentSeed() >= 0);
				Assert.assertTrue(event.getCurrentNFE() == 0);
				seedCount++;
			} else {
				Assert.assertNotNull(event.getCurrentAlgorithm());
				Assert.assertTrue(event.getCurrentSeed() >= 0 && event.getCurrentSeed() <= event.getTotalSeeds());
				Assert.assertTrue(event.getCurrentNFE() >= 0);
			}
			
			Assert.assertNotNull(event.getExecutor());
			Assert.assertTrue(event.getElapsedTime() >= 0.0);
			Assert.assertTrue(event.getRemainingTime() >= 0.0 || Double.isNaN(event.getRemainingTime()));
			Assert.assertTrue(event.getMaxTime() == -1.0); // Will be negative if not set
			
			callCount++;
			lastEvent = event;
		}
		
		public int getSeedCount() {
			return seedCount;
		}
		
		public int getCallCount() {
			return callCount;
		}
		
		public ProgressEvent getLastEvent() {
			return lastEvent;
		}

	}
	
}
