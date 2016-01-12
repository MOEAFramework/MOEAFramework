/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.ga.tsplib;

/**
 * Enumeration of the various formats in which edge weights (distances) can be
 * specified.
 */
public enum EdgeWeightFormat {
	
	/**
	 * Weights are given by a function.
	 */
	FUNCTION,
	
	/**
	 * Weights are given by a full matrix.
	 */
	FULL_MATRIX,
	
	/**
	 * Row-wise upper triangular matrix (excluding diagonal).
	 */
	UPPER_ROW,
	
	/**
	 * Row-wise lower triangular matrix (excluding diagonal).
	 */
	LOWER_ROW,
	
	/**
	 * Row-wise upper triangular matrix.
	 */
	UPPER_DIAG_ROW,
	
	/**
	 * Row-wise lower triangular matrix.
	 */
	LOWER_DIAG_ROW,
	
	/**
	 * Column-wise upper triangular matrix (without diagonal).
	 */
	UPPER_COL,
	
	/**
	 * Column-wise lower triangular matrix (without diagonal).
	 */
	LOWER_COL,
	
	/**
	 * Column-wise upper triangular matrix.
	 */
	UPPER_DIAG_COL,
	
	/**
	 * Column-wise lower triangular matrix.
	 */
	LOWER_DIAG_COL

}
