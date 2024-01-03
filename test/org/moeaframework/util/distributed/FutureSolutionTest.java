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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.problem.ProblemWrapper;

public class FutureSolutionTest {
	
	public class TestableFutureSolution extends FutureSolution {

		private static final long serialVersionUID = 2833940082100144051L;
		
		public boolean isUpdated;
		
		public TestableFutureSolution(Solution solution) {
			super(solution);
		}

		@Override
		public synchronized void update() {
			super.update();
			isUpdated = true;
		}
		
		@Override
		public synchronized void setFuture(Future<Solution> future) {
			super.setFuture(future);
			isUpdated = false;
		}
		
	}
	
	private Problem testProblem;
	
	private Solution unevaluatedSolution;
	
	private Solution evaluatedSolution;
	
	private Future<Solution> futureSolution;
	
	@Before
	public void setUp() {
		testProblem = new MockRealProblem();
		
		unevaluatedSolution = new RandomInitialization(testProblem).initialize(1)[0];
		
		evaluatedSolution = unevaluatedSolution.copy();
		testProblem.evaluate(evaluatedSolution);
		
		futureSolution = CompletableFuture.completedFuture(evaluatedSolution);
	}
	
	@Test
	public void testUpdate() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		solution.setFuture(futureSolution);
		
		TestUtils.assertEquals(solution, evaluatedSolution);
		Assert.assertTrue(solution.isUpdated);
	}
	
	@Test
	public void testSerialization() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
				
		try {
			DistributedProblem problem = DistributedProblem.from(testProblem, 1);
			
			TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
			problem.evaluate(solution);
			
			TestUtils.assertEquals(solution, evaluatedSolution);
			Assert.assertTrue(solution.isUpdated);
		} finally {
			executor.shutdown();
		}
	}
	
	@Test
	public void testBlocking() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Problem delayedProblem = new ProblemWrapper(testProblem) {

			@Override
			public void evaluate(Solution solution) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					throw new FrameworkException("sleep was interrupted", e);
				}
				
				super.evaluate(solution);
			}
			
		};
				
		try {
			DistributedProblem problem = DistributedProblem.from(delayedProblem, 1);
			
			TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
			problem.evaluate(solution);

			long startTime = System.currentTimeMillis();
			solution.update();
			long endTime = System.currentTimeMillis();
			
			Assert.assertTrue((endTime - startTime) > 1000);
		} finally {
			executor.shutdown();
		}
	}
	
	@Test
	public void testException() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Problem exceptionalProblem = new ProblemWrapper(testProblem) {

			@Override
			public void evaluate(Solution solution) {
				throw new FrameworkException("error!");
			}
			
		};
		
		try {
			DistributedProblem problem = DistributedProblem.from(exceptionalProblem, 1);
			
			TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
			
			// no exception during evaluation as this is asynchronous
			problem.evaluate(solution);
			
			// but we do receive the exception when updating the solution
			Assert.assertThrows(FrameworkException.class, () -> solution.update());
		} finally {
			executor.shutdown();
		}		
	}
	
	@Test
	public void testCopy() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		Assert.assertFalse(solution.isUpdated);
		
		FutureSolution copy = solution.copy();
		Assert.assertTrue(solution.isUpdated);
		Assert.assertNotEquals(solution, copy);
		
		solution.setFuture(futureSolution);
		copy = solution.copy();
		Assert.assertTrue(solution.isUpdated);
		Assert.assertNotEquals(solution, copy);
		TestUtils.assertEquals(solution, copy);
	}
	
	@Test
	public void testDeepCopy() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		evaluatedSolution.setAttribute("foo", "bar");
		
		Solution copy = solution.deepCopy();
		Assert.assertTrue(solution.isUpdated);
		Assert.assertTrue(copy instanceof FutureSolution);
		Assert.assertNotEquals(solution, copy);
		Assert.assertFalse(copy.hasAttribute("foo"));
		
		solution.setFuture(futureSolution);
		copy = solution.deepCopy();
		Assert.assertTrue(solution.isUpdated);
		Assert.assertTrue(copy instanceof FutureSolution);
		Assert.assertNotEquals(solution, copy);
		TestUtils.assertEquals(solution, copy);
		Assert.assertTrue(copy.hasAttribute("foo"));
	}
	
	@Test
	public void testDistanceTo() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		
		// Expect non-zero result since the unevaluated solution has an objective value of 0
		Assert.assertNotEquals(0.0, solution.distanceTo(evaluatedSolution), Settings.EPS);
		
		solution.setFuture(futureSolution);
		Assert.assertEquals(0.0, solution.distanceTo(evaluatedSolution), Settings.EPS);
	}

}
