package org.moeaframework.core.fitness;

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

public class CrowdingDistanceFitnessEvaluator implements FitnessEvaluator {

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
