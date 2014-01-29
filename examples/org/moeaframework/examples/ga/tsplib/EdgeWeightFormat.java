/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
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
