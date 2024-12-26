/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.parallel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemWrapper;

public class FutureSolutionTest {
	
	private TestableFutureProblem testProblem;
	private TestableFutureSolution unevaluatedSolution;
	private Solution evaluatedSolution;
	private Future<Solution> completedFuture;
	
	@Before
	public void setUp() {
		testProblem = new TestableFutureProblem();
		
		unevaluatedSolution = testProblem.newSolution();
		unevaluatedSolution.randomize();
		
		evaluatedSolution = unevaluatedSolution.copy();
		testProblem.evaluate(evaluatedSolution);
		
		completedFuture = CompletableFuture.completedFuture(evaluatedSolution);
	}
	
	@Test
	public void testUpdate() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		solution.setFuture(completedFuture);
		
		solution.assertEqualsTo(evaluatedSolution);
		solution.assertUpdated();
	}
	
	@Test
	public void testSerialization() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
				
		try {
			DistributedProblem problem = DistributedProblem.from(testProblem, 1);
			
			TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
			problem.evaluate(solution);
			
			solution.assertEqualsTo(evaluatedSolution);
			solution.assertUpdated();
		} finally {
			executor.shutdown();
		}
	}
	
	@Test
	public void testBlocking() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		testProblem = new TestableFutureProblem(0);
				
		try {
			DistributedProblem problem = DistributedProblem.from(testProblem, 1);
			
			TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
			problem.evaluate(solution);
			
			testProblem.releaseOrFailIfBlocked(1);

			solution.update();
			solution.assertUpdated();
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
		solution.assertNotUpdated();
		
		FutureSolution copy = solution.copy();
		solution.assertUpdated();
		Assert.assertNotSame(solution, copy);
		
		solution.setFuture(completedFuture);
		copy = solution.copy();
		solution.assertUpdated();
		solution.assertEqualsTo(copy);
		Assert.assertNotSame(solution, copy);
	}
	
	@Test
	public void testDeepCopy() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		evaluatedSolution.setAttribute("foo", "bar");
		
		Solution copy = solution.deepCopy();
		solution.assertUpdated();
		Assert.assertInstanceOf(FutureSolution.class, copy);
		Assert.assertNotSame(solution, copy);
		Assert.assertFalse(copy.hasAttribute("foo"));
		
		solution.setFuture(completedFuture);
		copy = solution.deepCopy();
		solution.assertUpdated();
		solution.assertEqualsTo(copy);
		Assert.assertInstanceOf(FutureSolution.class, copy);
		Assert.assertNotSame(solution, copy);
		Assert.assertTrue(copy.hasAttribute("foo"));
	}
	
	@Test
	public void testEuclideanDistance() {
		TestableFutureSolution solution = new TestableFutureSolution(unevaluatedSolution);
		
		// Expect non-zero result since the unevaluated solution has an objective value of 0
		Assert.assertNotEquals(0.0, solution.euclideanDistance(evaluatedSolution), TestThresholds.HIGH_PRECISION);
		
		solution.setFuture(completedFuture);
		Assert.assertEquals(0.0, solution.euclideanDistance(evaluatedSolution), TestThresholds.HIGH_PRECISION);
	}

}
