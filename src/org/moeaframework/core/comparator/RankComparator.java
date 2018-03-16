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

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.io.Serializable;
import java.util.Comparator;

import org.moeaframework.core.Solution;

/**
 * Compares solutions using their {@code RANK_ATTRIBUTE} value.
 */
public class RankComparator implements DominanceComparator, 
Comparator<Solution>, Serializable {

	private static final long serialVersionUID = 4973403102558586390L;

	/**
	 * Constructs a dominance comparator for comparing solutions using their
	 * {@code RANK_ATTRIBUTE} value.
	 */
	public RankComparator() {
		super();
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		int rank1 = (Integer)solution1.getAttribute(RANK_ATTRIBUTE);
		int rank2 = (Integer)solution2.getAttribute(RANK_ATTRIBUTE);
		
		return Integer.compare(rank1, rank2);
	}

}
