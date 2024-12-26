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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.core.indicator.PISAHypervolume;
import org.moeaframework.core.objective.NormalizedObjective;
import org.moeaframework.core.population.Population;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.validate.Validate;

/**
 * Assigns the fitness of solutions based on their contribution to the overall hypervolume.
 */
public class HypervolumeContributionFitnessEvaluator implements FitnessEvaluator {
	
	/**
	 * The problem.
	 */
	private final Problem problem;
	
	/**
	 * The offset used when determining the reference point for the hypervolume calculation.
	 */
	private final double offset;
	
	/**
	 * Constructs a new hypervolume contribution fitness evaluator with an offset of 100.
	 * 
	 * @param problem the problem
	 */
	public HypervolumeContributionFitnessEvaluator(Problem problem) {
		this(problem, 100.0);
	}
	
	/**
	 * Constructs a new hypervolume contribution fitness evaluator.
	 * 
	 * @param problem the problem
	 * @param offset the offset used when determining the reference point for the hypervolume calculation.
	 */
	public HypervolumeContributionFitnessEvaluator(Problem problem, double offset) {
		super();
		this.problem = problem;
		this.offset = offset;
	}

	@Override
	public void evaluate(Population population) {
		if (population.size() <= 2) {
			for (Solution solution : population) {
				Fitness.setAttribute(solution, 0.0);
			}
		} else {
			int numberOfObjectives = problem.getNumberOfObjectives();
			List<Solution> solutions = normalize(population);
			List<Solution> solutionsCopy = new ArrayList<>(solutions);
			
			double totalVolume = PISAHypervolume.calculateHypervolume(solutionsCopy, solutionsCopy.size(),
					numberOfObjectives);
			
			for (int i = 0; i < population.size(); i++) {
				solutionsCopy = new ArrayList<>(solutions);
				solutionsCopy.remove(i);
				
				double volume = PISAHypervolume.calculateHypervolume(solutionsCopy, solutionsCopy.size(),
						numberOfObjectives);
				
				Fitness.setAttribute(population.get(i), totalVolume - volume);
			}
		}
	}
	
	/**
	 * Normalizes the population using a reference point calculated by the maximum extent of the population plus an
	 * offset.
	 * 
	 * @param population the population to normalize
	 * @return the normalized solutions
	 */
	private List<Solution> normalize(Population population) {
		if (population.size() < 2) {
			Validate.that("population", population)
				.fails("Requires at least two solutions to compute bounds for normalization");
		}
		
		double[] min = population.getLowerBounds();
		double[] max = population.getUpperBounds();
		List<Solution> result = new ArrayList<>();
		
		for (Solution solution : population) {
			Solution newSolution = solution.copy();
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				NormalizedObjective obj = newSolution.getObjective(i).normalize(min[i], max[i]);
				obj.setValue(1.0 - obj.getValue() + offset);
			}

			result.add(newSolution);
		}
		
		return result;
	}

	@Override
	public boolean areLargerValuesPreferred() {
		return true;
	}

}
