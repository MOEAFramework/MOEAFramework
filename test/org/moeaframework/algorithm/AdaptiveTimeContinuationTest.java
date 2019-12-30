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

import java.io.NotSerializableException;
import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.UM;

/**
 * Test the {@link AdaptiveTimeContinuation} class.
 */
public class AdaptiveTimeContinuationTest {

	protected Population population;
	
	protected EpsilonBoxDominanceArchive archive;
	
	protected MockAlgorithm algorithm;
	
	protected AdaptiveTimeContinuation adaptiveTimeContinuation;
	
	protected int numberOfRestarts;
	
	private class MockAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {
		
		private int numberOfIterations;

		@Override
		public Population getPopulation() {
			return population;
		}

		@Override
		public Problem getProblem() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NondominatedPopulation getResult() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void step() {
			numberOfIterations++;
		}

		@Override
		public void evaluate(Solution solution) {
			//do nothing
		}

		@Override
		public int getNumberOfEvaluations() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isTerminated() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void terminate() {
			throw new UnsupportedOperationException();
		}

		@Override
		public EpsilonBoxDominanceArchive getArchive() {
			return archive;
		}

		public int getNumberOfIterations() {
			return numberOfIterations;
		}

		@Override
		public Serializable getState() throws NotSerializableException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setState(Object state) throws NotSerializableException {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Before
	public void setUp() {
		population = new Population();
		archive = new EpsilonBoxDominanceArchive(0.01);
		algorithm = new MockAlgorithm();
		adaptiveTimeContinuation = new AdaptiveTimeContinuation(
				algorithm,
				10,
				100,
				4.0,
				4,
				20,
				new UniformSelection(),
				new UM(1.0));
		
		adaptiveTimeContinuation.addRestartListener(new RestartListener() {

			@Override
			public void restarted(RestartEvent event) {
				numberOfRestarts++;
			}
			
		});
		
		numberOfRestarts = 0;
	}
	
	@After
	public void tearDown() {
		population = null;
		archive = null;
		algorithm = null;
		adaptiveTimeContinuation = null;
	}
	
	@Test
	public void testMaxWindow() {
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.0, 1.0));
		
		for (int i=0; i<1000; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(1000, algorithm.getNumberOfIterations());
		Assert.assertEquals(10, numberOfRestarts);
	}
	
	@Test
	public void testPopulationRatio() {
		//population=6, archive=2, ratio within 25%
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));
		
		for (int i=0; i<10; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(0, numberOfRestarts);
		
		//population=5, archive=2, population too small but not yet checked
		population.remove(0);
		
		for (int i=0; i<9; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(0, numberOfRestarts);
		
		//checked on 10th step
		adaptiveTimeContinuation.step();
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(8, population.size());
		
		//no other restarts should occur up to maxWindowSize
		for (int i=0; i<99; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
	}
	
	@Test
	public void testMaxPopulationSize() {
		archive.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.75, 0.25));
		archive.add(TestUtils.newSolution(0.5, 0.5));
		archive.add(TestUtils.newSolution(0.25, 0.75));
		archive.add(TestUtils.newSolution(0.0, 1.0));
		
		for (int i=0; i<100; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(20, population.size());
	}
	
	@Test
	public void testMinPopulationSize() {
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.0, 1.0));
		
		for (int i=0; i<100; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(4, population.size());
	}
	
	/**
	 * Ensures an empty archive results in an error rather than an infinite
	 * loop.
	 */
	@Test(expected=Exception.class)
	public void testEmptyArchive() {
		for (int i=0; i<100; i++) {
			adaptiveTimeContinuation.step();
		}
	}
	
	/**
	 * Ensures that if the population ratio would result in a population size
	 * exceeding the maximum population size, a sequence of back-to-back
	 * restarts would not occur due to the population ratio being violated.
	 */
	@Test
	public void testPopulationRatioConflictWithMaxPopulationSize() {
		archive.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.9, 0.1));
		archive.add(TestUtils.newSolution(0.75, 0.25));
		archive.add(TestUtils.newSolution(0.5, 0.5));
		archive.add(TestUtils.newSolution(0.25, 0.75));
		archive.add(TestUtils.newSolution(0.1, 0.9));
		archive.add(TestUtils.newSolution(0.0, 1.0));
		
		for (int i=0; i<1000; i++) {
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(10, numberOfRestarts);
		Assert.assertEquals(20, population.size());
	}
	
}
