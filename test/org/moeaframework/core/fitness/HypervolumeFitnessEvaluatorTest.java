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
package org.moeaframework.core.fitness;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;

public class HypervolumeFitnessEvaluatorTest {
	
	private HypervolumeFitnessEvaluator fitness;
	
	@Before
	public void setUp() {
		fitness = new HypervolumeFitnessEvaluator(new MockRealProblem(2));
	}
	
	@Test
	public void testSame() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.5);

		Assert.assertEquals(0.0, fitness.calculateIndicator(solution1, solution2), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testDominated() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 1.0);

		Assert.assertEquals(-0.75, fitness.calculateIndicator(solution1, solution2), TestEnvironment.HIGH_PRECISION);
		Assert.assertEquals(0.75, fitness.calculateIndicator(solution2, solution1), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testNonDominated() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);

		Assert.assertEquals(0.25, fitness.calculateIndicator(solution1, solution2), TestEnvironment.HIGH_PRECISION);
		Assert.assertEquals(0.25, fitness.calculateIndicator(solution2, solution1), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testDominatedPopulation() {
		Population population = new Population();
		population.add(MockSolution.of().withObjectives(0.0, 0.0));
		population.add(MockSolution.of().withObjectives(1.0, 1.0));
		
		fitness.evaluate(population);
		
		Assert.assertLessThan(Fitness.getAttribute(population.get(0)),
				Fitness.getAttribute(population.get(1)));
	}
	
	@Test
	public void testNonDominatedPopulation() {
		Population population = new Population();
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		fitness.evaluate(population);
		
		Assert.assertEquals(Fitness.getAttribute(population.get(0)),
				Fitness.getAttribute(population.get(1)), TestEnvironment.HIGH_PRECISION);
	}
	
}
