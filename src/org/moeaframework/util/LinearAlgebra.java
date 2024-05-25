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
package org.moeaframework.util;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.moeaframework.core.Settings;

/**
 * Collection of linear algebra routines.
 */
public class LinearAlgebra {
	
	private LinearAlgebra() {
		super();
	}
	
	/**
	 * Solves a system of linear equations in the form {@code A*x = b} using Guassian Elimination with Partial
	 * Pivioting.  {@code A} must be a square matrix of size {@code N * N}, and {@code b} a vector of length {@code N}.
	 * <p>
	 * <strong>This method modifies the arguments!</strong>  Pass in copies if you need to retain the original values.
	 * <p>
	 * In our testing, this method is about 5-10x faster than other solvers like Commons Math's {@code LUDecomposition}
	 * on {@code N < 50}, however these other approaches may have better numerical stability.  If needed, this can be
	 * replaced with:
	 * <pre>
	 *   RealMatrix A = new Array2DRowRealMatrix(...);
	 *   RealVector b = new ArrayRealVector(...);
	 *   RealVector x = new LUDecomposition(A).getSolver().solve(b);
	 * </pre>
	 * 
	 * @param A the {@code A} matrix
	 * @param b the {@code b} vector
	 * @return the solved values for {@code x}
	 * @throws SingularMatrixException if the matrix is singular or nearly singular
	 * @throws DimensionMismatchException if the dimensions of the provided matrix and vector are not valid
	 * @see <a href="http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html">Reference Code</a>
	 */
	public static double[] lsolve(double[][] A, double[] b) {	
		int N = b.length;
		
		if (A.length != N) {
			throw new DimensionMismatchException(A.length, N);
		}
		
		for (int i = 0; i < N; i++) {
			if (A[i].length != N) {
				throw new DimensionMismatchException(A[i].length, N);
			}
		}

		for (int p = 0; p < N; p++) {
			// find pivot row and swap
			int max = p;

			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}

			double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;

			double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= Settings.EPS) {
				throw new SingularMatrixException();
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];

				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[N];

		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;

			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}

			x[i] = (b[i] - sum) / A[i][i];
		}

		return x;
	}

}
