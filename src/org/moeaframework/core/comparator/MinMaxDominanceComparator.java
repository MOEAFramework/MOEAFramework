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
 * Weighted min-max aggregate function that supports constraints.
 * 
 * @see AggregateConstraintComparator
 * @see MinMaxObjectiveComparator
 */
public class MinMaxDominanceComparator extends ChainedComparator implements AggregateObjectiveComparator {
	
	/**
	 * Constructs a new dominance comparator using a weighted min-max aggregate function and constraints.  One weight
	 * should be given for each objective; if fewer weights are provided, the last weight is repeated for the
	 * remaining objectives.  Defaults to weights of {@code 1.0} if none are provided.
	 * 
	 * @param weights the weight vector
	 */
	public MinMaxDominanceComparator(double... weights) {
		super(new AggregateConstraintComparator(), new MinMaxObjectiveComparator(weights));
	}
	
	@Override
	public double[] getWeights() {
		return ((MinMaxObjectiveComparator)comparators[1]).getWeights();
	}
	
	@Override
	public double calculate(Solution solution) {
		return ((MinMaxObjectiveComparator)comparators[1]).calculate(solution);
	}

}
