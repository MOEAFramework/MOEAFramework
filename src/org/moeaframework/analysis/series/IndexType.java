/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.analysis.series;

/**
 * The type of index used by {@link IndexedResult} or {@link ResultSeries}.
 */
public enum IndexType {
	
	/**
	 * The index represents the number of function evaluations (NFE).  When finding the entry with a given NFE,
	 * we typically find the nearest match.
	 */
	NFE,
	
	/**
	 * The index represents the sequential position of the result.  This typically means the entries are not associated
	 * with a single run of an algorithm.
	 */
	Index,
	
	/**
	 * The series contains a single entry, which is returned regardless of what index is provided.  This is typically
	 * used by reference sets.
	 */
	Singleton
	
}