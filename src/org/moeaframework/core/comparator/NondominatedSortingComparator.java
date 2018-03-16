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
import java.util.Comparator;

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.Solution;

/**
 * Compares two solutions using their rank and crowding distance. Rank is the
 * primary comparison criteria and the crowding distance is used to break ties.
 * 
 * @see FastNondominatedSorting
 */
public class NondominatedSortingComparator extends ChainedComparator implements
Comparator<Solution>, Serializable {

	private static final long serialVersionUID = 4973403102558586390L;

	/**
	 * Constructs a dominance comparator for comparing solutions using their 
	 * rank and crowding distance.
	 */
	public NondominatedSortingComparator() {
		super(new RankComparator(), new CrowdingComparator());
	}

}
