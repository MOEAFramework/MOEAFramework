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
package org.moeaframework.core.fitness;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.moeaframework.TestUtils;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockRealProblem;

public class HypervolumeFitnessEvaluatorTest {
	
	private HypervolumeFitnessEvaluator fitness;
	
	@Before
	public void setUp() {
		fitness = new HypervolumeFitnessEvaluator(new MockRealProblem(2));
	}
	
	@Test
	public void testSame() {
		Solution solution1 = TestUtils.newSolution(0.5, 0.5);
		Solution solution2 = TestUtils.newSolution(0.5, 0.5);

		Assert.assertEquals(0.0, fitness.calculateIndicator(solution1, solution2), Settings.EPS);
	}
	
	@Test
	public void testDominated() {
		Solution solution1 = TestUtils.newSolution(0.0, 0.0);
		Solution solution2 = TestUtils.newSolution(1.0, 1.0);

		Assert.assertEquals(-0.75, fitness.calculateIndicator(solution1, solution2), Settings.EPS);
		Assert.assertEquals(0.75, fitness.calculateIndicator(solution2, solution1), Settings.EPS);
	}
	
	@Test
	public void testNonDominated() {
		Solution solution1 = TestUtils.newSolution(1.0, 0.0);
		Solution solution2 = TestUtils.newSolution(0.0, 1.0);

		Assert.assertEquals(0.25, fitness.calculateIndicator(solution1, solution2), Settings.EPS);
		Assert.assertEquals(0.25, fitness.calculateIndicator(solution2, solution1), Settings.EPS);
	}
	
	@Test
	public void testDominatedPopulation() {
		Population population = new Population();
		population.add(TestUtils.newSolution(0.0, 0.0));
		population.add(TestUtils.newSolution(1.0, 1.0));
		
		fitness.evaluate(population);
		
		Assert.assertTrue((Double)population.get(0).getAttribute("fitness") < 
				(Double)population.get(1).getAttribute("fitness"));
	}
	
	@Test
	public void testNonDominatedPopulation() {
		Population population = new Population();
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		fitness.evaluate(population);
		
		Assert.assertEquals((Double)population.get(0).getAttribute("fitness"), 
				(Double)population.get(1).getAttribute("fitness"), Settings.EPS);
	}
	
}
