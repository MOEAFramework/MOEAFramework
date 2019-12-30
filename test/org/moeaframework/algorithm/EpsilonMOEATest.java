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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link EpsilonMOEA} class.
 */
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
		
		algorithm = new EpsilonMOEA(problem, population, 
				new EpsilonBoxDominanceArchive(0.01),
				new TournamentSelection(2), new CompoundVariation(),
				new RandomInitialization(problem, 0));
	}
	
	@After
	public void tearDown() {
		problem = null;
		algorithm = null;
	}
	
	@Test
	public void testAddToPopulationDominated() {
		Solution solutionA = problem.newSolution();
		solutionA.setObjectives(new double[] { 0.0, 0.0 });
		
		Solution solutionB = problem.newSolution();
		solutionB.setObjectives(new double[] { 1.0, 1.0 });
		
		Solution solutionC = problem.newSolution();
		solutionC.setObjectives(new double[] { 0.5, 0.5 });
		
		for (int i=0; i<10; i++) {
			population.add(solutionA);
			population.add(solutionB);
		}

		for (int i=0; i<10; i++) {
			algorithm.addToPopulation(solutionC);
			TestUtils.assertEquals(solutionB, population.lastRemovedSolution);
		}
		
		population.lastRemovedIndex = -1;
		
		for (int i=0; i<10; i++) {
			algorithm.addToPopulation(solutionC);
			TestUtils.assertEquals(-1, population.lastRemovedIndex);
		}
	}
	
	@Test
	public void testAddToPopulationNondominated() {
		for (int i=0; i<10; i++) {
			Solution solution = problem.newSolution();
			double value = PRNG.nextDouble();
			
			solution.setObjectives(new double[] { value, 1.0-value });
			population.add(solution);
		}
		
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution solution = problem.newSolution();
			double value = PRNG.nextDouble();
			
			solution.setObjectives(new double[] { value, 1.0-value });
			algorithm.addToPopulation(solution);
			statistics.addValue(population.lastRemovedIndex);
		}
		
		TestUtils.assertUniformDistribution(0, population.size()-1, statistics);
	}

}
