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
 * Compares two solutions using the Pareto dominance relation on the objectives.
 * <p>
 * In general, the {@link ParetoDominanceComparator} should be used instead as
 * it also incorporates constraint violation checks.
 */
public class ParetoObjectiveComparator implements DominanceComparator,
Serializable {

	private static final long serialVersionUID = 5086102885918799148L;

	/**
	 * Constructs a Pareto dominance comparator.
	 */
	public ParetoObjectiveComparator() {
		super();
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			int flag = Double.compare(solution1.getObjective(i), solution2.getObjective(i));
			
			if (flag < 0) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (flag > 0) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (dominate1 == dominate2) {
			return 0;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}

}
