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

import static org.moeaframework.core.FastNondominatedSorting.CROWDING_ATTRIBUTE;

import java.io.Serializable;
import java.util.Comparator;

import org.moeaframework.core.Solution;

/**
 * Compares solutions using their crowding distance. Solutions with larger
 * crowding distances are preferred.
 */
public class CrowdingComparator implements DominanceComparator,
Comparator<Solution>, Serializable {

	private static final long serialVersionUID = 4973403102558586390L;

	/**
	 * Constructs a dominance comparator for comparing solutions using their 
	 * crowding distance.
	 */
	public CrowdingComparator() {
		super();
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double crowding1 = (Double)solution1.getAttribute(CROWDING_ATTRIBUTE);
		double crowding2 = (Double)solution2.getAttribute(CROWDING_ATTRIBUTE);
		
		return -Double.compare(crowding1, crowding2);
	}

}
