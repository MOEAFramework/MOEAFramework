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
import org.moeaframework.core.PRNG;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.mock.MockEpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.mock.MockSolution;

public class EpsilonProgressContinuationTest {
	
	protected Population population;
	
	protected EpsilonBoxDominanceArchive archive;
	
	protected MockEpsilonBoxEvolutionaryAlgorithm algorithm;
	
	protected EpsilonProgressContinuationExtension epsilonProgressContinuation;
	
	protected int numberOfRestarts;
		
	@Before
	public void setUp() {
		population = new Population();
		archive = new EpsilonBoxDominanceArchive(0.5);
		algorithm = new MockEpsilonBoxEvolutionaryAlgorithm(population, archive);
		epsilonProgressContinuation = new EpsilonProgressContinuationExtension(
				10,
				100,
				4.0,
				1,
				20,
				new UniformSelection(),
				new UM(1.0));
		
		epsilonProgressContinuation.addRestartListener(new RestartListener() {

			@Override
			public void restarted(RestartEvent event) {
				numberOfRestarts++;
			}
			
		});
		
		algorithm.addExtension(epsilonProgressContinuation);
		numberOfRestarts = 0;
	}
	
	@After
	public void tearDown() {
		population = null;
		archive = null;
		algorithm = null;
		epsilonProgressContinuation = null;
	}

	@Test
	public void testNoProgress() {
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.75, 0.25));
		archive.add(MockSolution.of().withObjectives(0.25, 0.75));
		
		for (int i=0; i<100; i++) {
			if (i % 2 == 0) {
				archive.add(MockSolution.of().withObjectives(PRNG.nextDouble(0.51, 0.99), PRNG.nextDouble(0.01, 0.49)));
			} else {
				archive.add(MockSolution.of().withObjectives(PRNG.nextDouble(0.01, 0.49), PRNG.nextDouble(0.51, 0.99)));
			}
			
			algorithm.step();
		}
		
		//note that the first two solutions count towards eps-progress
		Assert.assertEquals(9, numberOfRestarts);
	}
	
	@Test
	public void testProgress() {
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		archive.add(MockSolution.of().withObjectives(0.75, 0.25));
		archive.add(MockSolution.of().withObjectives(0.25, 0.75));
		
		for (int i=0; i<100; i++) {
			if (i == 55) {
				archive.add(MockSolution.of().withObjectives(0.1, 0.1));
				
				//shrink population to avoid population ratio restart
				while (population.size() > 5) {
					population.remove(0);
				}
			} else if (i % 2 == 0) {
				archive.add(MockSolution.of().withObjectives(PRNG.nextDouble(0.51, 0.99), PRNG.nextDouble(0.01, 0.49)));
			} else {
				archive.add(MockSolution.of().withObjectives(PRNG.nextDouble(0.01, 0.49), PRNG.nextDouble(0.51, 0.99)));
			}
			
			algorithm.step();
		}
		
		Assert.assertEquals(8, numberOfRestarts);
	}
	
}
