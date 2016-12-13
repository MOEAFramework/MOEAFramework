/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.Solution;

/**
 * Weighted min-max aggregate function.  By default, all weights are assumed
 * to be equal.
 * 
 * @see MinMaxDominanceComparator
 */
public class MinMaxObjectiveComparator implements AggregateObjectiveComparator, Serializable {
	
	private static final long serialVersionUID = 5018011451944335718L;
	
	/**
	 * The weight vector.
	 */
	private double[] weights;
	
	/**
	 * Constructs a new comparator using a weighted min-max aggregate function.
	 * Equal weights are used for each objective.
	 */
	public MinMaxObjectiveComparator() {
		this(1.0);
	}
	
	/**
	 * Constructs a new comparator using a weighted min-max aggregate function.
	 * One weight should be given for each objective; if fewer weights are
	 * provided, the last weight is repeated for the remaining objectives.
	 * 
	 * @param weights the weight vector
	 */
	public MinMaxObjectiveComparator(double... weights) {
		super();
		this.weights = weights;
		
		if ((this.weights == null) || (this.weights.length == 0)) {
			this.weights = new double[] { 1.0 };
		}
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double fitness1 = calculateFitness(solution1, weights);
		double fitness2 = calculateFitness(solution2, weights);
		
		return Double.compare(fitness1, fitness2);
	}
	
	/**
	 * Computes the weighted min-max aggregate fitness of the solution.  One
	 * weight should be given for each objective; if fewer weights are
	 * provided, the last weight is repeated for the remaining objectives.
	 * 
	 * @param solution the solution
	 * @param weights the weight vector
	 * @return the fitness, where smaller values are preferred
	 */
	public static double calculateFitness(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, Math.max(
					weights[i >= weights.length ? weights.length-1 : i],
					0.0001) * solution.getObjective(i));
		}

		return max;
	}

}
