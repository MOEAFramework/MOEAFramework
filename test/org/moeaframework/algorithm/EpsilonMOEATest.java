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
package org.moeaframework.algorithm;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class EpsilonMOEATest {
	
	private Problem problem;
	
	private EpsilonMOEA algorithm;
	
	private TestPopulation population;
	
	private static class TestPopulation extends Population {
		
		private int lastRemovedIndex;
		
		private Solution lastRemovedSolution;

		@Override
		public void remove(int index) {
			lastRemovedIndex = index;
			lastRemovedSolution = get(index);
			super.remove(index);
		}

		@Override
		public boolean remove(Solution solution) {
			lastRemovedIndex = indexOf(solution);
			lastRemovedSolution = solution;
			return super.remove(solution);
		}
		
	}
	
	@Before
	public void setUp() {
		problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		population = new TestPopulation();
		
		algorithm = new EpsilonMOEA(problem, 100, population,
				new EpsilonBoxDominanceArchive(0.01),
				new TournamentSelection(2), new CompoundVariation(),
				new RandomInitialization(problem));
	}
	
	@After
	public void tearDown() {
		problem = null;
		algorithm = null;
	}
	
	@Test
	public void testAddToPopulationDominated() {
		Solution solutionA = MockSolution.of(problem).withObjectives(0.0, 0.0);
		Solution solutionB = MockSolution.of(problem).withObjectives(1.0, 1.0);
		Solution solutionC = MockSolution.of(problem).withObjectives(0.5, 0.5);
		
		for (int i=0; i<10; i++) {
			population.add(solutionA);
			population.add(solutionB);
		}

		for (int i=0; i<10; i++) {
			algorithm.addToPopulation(solutionC);
			Assert.assertEquals(solutionB, population.lastRemovedSolution);
		}
		
		population.lastRemovedIndex = -1;
		
		for (int i=0; i<10; i++) {
			algorithm.addToPopulation(solutionC);
			Assert.assertEquals(-1, population.lastRemovedIndex);
		}
	}
	
	@Test
	public void testAddToPopulationNondominated() {
		for (int i=0; i<10; i++) {
			double value = PRNG.nextDouble();
			population.add(MockSolution.of(problem).withObjectives(value, 1.0-value));
		}
		
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			double value = PRNG.nextDouble();
			algorithm.addToPopulation(MockSolution.of(problem).withObjectives(value, 1.0-value));
			statistics.addValue(population.lastRemovedIndex);
		}
		
		Assert.assertUniformDistribution(0, population.size()-1, statistics);
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		EpsilonMOEA algorithm = new EpsilonMOEA(problem);
		
		Assert.assertArrayEquals(algorithm.getArchive().getComparator().getEpsilons().toArray(),
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);
		
		algorithm.applyConfiguration(TypedProperties.of("epsilon", 0.1));
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);

		algorithm.applyConfiguration(TypedProperties.of("epsilon", 0.1, 0.2));
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);
	}

}
