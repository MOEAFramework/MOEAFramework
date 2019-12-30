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
package org.moeaframework.core.fitness;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link IndicatorFitnessEvaluatorTest} class.
 */
public class IndicatorFitnessEvaluatorTest {
	
	/**
	 * Tests if the updated fitness from {@code removeAndUpdate()} equals those
	 * produced by {@code evaluate} on the resulting population.
	 */
	@Test
	public void testRemoveAndUpdate() {
		Problem problem = new MockRealProblem();
		
		Population population = new Population();
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(0.25, 0.25));
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.75, 0.75));
		population.add(TestUtils.newSolution(1.0, 0.0));
		
		IndicatorFitnessEvaluator evaluator = new HypervolumeFitnessEvaluator(problem);
		evaluator.evaluate(population);
		evaluator.removeAndUpdate(population, 2);
		
		
		Population expectedPopulation = new Population();
		expectedPopulation.add(TestUtils.newSolution(0.0, 1.0));
		expectedPopulation.add(TestUtils.newSolution(0.25, 0.25));
		expectedPopulation.add(TestUtils.newSolution(0.75, 0.75));
		expectedPopulation.add(TestUtils.newSolution(1.0, 0.0));
		
		evaluator.evaluate(expectedPopulation);
		
		
		Assert.assertEquals(expectedPopulation.size(), population.size());
		
		for (int i = 0; i < population.size(); i++) {
			TestUtils.assertEquals(
					(Double)population.get(i).getAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE),
					(Double)expectedPopulation.get(i).getAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE));
		}
	}

}
