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
package org.moeaframework.core.comparator;

import static org.moeaframework.core.FitnessEvaluator.FITNESS_ATTRIBUTE;

import java.io.Serializable;
import java.util.Comparator;

import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Solution;

/**
 * Compares two solutions based on their {@code FITNESS_ATTRIBUTE} value.
 * 
 * @see FitnessEvaluator
 */
public class FitnessComparator implements DominanceComparator, 
Comparator<Solution>, Serializable {

	private static final long serialVersionUID = -3480841924543305614L;
	
	/**
	 * {@code true} if larger fitness values are preferred; otherwise smaller
	 * fitness values are preferred.
	 */
	private final boolean largerValuesPreferred;

	/**
	 * Constructs a dominance comparator for comparing solutions based on their
	 * {@code FITNESS_ATTRIBUTE} value.  By default, smaller fitness values
	 * are preferred.
	 * 
	 * @deprecated Use {@link #FitnessComparator(boolean)} instead; this method
	 *             will be removed in a future version
	 */
	@Deprecated
	public FitnessComparator() {
		this(false);
	}
	
	/**
	 * Constructs a dominance comparator for comparing solutions based on their
	 * {@code FITNESS_ATTRIBUTE} value.
	 * 
	 * @param largerValuesPreferred {@code true} if larger fitness values are
	 *        preferred; otherwise smaller fitness values are preferred
	 */
	public FitnessComparator(boolean largerValuesPreferred) {
		super();
		this.largerValuesPreferred = largerValuesPreferred;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		return (largerValuesPreferred ? -1 : 1) * Double.compare(
				(Double)solution1.getAttribute(FITNESS_ATTRIBUTE),
				(Double)solution2.getAttribute(FITNESS_ATTRIBUTE));
	}

}
