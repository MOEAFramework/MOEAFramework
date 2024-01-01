/* Copyright 2009-2023 David Hadka
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

public interface CorrelationMatrix {
	
	public double[][] apply(int M);
	
	public static final CorrelationMatrix Separable = M -> {
		double[][] A = new double[M][M];
		
		for (int i = 0; i < M; i++) {
			A[i][i] = 1.0;
		}
		
		return A;
	};
	
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
