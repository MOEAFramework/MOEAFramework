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
 */
public class MinMaxObjectiveComparator implements AggregateObjectiveComparator, Serializable {
	
	private static final long serialVersionUID = 5018011451944335718L;
	
	private double[] weights;
	
	public MinMaxObjectiveComparator() {
		this(1.0);
	}
	
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
	
	public static double calculateFitness(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, Math.max(weights[i], 0.0001) * solution.getObjective(i));
		}

		return max;
	}

}
