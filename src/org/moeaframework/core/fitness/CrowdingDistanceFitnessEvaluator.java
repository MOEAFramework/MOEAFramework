/* Copyright 2009-2018 David Hadka
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

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

/**
 * Assigns fitness values based on the crowding distance from fast
 * non-dominated sorting.
 */
public class CrowdingDistanceFitnessEvaluator implements FitnessEvaluator {
	
	/**
	 * Constructs a new crowding distance fitness evaluator.
	 */
	public CrowdingDistanceFitnessEvaluator() {
		super();
	}

	@Override
	public void evaluate(Population population) {
		new FastNondominatedSorting().updateCrowdingDistance(copy(population));
		
		for (Solution solution : population) {
			solution.setAttribute(FITNESS_ATTRIBUTE,
					(Double)solution.getAttribute(FastNondominatedSorting.CROWDING_ATTRIBUTE));
		}
	}
	
	/**
	 * Returns a copy of the population.  The fast non-dominated sorting
	 * routine reorders solutions in the population, so creating a copy allows
	 * the original population to remain unchanged.  
	 * 
	 * @param population the original population
	 * @return a copy of the population
	 */
	private Population copy(Population population) {
		Population result = new Population();
		
		for (Solution solution : population) {
			result.add(solution);
		}
		
		return result;
	}
	
	@Override
	public boolean areLargerValuesPreferred() {
		return true;
	}

}
