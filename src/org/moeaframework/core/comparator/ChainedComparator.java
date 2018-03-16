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

import java.io.Serializable;

import org.moeaframework.core.Solution;

/**
 * Applies any number of comparators in succession, returning the result from
 * the first comparator producing a non-zero return value. If no comparators
 * produce a non-zero return value, {@code 0} is returned.
 * <p>
 * For example, the following:
 * <pre>
 * Comparator comparator = new ChainedComparator(comparator1, comparator2); 
 * return comparator.compare(s1, s2);
 * </pre>
 * is equivalent to
 * <pre>
 * int flag1 = comparator1.compare(s1, s2);
 * if (flag1 == 0) {
 *   return comparator2.compare(s1, s2);
 * } else {
 *   return flag1;
 * }
 * </pre>
 */
public class ChainedComparator implements DominanceComparator, Serializable {

	private static final long serialVersionUID = 4907755397965363873L;

	/**
	 * The comparators in the order they are to be applied.
	 */
	private DominanceComparator[] comparators;

	/**
	 * Constructs a chained comparator for applying the specified comparators in
	 * order, returning the result from the first comparator producing a 
	 * non-zero return value. If no comparators produce a non-zero return value,
	 * {@code 0} is returned.
	 * 
	 * @param comparators the comparators in the order they are to be applied
	 */
	public ChainedComparator(DominanceComparator... comparators) {
		super();
		this.comparators = comparators;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		for (DominanceComparator comparator : comparators) {
			int flag = comparator.compare(solution1, solution2);

			if (flag != 0) {
				return flag;
			}
		}

		return 0;
	}

}
