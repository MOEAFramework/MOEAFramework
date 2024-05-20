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
package org.moeaframework.core.comparator;

import java.util.Comparator;

import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Solution;

/**
 * Compares two solutions based on their fitness value.
 * 
 * @see FitnessEvaluator
 */
public class FitnessComparator implements DominanceComparator, Comparator<Solution> {
	
	/**
	 * {@code true} if larger fitness values are preferred; otherwise smaller fitness values are preferred.
	 */
	private final boolean largerValuesPreferred;
	
	/**
	 * Constructs a dominance comparator for comparing solutions based on their fitness value.
	 * 
	 * @param largerValuesPreferred {@code true} if larger fitness values are preferred; otherwise smaller fitness
	 *        values are preferred
	 */
	public FitnessComparator(boolean largerValuesPreferred) {
		super();
		this.largerValuesPreferred = largerValuesPreferred;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		return (largerValuesPreferred ? -1 : 1) *
				Double.compare(FitnessEvaluator.getFitness(solution1), FitnessEvaluator.getFitness(solution2));
	}

}
