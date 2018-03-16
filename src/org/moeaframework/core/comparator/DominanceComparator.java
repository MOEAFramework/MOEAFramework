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

import java.util.Comparator;

import org.moeaframework.core.Solution;

/**
 * Interface for comparing two solutions using a dominance relation.  A
 * dominance relation may impose a partial or total ordering on a set of 
 * solutions.
 * <p>
 * Implementations which also implement {@link Comparator} impose a 
 * total ordering on the set of solutions.  However, it is typically the case
 * that {@code (compare(x, y)==0) == (x.equals(y))} does not hold, and the
 * comparator may impose orderings that are inconsistent with equals.
 */
public interface DominanceComparator {

	/**
	 * Compares the two solutions using a dominance relation, returning
	 * {@code -1} if {@code solution1} dominates {@code solution2}, {@code 1} if
	 * {@code solution2} dominates {@code solution1}, and {@code 0} if the
	 * solutions are non-dominated.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @return {@code -1} if {@code solution1} dominates {@code solution2},
	 *         {@code 1} if {@code solution2} dominates {@code solution1}, and
	 *         {@code 0} if the solutions are non-dominated
	 */
	public int compare(Solution solution1, Solution solution2);

}
