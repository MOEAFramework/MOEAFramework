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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class IndicatorFitnessEvaluatorTest {
	
	@Test
	public void testRemoveAndUpdate() {
		Problem problem = new MockRealProblem(2);
		
		Population population = new Population();
		population.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		population.add(MockSolution.of(problem).withObjectives(0.25, 0.25));
		population.add(MockSolution.of(problem).withObjectives(0.5, 0.5));
		population.add(MockSolution.of(problem).withObjectives(0.75, 0.75));
		population.add(MockSolution.of(problem).withObjectives(1.0, 0.0));
		
		IndicatorFitnessEvaluator evaluator = new HypervolumeFitnessEvaluator(problem);
		evaluator.evaluate(population);
		evaluator.removeAndUpdate(population, 2);
		
		Population expectedPopulation = new Population();
		expectedPopulation.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		expectedPopulation.add(MockSolution.of(problem).withObjectives(0.25, 0.25));
		expectedPopulation.add(MockSolution.of(problem).withObjectives(0.75, 0.75));
		expectedPopulation.add(MockSolution.of(problem).withObjectives(1.0, 0.0));
		
		evaluator.evaluate(expectedPopulation);
		
		Assert.assertEquals(expectedPopulation.size(), population.size());
		
		for (int i = 0; i < population.size(); i++) {
			Assert.assertEquals(Fitness.getAttribute(population.get(i)),
					Fitness.getAttribute(expectedPopulation.get(i)));
		}
	}

}
