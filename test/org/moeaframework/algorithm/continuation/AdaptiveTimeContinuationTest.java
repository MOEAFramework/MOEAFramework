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
package org.moeaframework.algorithm.continuation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.moeaframework.Assert;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Population;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.mock.MockEpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.mock.MockSolution;

public class AdaptiveTimeContinuationTest {

	protected Population population;
	
	protected EpsilonBoxDominanceArchive archive;
	
	protected MockEpsilonBoxEvolutionaryAlgorithm algorithm;
	
	protected AdaptiveTimeContinuationExtension adaptiveTimeContinuation;
	
	protected int numberOfRestarts;
	
	@Before
	public void setUp() {
		population = new Population();
		archive = new EpsilonBoxDominanceArchive(0.01);
		algorithm = new MockEpsilonBoxEvolutionaryAlgorithm(population, archive);
		adaptiveTimeContinuation = new AdaptiveTimeContinuationExtension(
				10,
				100,
				0.25,
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
		
		algorithm.addExtension(adaptiveTimeContinuation);
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
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		for (int i=0; i<1000; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1000, algorithm.getNumberOfSteps());
		Assert.assertEquals(10, numberOfRestarts);
	}
	
	@Test
	public void testPopulationRatio() {
		//population=6, archive=2, ratio within 25%
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.0, 1.0));
		archive.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		for (int i=0; i<10; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(0, numberOfRestarts);
		
		//population=5, archive=2, population too small but not yet checked
		population.remove(0);
		
		for (int i=0; i<9; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(0, numberOfRestarts);
		
		//checked on 10th step
		algorithm.step();
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(8, population.size());
		
		//no other restarts should occur up to maxWindowSize
		for (int i=0; i<99; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
	}
	
	@Test
	public void testMaxPopulationSize() {
		archive.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.75, 0.25));
		archive.add(MockSolution.of().withObjectives(0.5, 0.5));
		archive.add(MockSolution.of().withObjectives(0.25, 0.75));
		archive.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		for (int i=0; i<100; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(20, population.size());
	}
	
	@Test
	public void testMinPopulationSize() {
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		for (int i=0; i<100; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1, numberOfRestarts);
		Assert.assertEquals(4, population.size());
	}
	
	/**
	 * Ensures an empty archive results in an error rather than an infinite loop.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEmptyArchive() {
		for (int i=0; i<100; i++) {
			algorithm.step();
		}
	}
	
	/**
	 * Ensures that if the population ratio would result in a population size exceeding the maximum population size, a
	 * sequence of back-to-back restarts would not occur due to the population ratio being violated.
	 */
	@Test
	public void testPopulationRatioConflictWithMaxPopulationSize() {
		archive.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.9, 0.1));
		archive.add(MockSolution.of().withObjectives(0.75, 0.25));
		archive.add(MockSolution.of().withObjectives(0.5, 0.5));
		archive.add(MockSolution.of().withObjectives(0.25, 0.75));
		archive.add(MockSolution.of().withObjectives(0.1, 0.9));
		archive.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		for (int i=0; i<1000; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(10, numberOfRestarts);
		Assert.assertEquals(20, population.size());
	}
	
}
