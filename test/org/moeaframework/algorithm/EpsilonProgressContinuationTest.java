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
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.UM;

/**
 * Tests the {@link EpsilonProgressContinuation} class.
 */
public class EpsilonProgressContinuationTest {
	
	protected Population population;
	
	protected EpsilonBoxDominanceArchive archive;
	
	protected MockAlgorithm algorithm;
	
	protected EpsilonProgressContinuation adaptiveTimeContinuation;
	
	protected int numberOfRestarts;
	
	private class MockAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

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
			//do nothing
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
		archive = new EpsilonBoxDominanceArchive(0.5);
		algorithm = new MockAlgorithm();
		adaptiveTimeContinuation = new EpsilonProgressContinuation(
				algorithm,
				10,
				100,
				4.0,
				1,
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
	public void testNoProgress() {
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.75, 0.25));
		archive.add(TestUtils.newSolution(0.25, 0.75));
		
		for (int i=0; i<100; i++) {
			if (i % 2 == 0) {
				archive.add(TestUtils.newSolution(PRNG.nextDouble(0.51, 0.99), PRNG.nextDouble(0.01, 0.49)));
			} else {
				archive.add(TestUtils.newSolution(PRNG.nextDouble(0.01, 0.49), PRNG.nextDouble(0.51, 0.99)));
			}
			
			adaptiveTimeContinuation.step();
		}
		
		//note that the first two solutions count towards eps-progress
		Assert.assertEquals(9, numberOfRestarts);
	}
	
	@Test
	public void testProgress() {
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.75, 0.25));
		archive.add(TestUtils.newSolution(0.25, 0.75));
		
		for (int i=0; i<100; i++) {
			if (i == 55) {
				archive.add(TestUtils.newSolution(0.1, 0.1));
				
				//shrink population to avoid population ratio restart
				while (population.size() > 5) {
					population.remove(0);
				}
			} else if (i % 2 == 0) {
				archive.add(TestUtils.newSolution(PRNG.nextDouble(0.51, 0.99), PRNG.nextDouble(0.01, 0.49)));
			} else {
				archive.add(TestUtils.newSolution(PRNG.nextDouble(0.01, 0.49), PRNG.nextDouble(0.51, 0.99)));
			}
			
			adaptiveTimeContinuation.step();
		}
		
		Assert.assertEquals(8, numberOfRestarts);
	}
	
}
