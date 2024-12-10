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
package org.moeaframework.mock;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.initialization.Initialization;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.problem.Problem;

public class MockEvolutionaryAlgorithm extends AbstractEvolutionaryAlgorithm {
	
	private static final int EVALUATIONS_PER_STEP = 50;
	
	private int numberOfSteps;
		
	public MockEvolutionaryAlgorithm() {
		this(new MockRealProblem(2));
	}
	
	public MockEvolutionaryAlgorithm(Problem problem) {
		super(problem, EVALUATIONS_PER_STEP, new Population(), new NondominatedPopulation(),
				new RandomInitialization(problem), OperatorFactory.getInstance().getVariation(problem));
	}
	
	public MockEvolutionaryAlgorithm(Problem problem, int initialPopulationSize, Population population,
			NondominatedPopulation archive, Initialization initialization, Variation variation) {
		super(problem, initialPopulationSize, population, archive, initialization, variation);
	}

	@Override
	public String getName() {
		return "MockEvolutionaryAlgorithm";
	}
	
	public int getNumberOfSteps() {
		return numberOfSteps;
	}
	
	public int getNumberOfEvaluationsPerStep() {
		return EVALUATIONS_PER_STEP;
	}

	@Override
	public void step() {
		numberOfSteps++;
		super.step();
	}
	
	@Override
	public void iterate() {
		numberOfEvaluations += getNumberOfEvaluationsPerStep();
	}
	
	public void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations = numberOfEvaluations;
	}
	
}
