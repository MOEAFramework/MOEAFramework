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
package org.moeaframework.core.comparator;

import org.moeaframework.core.Solution;

/**
 * The weighted linear aggregate function.  By default, this method assumes equal weights for all objectives.
 */
public class LinearObjectiveComparator extends AbstractAggregateObjectiveComparator {
	
	/**
	 * Constructs a new comparator using a weighted linear aggregate function.
	 * 
	 * @param weights the weight vector
	 */
	public LinearObjectiveComparator(double... weights) {
		super(weights);
	}
	
	@Override
	public double calculate(Solution solution) {
		return calculate(solution, weights);
	}
	
	/**
	 * Computes the weighted linear aggregate vaalue of the solution.  One weight should be given for each objective;
	 * if fewer weights are provided, the last weight is repeated for the remaining objectives.
	 * 
	 * @param solution the solution
	 * @param weights the weight vector
	 * @return the fitness, where smaller values are preferred
	 */
	public static final double calculate(Solution solution, double[] weights) {
		double fitness = 0.0;
		
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			fitness += solution.getObjective(i).applyWeight(weights[i >= weights.length ? weights.length-1 : i]);
		}
		
		return fitness;
	}

}
