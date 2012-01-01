/* Copyright 2009-2012 David Hadka
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
 * Compares solutions first using the {@link AggregateConstraintComparator}
 * followed by the {@link EpsilonBoxDominanceComparator}.
 */
public class EpsilonBoxConstraintComparator extends
EpsilonBoxDominanceComparator {

	private static final long serialVersionUID = -5691444587961578117L;

	/**
	 * The aggregate constraint comparator.
	 */
	private final AggregateConstraintComparator comparator;

	/**
	 * Constructs a dominance comparator for comparing solutions first using the
	 * {@link AggregateConstraintComparator} followed by the
	 * {@link EpsilonBoxDominanceComparator} with the specified &epsilon; value.
	 * 
	 * @param epsilon the epsilon value used by this comparator
	 */
	public EpsilonBoxConstraintComparator(double epsilon) {
		super(epsilon);
		comparator = new AggregateConstraintComparator();
	}

	/**
	 * Constructs a dominance comparator for comparing solutions first using the
	 * {@link AggregateConstraintComparator} followed by the
	 * {@link EpsilonBoxDominanceComparator} with the specified &epsilon; value.
	 * 
	 * @param epsilons the epsilon values used by this comparator
	 */
	public EpsilonBoxConstraintComparator(double[] epsilons) {
		super(epsilons);
		comparator = new AggregateConstraintComparator();
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		int flag = comparator.compare(solution1, solution2);

		if (flag != 0) {
			isSameBox = false;
			return flag;
		} else {
			return super.compare(solution1, solution2);
		}
	}

}
