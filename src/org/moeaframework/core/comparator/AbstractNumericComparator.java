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
import java.util.function.Function;
import org.moeaframework.core.Solution;

/**
 * Abstract class for implementing a numeric or "fitness" based comparator.  This comparison is based on a single
 * value providing a total ordering of solutions.  Specify which property or attribute is consumed by providing an
 * appropriate getter method.
 */
public abstract class AbstractNumericComparator<T extends Number & Comparable<T>> implements DominanceComparator,
Comparator<Solution> {
	
	/**
	 * Constant value to indicate larger values are preferred by this comparator.  We recommend using this constant
	 * for readability.
	 */
	public static final boolean LARGER_VALUES_PREFERRED = true;
	
	/**
	 * Constant value to indicate smaller values are preferred by this comparator.  We recommend using this constant
	 * for readability.
	 */
	public static final boolean SMALLER_VALUES_PREFERRED = false;
	
	/**
	 * {@code true} if larger values are preferred; otherwise smaller values are preferred.
	 */
	private final boolean largerValuesPreferred;
	
	/**
	 * The function that reads or computes the fitness value of each solution.
	 */
	private final Function<Solution, T> getter;
	
	/**
	 * Constructs a dominance comparator for comparing solutions based on some fitness value.
	 * 
	 * @param largerValuesPreferred {@code true} if larger values are preferred; otherwise smaller
	 * @param getter the function that reads or computes the fitness value of each solution
	 */
	public AbstractNumericComparator(boolean largerValuesPreferred, Function<Solution, T> getter) {
		super();
		this.largerValuesPreferred = largerValuesPreferred;
		this.getter = getter;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		return (largerValuesPreferred ? -1 : 1) * getter.apply(solution1).compareTo(getter.apply(solution2));
	}

}
