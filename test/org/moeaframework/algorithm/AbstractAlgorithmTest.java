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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockRealProblem;

public class AbstractAlgorithmTest {

	/**
	 * Test {@code AbstractAlgorithm} that counts the number of times the {@code iterate} method is invoked.
	 */
	private static class TestAbstractAlgorithm extends AbstractAlgorithm {

		/**
		 * The number of times the {@code iterate} method is invoked.
		 */
		private int iterated;

		/**
		 * Constructs a test {@code AbstractAlgorithm}.
		 */
		public TestAbstractAlgorithm() {
			super(new MockRealProblem(2));
		}

		@Override
		public void iterate() {
			iterated++;
		}

		@Override
		public NondominatedPopulation getResult() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns the number of times the iterate method is invoked.
		 * 
		 * @return the number of times the iterate method is invoked
		 */
		public int getIterated() {
			return iterated;
		}

	}

	@Test
	public void testExplicitCallToInitialize() {
		TestAbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		Assert.assertFalse(algorithm.isInitialized());
		Assert.assertFalse(algorithm.isTerminated());

		algorithm.initialize();

		Assert.assertTrue(algorithm.isInitialized());
		Assert.assertFalse(algorithm.isTerminated());

		algorithm.step();
		algorithm.step();

		Assert.assertTrue(algorithm.isInitialized());
		Assert.assertFalse(algorithm.isTerminated());
		Assert.assertEquals(2, algorithm.getIterated());

		algorithm.terminate();

		Assert.assertTrue(algorithm.isInitialized());
		Assert.assertTrue(algorithm.isTerminated());
	}

	@Test
	public void testImplicitInitialization() {
		TestAbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		Assert.assertFalse(algorithm.isInitialized());
		Assert.assertFalse(algorithm.isTerminated());

		algorithm.step();
		algorithm.step();

		Assert.assertTrue(algorithm.isInitialized());
		Assert.assertFalse(algorithm.isTerminated());
		Assert.assertEquals(1, algorithm.getIterated());

		algorithm.terminate();

		Assert.assertTrue(algorithm.isInitialized());
		Assert.assertTrue(algorithm.isTerminated());
	}

	@Test
	public void testTerminateBeforeInitialization() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.terminate();

		Assert.assertFalse(algorithm.isInitialized());
		Assert.assertTrue(algorithm.isTerminated());
	}

	@Test(expected = AlgorithmException.class)
	public void testGuardMultipleInitializations1() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.initialize();
		algorithm.initialize();
	}

	@Test(expected = AlgorithmException.class)
	public void testGuardMultipleInitializations2() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.initialize();
	}

	@Test(expected = AlgorithmException.class)
	public void testGuardMultipleTerminations() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.terminate();
	}

	@Test(expected = AlgorithmException.class)
	public void testGuardStepAfterTerminate() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.step();
	}

	@Test(expected = AlgorithmException.class)
	public void testGuardInitializeAfterTerminate() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.initialize();
	}

	@Test
	public void testNumberOfEvaluations() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();
		Solution[] solutions = new Solution[100];
		
		for (int i = 0; i < 100; i++) {
			solutions[i] = algorithm.getProblem().newSolution();
		}

		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());

		algorithm.evaluateAll(solutions);
		algorithm.evaluate(algorithm.getProblem().newSolution());
		algorithm.evaluate(algorithm.getProblem().newSolution());
		algorithm.evaluateAll(solutions);

		Assert.assertEquals(202, algorithm.getNumberOfEvaluations());
	}

	@Test
	public void testResumable() throws IOException {
		// while this is technically an error since the algorithm is not serializable, this is just testing the
		// constructor if no state file exists
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = new Checkpoints(new TestAbstractAlgorithm(), file, 0);
		checkpoints.step();
	}

}
