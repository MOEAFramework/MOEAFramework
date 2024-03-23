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
package org.moeaframework.core.indicator;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockConstraintProblem;
import org.moeaframework.mock.MockRealProblem;

public abstract class AbstractIndicatorTest<T extends Indicator> {
	
	/**
	 * Constructs a new instance of this indicator.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the indicator
	 */
	public abstract T createInstance(Problem problem, NondominatedPopulation referenceSet);
	
	/**
	 * Returns the worst value this indicator produces.
	 * 
	 * @return the worst value this indicator produces
	 */
	public abstract double getWorstValue();
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyReferenceSet() {
		createInstance(new MockRealProblem(2), new NondominatedPopulation());
	}
	
	@Test
	public void testEmptyApproximationSet() {
		Indicator indicator = createInstance(new MockRealProblem(2), getDefaultReferenceSet());
		Assert.assertEquals(getWorstValue(), indicator.evaluate(new NondominatedPopulation()), Settings.EPS);
	}
	
	@Test
	public void testInfeasibleApproximationSet() {
		Problem problem = new MockConstraintProblem(2);
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Solution solution = problem.newSolution();
		solution.setObjectives(new double[] { 0.5, 0.5 });
		solution.setConstraints(new double[] { 0.0, 1.0, 0.0 });
		approximationSet.add(solution);

		Indicator indicator = createInstance(problem, getDefaultReferenceSet());
		Assert.assertEquals(getWorstValue(), indicator.evaluate(approximationSet), Settings.EPS);
	}
	
	/**
	 * Returns a reference set resulting in default bounds between (0, 0, ..., 0) and (1, 1, ..., 1).
	 * 
	 * @return the reference set
	 */
	protected NondominatedPopulation getDefaultReferenceSet() {
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		return referenceSet;
	}

	/**
	 * Converts a population into a 2D array that can be used by the JMetal indicators.
	 * 
	 * @param population the population
	 * @return the 2D array representation of the problem
	 */
	protected double[][] toArray(Population population) {
		double[][] array = new double[population.size()][];

		for (int i = 0; i < population.size(); i++) {
			Solution solution = population.get(i);
			array[i] = new double[solution.getNumberOfObjectives()];

			for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
				array[i][j] = solution.getObjective(j);
			}
		}

		return array;
	}

	/**
	 * Returns a new, randomly-generated approximation set for the specified problem. The resulting approximation set
	 * will contain at least {@code N} solutions, but depending on the degree of dominance the actual size may be
	 * significantly less than {@code N}.
	 * 
	 * @param problemName the problem
	 * @param N the number of randomly-generated solutions
	 * @return a new, randomly-generated approximation set for the specified problem
	 */
	protected NondominatedPopulation generateApproximationSet(String problemName, int N) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		Initialization initialization = new RandomInitialization(problem);
		Solution[] solutions = initialization.initialize(N);

		for (Solution solution : solutions) {
			problem.evaluate(solution);
		}

		NondominatedPopulation result = new NondominatedPopulation();
		result.addAll(solutions);
		return result;
	}

}
