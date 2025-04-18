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
 * Abstract class for implementing comparators based on some weighted aggregate of objective values.
 */
public abstract class AbstractAggregateObjectiveComparator implements AggregateObjectiveComparator {
		
	/**
	 * The weight vector.
	 */
	protected double[] weights;
	
	/**
	 * Constructs a new comparator using a weighted aggregate function.  One weight should be given for each objective;
	 * if fewer weights are provided, the last weight is repeated for the remaining objectives.  Defaults to weights
	 * of {@code 1.0} if none are provided.
	 * 
	 * @param weights the weight vector
	 */
	public AbstractAggregateObjectiveComparator(double... weights) {
		super();
		this.weights = weights;
		
		if ((this.weights == null) || (this.weights.length == 0)) {
			this.weights = new double[] { 1.0 };
		}
	}
	
	@Override
	public double[] getWeights() {
		return weights;
	}
	
	@Override
	public int compare(Solution solution1, Solution solution2) {
		double fitness1 = calculate(solution1);
		double fitness2 = calculate(solution2);
		
		return Double.compare(fitness1, fitness2);
	}
	
}