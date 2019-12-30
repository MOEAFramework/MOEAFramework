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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link AbstractAlgorithm} class.
 */
public class AbstractAlgorithmTest {

	/**
	 * Test problem.
	 */
	private static class TestProblem implements Problem {

		@Override
		public void evaluate(Solution solution) {
			// do nothing
		}

		@Override
		public String getName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfConstraints() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfObjectives() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfVariables() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Solution newSolution() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			//do nothing
		}

	}

	/**
	 * Test {@code AbstractAlgorithm} that counts the number of times the
	 * {@code iterate} method is invoked.
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
			super(new TestProblem());
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

	/**
	 * Tests if the {@code AbstractAlgorithm} works correcly under normal
	 * operating conditions.
	 */
	@Test
	public void testLifecycle1() {
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

	/**
	 * Tests if the {@code AbstractAlgorithm} works correcly under normal
	 * operating conditions.
	 */
	@Test
	public void testLifecycle2() {
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

	/**
	 * While this state may seem to be an error, since the {@link Algorithm}
	 * interface does not provide an {@code initialize()} method, calling
	 * {@code terminate} immediately after the constructor is valid.
	 */
	@Test
	public void testLifecycle3() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.terminate();

		Assert.assertFalse(algorithm.isInitialized());
		Assert.assertTrue(algorithm.isTerminated());
	}

	/**
	 * Tests if an abnormal lifecycle is correctly handled; in this case,
	 * if the {@code AbstractAlgorithm} is initialized twice.
	 */
	@Test(expected = AlgorithmException.class)
	public void testLifecycleError1() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.initialize();
		algorithm.initialize();
	}

	/**
	 * Tests if an abnormal lifecycle is correctly handled; in this case,
	 * if the {@code AbstractAlgorithm} is initialized twice.
	 */
	@Test(expected = AlgorithmException.class)
	public void testLifecycleError2() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.initialize();
	}

	/**
	 * Tests if an abnormal lifecycle is correctly handled; in this case,
	 * if the {@code AbstractAlgorithm} is terminated twice.
	 */
	@Test(expected = AlgorithmException.class)
	public void testLifecycleError3() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.terminate();
	}

	/**
	 * Tests if an abnormal lifecycle is correctly handled; in this case,
	 * if the {@code AbstractAlgorithm} is iterated after termination.
	 */
	@Test(expected = AlgorithmException.class)
	public void testLifecycleError4() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.step();
	}

	/**
	 * Tests if an abnormal lifecycle is correctly handled; in this case,
	 * if the {@code AbstractAlgorithm} is initialized after termination.
	 */
	@Test(expected = AlgorithmException.class)
	public void testLifecycleError5() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		algorithm.initialize();
	}

	/**
	 * Tests if the {@code evaluate} and {@code evaluateAll} methods correctly
	 * track the number of evaluations.
	 */
	@Test
	public void testNumberOfEvaluations() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();
		Solution[] solutions = new Solution[100];

		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());

		// since TestProblem does nothing, null solutions are ok
		algorithm.evaluateAll(Arrays.asList(solutions));
		algorithm.evaluate(null);
		algorithm.evaluate(null);
		algorithm.evaluateAll(Arrays.asList(solutions));

		Assert.assertEquals(202, algorithm.getNumberOfEvaluations());
	}

	/**
	 * While this is technically an error as the {@code AbstractAlgorithm} is
	 * not serializable, the current contract of {@link Checkpoints} requires
	 * it to continue normally if the state file is not available.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testResumable() throws IOException {
		File file = TestUtils.createTempFile();
		Checkpoints checkpoints = new Checkpoints(new TestAbstractAlgorithm(),
				file, 0);
		checkpoints.step();
	}

}
