/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.core.experimental;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Compares two solutions using the multiplicative epsilon-box dominance
 * comparator.
 * <p>
 * References:
 * <ol>
 * <li>Laumanns et al. "Combining Convergence and Diversity in Evolutionary
 * Multi-Objective Optimization." Evolutionary Computation. 10(3). 2002.
 * </ol>
 */
public class MultiplicativeEpsilonBoxDominanceComparator extends
		EpsilonBoxDominanceComparator {

	private static final long serialVersionUID = -2786495357830893355L;

	private static final ParetoDominanceComparator comparator = new ParetoDominanceComparator();

	/**
	 * Constructs a multiplicative epsilon-box dominance comparator with the 
	 * specified epsilon value.
	 * 
	 * @param epsilon the epsilon value used by this comparator
	 */
	public MultiplicativeEpsilonBoxDominanceComparator(double epsilon) {
		super(epsilon);
	}

	/**
	 * Constructs a multiplicative epsilon-box dominance comparator with the 
	 * specified epsilon values.
	 * 
	 * @param epsilons the epsilon values used by this comparator
	 */
	public MultiplicativeEpsilonBoxDominanceComparator(double[] epsilons) {
		super(epsilons);
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		setSameBox(false);

		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			int index1 = (int)Math.floor(Math.log(1.0 + solution1
					.getObjective(i))
					/ Math.log(1.0 + getEpsilon(i)));
			int index2 = (int)Math.floor(Math.log(1.0 + solution2
					.getObjective(i))
					/ Math.log(1.0 + getEpsilon(i)));

			if (index1 < index2) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (index1 > index2) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (!dominate1 && !dominate2) {
			setSameBox(true);

			if (comparator.compare(solution1, solution2) < 0) {
				return -1;
			} else {
				return 1;
			}
		} else if (dominate1 && dominate2) {
			return 0;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}

}
