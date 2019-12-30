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

/**
 * Compares two solutions using aggregate constraint violation and the Pareto
 * dominance relation as originally proposed by Kalyanmoy Deb.
 * <p>
 * References:
 * <ol>
 * <li>Deb, K., "An Efficient Constraint Handling Method for Genetic
 * Algorithms." Computer Methods in Applied Mechanics and Engineering, pp.
 * 311--338, 1998.
 * </ol>
 * 
 * @see AggregateConstraintComparator
 * @see ParetoObjectiveComparator
 */
public class ParetoDominanceComparator extends ChainedComparator implements
Serializable {

	private static final long serialVersionUID = -3198596505754896119L;

	/**
	 * Constructs a Pareto dominance comparator.
	 */
	public ParetoDominanceComparator() {
		super(new AggregateConstraintComparator(),
				new ParetoObjectiveComparator());
	}

}
