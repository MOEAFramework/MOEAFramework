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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockProblem;
import org.moeaframework.mock.MockSolution;

public class AdditiveEpsilonIndicatorFitnessEvaluatorTest {
	
	private AdditiveEpsilonIndicatorFitnessEvaluator fitness;
	
	@Before
	public void setUp() {
		fitness = new AdditiveEpsilonIndicatorFitnessEvaluator(new MockProblem(3));
	}
	
	@After
	public void tearDown() {
		fitness = null;
	}

	@Test
	public void testEqual() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.5, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 0.5, 1.0);
		
		Assert.assertEquals(0.0, fitness.calculateIndicator(solution1, solution2), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testBetter() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.5, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 0.75, 1.0);
		
		Assert.assertEquals(0.0, fitness.calculateIndicator(solution1, solution2), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testWorse() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.75, 0.75);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 0.5, 1.0);
		
		Assert.assertEquals(0.5, fitness.calculateIndicator(solution1, solution2), TestThresholds.HIGH_PRECISION);
	}
	
}
