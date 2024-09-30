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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.algorithm.extension.CheckpointExtension;
import org.moeaframework.algorithm.extension.Extension;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockRealProblem;

public class AbstractAlgorithmTest {

	private static class TestAbstractAlgorithm extends AbstractAlgorithm {

		private int numberOfIterations;

		public TestAbstractAlgorithm() {
			super(new MockRealProblem(2));
		}
		
		@Override
		public void initialize() {
			super.initialize();
			numberOfEvaluations += 100;
		}

		@Override
		public void iterate() {
			numberOfIterations++;
			numberOfEvaluations += 100;
		}

		@Override
		public NondominatedPopulation getResult() {
			throw new UnsupportedOperationException();
		}

		public int getNumberOfIterations() {
			return numberOfIterations;
		}

	}
	
	private static class TestExtension implements Extension {
		
		private int registerCount = 0;
		private int stepCount = 0;
		private int initializeCount = 0;
		private int terminateCount = 0;

		@Override
		public void onRegister(Algorithm algorithm) {
			registerCount++;
		}

		@Override
		public void onStep(Algorithm algorithm) {
			stepCount++;
		}

		@Override
		public void onInitialize(Algorithm algorithm) {
			initializeCount++;
		}

		@Override
		public void onTerminate(Algorithm algorithm) {
			terminateCount++;
		}
		
		public void assertCalls(int expectedSteps) {
			Assert.assertEquals(1, registerCount);
			Assert.assertEquals(1, initializeCount);
			Assert.assertEquals(expectedSteps, stepCount);
			Assert.assertEquals(1, terminateCount);
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
		Assert.assertEquals(2, algorithm.getNumberOfIterations());

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
		Assert.assertEquals(1, algorithm.getNumberOfIterations());

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

	public void testStepAfterTermination() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.step();
		algorithm.terminate();
		
		Assert.assertTrue(algorithm.isTerminated());
		
		algorithm.step();
		
		Assert.assertFalse(algorithm.isTerminated());
		
		algorithm.terminate();
		
		Assert.assertTrue(algorithm.isTerminated());
	}
	
	public void testRunAfterTermination() {
		AbstractAlgorithm algorithm = new TestAbstractAlgorithm();

		algorithm.run(100);
		Assert.assertTrue(algorithm.isTerminated());
		algorithm.run(100);
		Assert.assertTrue(algorithm.isTerminated());
		Assert.assertEquals(200, algorithm.getNumberOfEvaluations());
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
		File file = TempFiles.createFile();
		
		Algorithm algorithm = new TestAbstractAlgorithm();
		algorithm.addExtension(new CheckpointExtension(file, 0));
		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());
		
		algorithm.step();
		algorithm.step();
		Assert.assertFileWithContent(file);
		
		algorithm = new TestAbstractAlgorithm();
		algorithm.addExtension(new CheckpointExtension(file, 0));
		Assert.assertEquals(200, algorithm.getNumberOfEvaluations());
	}
	
	@Test
	public void testExtensions() {
		TestExtension extension = new TestExtension();
		
		Algorithm algorithm = new TestAbstractAlgorithm();
		algorithm.addExtension(extension);
		algorithm.step();
		algorithm.step();
		algorithm.terminate();
		
		extension.assertCalls(2);
	}

}
