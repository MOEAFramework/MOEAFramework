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

import org.moeaframework.core.Solution;

/**
 * Compares solutions based on their magnitude of constraint violations.  Absolute values of constraints are used,
 * so only the magnitude of the constraint violation is important.
 */
public class AggregateConstraintComparator implements DominanceComparator, Comparator<Solution> {

	/**
	 * Constructs an aggregate constraint comparator.
	 */
	public AggregateConstraintComparator() {
		super();
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double constraints1 = solution1.getSumOfConstraintViolations();
		double constraints2 = solution2.getSumOfConstraintViolations();
		
		return Double.compare(constraints1, constraints2);
	}

}
