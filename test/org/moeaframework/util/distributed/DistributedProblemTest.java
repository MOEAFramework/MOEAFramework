/* Copyright 2009-2016 David Hadka
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link DistributedProblem} and {@link FutureSolution} classes.
 */
public class DistributedProblemTest {
	
	/**
	 * Tests if the synchronized keyword is sufficient to force a problem to
	 * be evaluated serially.
	 */
	@Test
	public void testSerialProblem() {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Problem problem = new DistributedProblem(new MockRealProblem() {
			
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
			
		}, executor);
		
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
		
		executor.shutdown();
	}

	/**
	 * Tests running a small single-thread test.
	 */
	@Test
	public void testSmallSingleThread() {
		testRun(4, 1);
	}

	/**
	 * Tests running a small dual-thread test.
	 */
	@Test
	public void testSmallDualThread() {
		testRun(4, 2);
	}

	/**
	 * Tests running a small quad-thread test.
	 */
	@Test
	public void testSmallQuadThread() {
		testRun(4, 4);
	}

	/**
	 * Tests running a large test with half as many threads as there are
	 * solutions to evaluate.
	 */
	@Test
	public void testLargeHalfThread() {
		testRun(100, 50);
	}

	/**
	 * Tests running a large test with the same number of threads as there are
	 * solutions to evaluate.
	 */
	@Test
	public void testLargeFullThread() {
		testRun(100, 100);
	}

	/**
	 * Tests the blocking functionality of the {@code DistributedProblem} and
	 * {@code FutureSolution} classes, ensuring the methods block when
	 * appropriate.
	 * 
	 * @param N the number of solutions to evaluate
	 * @param P the number of processing threads
	 */
	public void testRun(int N, int P) {
		DistributedProblem problem = new DistributedProblem(
				new AbstractProblem(0, 0) {

			@Override
			public void evaluate(Solution solution) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Solution newSolution() {
				return new Solution(0, 1, 1);
			}

		}, Executors.newFixedThreadPool(P));

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

		Assert.assertTrue(Math.abs(1000 * N / (double)P
				- (System.currentTimeMillis() - startTime)) < 1000);
	}

}
