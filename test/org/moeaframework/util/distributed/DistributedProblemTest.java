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
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealStochasticProblem;

@RunWith(CIRunner.class)
@Retryable
public class DistributedProblemTest {
	
	@Test
	public void testSerialExecution() {
		int N = 10;
		TestableSynchronizedProblem synchronizedProblem = new TestableSynchronizedProblem();
		
		try (Problem problem = new DistributedProblem(synchronizedProblem, Executors.newFixedThreadPool(N))) {
			Population population = new Population();
			
			for (int i = 0; i < N; i++) {
				population.add(problem.newSolution());
			}
			
			// verify these are all future solutions
			for (int i = 0; i < N; i++) {
				Assert.assertInstanceOf(FutureSolution.class, population.get(i));
			}
			
			// calls to evaluate are serialized (assertions in evaluate method)
			for (int i = 0; i < N; i++) {
				problem.evaluate(population.get(i));
			}
			
			// futures are still used even when synchronized, should not be updated yet
			for (int i = 0; i < N; i++) {
				((TestableFutureSolution)population.get(i)).assertNotUpdated();
			}

			// verify reads call update
			for (int i = 0; i < N; i++) {
				population.get(i).getObjectives();
				((TestableFutureSolution)population.get(i)).assertUpdated();
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
		if (N % P != 0) {
			Assert.fail("Test should only be run when N is a multiple of P");
		}
		
		TestableFutureProblem blockingProblem = new TestableFutureProblem(0);
				
		try (DistributedProblem problem = new DistributedProblem(blockingProblem, Executors.newFixedThreadPool(P))) {
			Population population = new Population();
	
			for (int i = 0; i < N; i++) {
				population.add(problem.newSolution());
			}
			
			// verify these are all future solutions
			for (int i = 0; i < N; i++) {
				Assert.assertInstanceOf(FutureSolution.class, population.get(i));
			}
	
			// submit the tasks to start processing
			for (int i = 0; i < N; i++) {
				problem.evaluate(population.get(i));
			}
			
			// futures are not updated yet
			for (int i = 0; i < N; i++) {
				((TestableFutureSolution)population.get(i)).assertNotUpdated();
			}

			// evaluate should not block the test thread if properly distributed
			blockingProblem.releaseOrFailIfBlocked(N);
			
			// futures are not updated yet
			for (int i = 0; i < N; i++) {
				((TestableFutureSolution)population.get(i)).assertNotUpdated();
			}

			// verify reads call update
			for (int i = 0; i < N; i++) {
				population.get(i).getObjective(0);
				population.get(i).getConstraint(0);
				((TestableFutureSolution)population.get(i)).assertUpdated();
			}
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

}
