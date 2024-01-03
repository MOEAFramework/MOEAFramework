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
 * Linkage functions used by the LSMOP test problem suite.  This serves two goals:
 * <ol>
 *   <li>Creates a linkage between the first decision variable and each variable in x_s.
 *   <li>Applies a transformation to the decision variables.
 * </ol>
 */
public interface LinkageFunction {
	
	/**
	 * Applies the linkage function to the decision variables.
	 * 
	 * @param M the number of objectives
	 * @param D the number of decision variables
	 * @param x the original decision variables
	 * @return the decision variables after applying the linkage function
	 */
	public double[] apply(int M, int D, double[] x);
	
	/**
	 * Linear linkage function.
	 */
	public static final LinkageFunction Linear = (M, D, x) -> {
		double[] result = new double[D];
		
		for (int i = 0; i < M - 1; i++) {
			result[i] = x[i];
		}
		
		for (int i = 0; i < D - M + 1; i++) {
			double L = 1.0 + (i + M) / (double)D;
			result[i + M - 1] = L * x[i + M - 1] - 10.0 * x[0];
		}
		
		return result;
	};
	
	/**
	 * Nonlinear linkage function.
	 */
	public static final LinkageFunction NonLinear = (M, D, x) -> {
		double[] result = new double[D];
		
		for (int i = 0; i < M - 1; i++) {
			result[i] = x[i];
		}
		
		for (int i = 0; i < D - M + 1; i++) {
			double L = 1.0 + Math.cos(0.5 * Math.PI * (i + M) / (double)D);
			result[i + M - 1] = L * x[i + M - 1] - 10.0 * x[0];
		}
		
		return result;
	};

}
