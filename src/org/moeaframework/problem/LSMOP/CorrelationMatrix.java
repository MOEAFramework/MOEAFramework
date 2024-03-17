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
package org.moeaframework.problem.LSMOP;

/**
 * Correlation matrices used by the LSMOP test problem suite.  The matrix describes the correlation between each
 * decision variable group and the corresponding objective value.
 * <p>
 * A value of {@code 0.0} indicates the decision variable group does not contribute to the objective value; whereas a
 * value of {@code 1.0} includes the decision variable group in the calculation.  While it's possible for the matrix to
 * contain values between {@code [0, 1]}, the LSMOP suite does not use such intermediate values.
 */
public interface CorrelationMatrix {
	
	/**
	 * Returns an M x M correlation matrix containing values between [0, 1].

	 * @param M the dimension
	 * @return the correlation matrix
	 */
	public double[][] apply(int M);
	
	/**
	 * Creates a correlation matrix with no correlation.  Each decision variable group is associated with a single
	 * objective.
	 */
	public static final CorrelationMatrix Separable = M -> {
		double[][] A = new double[M][M];
		
		for (int i = 0; i < M; i++) {
			A[i][i] = 1.0;
		}
		
		return A;
	};
	
	/**
	 * Creates a correlation matrix where the i-th and (i+1)-th decision variable groups are associated with the
	 * {@code i}-th objective.
	 */
	public static final CorrelationMatrix Overlapped = M -> {
		double[][] A = new double[M][M];
		
		for (int i = 0; i < M; i++) {
			A[i][i] = 1.0;
			
			if (i+1 < M) {
				A[i][i+1] = 1.0;
			}
		}
		
		return A;
	};
	
	/**
	 * Creates a full correlation matrix where every decision variable group is included in the objective value
	 * calculation.
	 */
	public static final CorrelationMatrix Full = M -> {
		double[][] A = new double[M][M];
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				A[i][j] = 1.0;
			}
		}
		
		return A;
	};

}
