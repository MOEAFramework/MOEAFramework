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
package org.moeaframework.util.distributed;

import java.util.concurrent.Executors;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockConstraintProblem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockRealStochasticProblem;

public class DistributedProblemTest {
	
	@Test
	public void testSerialExecution() {
		try (Problem problem = new DistributedProblem(new MockSynchronizedProblem(),
				Executors.newFixedThreadPool(10))) {
			Population population = new Population();
			
			for (int i = 0; i < 10; i++) {
				population.add(problem.newSolution());
			}
			
			for (int i = 0; i < 10; i++) {
				problem.evaluate(population.get(i));
			}
			
			for (int i = 0; i < 10; i++) {
				population.get(i).getObjectives();
			}
		}
	}

	@Test
	public void testSmallSingleThread() {
		testRun(4, 1);
	}

	@Test
	public void testSmallDualThread() {
		testRun(4, 2);
	}

	@Test
	public void testSmallQuadThread() {
		testRun(4, 4);
	}

	@Test
	public void testLargeHalfThread() {
		testRun(100, 50);
	}

	@Test
	public void testLargeFullThread() {
		testRun(100, 100);
	}

	/**
	 * Tests the blocking functionality of the {@code DistributedProblem} and {@code FutureSolution} classes, ensuring
	 * the methods block when appropriate.
	 * 
	 * @param N the number of solutions to evaluate
	 * @param P the number of processing threads
	 */
	public void testRun(int N, int P) {
		try (DistributedProblem problem = new DistributedProblem(new MockExpensiveProblem(),
				Executors.newFixedThreadPool(P))) {
			Population population = new Population();
	
			for (int i = 0; i < N; i++) {
				population.add(problem.newSolution());
			}
	
			long startTime = System.currentTimeMillis();
	
			// submit the tasks to start processing
			for (int i = 0; i < N; i++) {
				problem.evaluate(population.get(i));
			}
	
			// these should block
			for (int i = 0; i < N; i++) {
				population.get(i).getObjective(0);
			}
	
			// these should not block
			for (int i = 0; i < N; i++) {
				population.get(i).getConstraint(0);
			}
	
			Assert.assertLessThan(Math.abs(1000 * N / (double)P - (System.currentTimeMillis() - startTime)), 1000);
		}
	}
	
	@Test
	public void testReplicabilityOfStochasticDistributedProblem() {		
		double bestSingle = getResultFromStochasticRun(1);
		double bestQuad = getResultFromStochasticRun(4);
		double bestSixteen = getResultFromStochasticRun(16);
		Assert.assertEquals(bestSingle, bestQuad, TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(bestQuad, bestSixteen, TestThresholds.HIGH_PRECISION);
	}
	
	private double getResultFromStochasticRun(int numThreads) {
		PRNG.setSeed(1234); // arbitrary seed
		
		Problem problem = DistributedProblem.from(new MockRealStochasticProblem(), numThreads);	
		
		GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
		algorithm.run(1000);
		
		return algorithm.getResult().get(0).getObjective(0); // one optimum for a single objective problem
	}
	
	private static class MockExpensiveProblem extends MockConstraintProblem {

		@Override
		public void evaluate(Solution solution) {
			try {
				super.evaluate(solution);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}
	
	private static class MockSynchronizedProblem extends MockRealProblem {
		
		private boolean isInvoked;

		@Override
		public synchronized void evaluate(Solution solution) {
			Assert.assertFalse(isInvoked);
			
			isInvoked = true;
			
			super.evaluate(solution);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// do nothing
			}
			
			isInvoked = false;
		}
		
	}

}
