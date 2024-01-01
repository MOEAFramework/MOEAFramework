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

import org.moeaframework.util.Vector;

public interface ParetoFrontGeometry {
	
	public double[] apply(int M, double[] G, double[][] A, double[] x_f);
	
	public static final ParetoFrontGeometry Linear = (M, G, A, x_f) -> {
		double[] F = new double[M];
		
		for (int i = 0; i < M; i++) {
			double prod = 1.0;
			double g = Vector.dot(G, A[i]);
			
			for (int j = 0; j < M-i-1; j++) {
				prod *= x_f[j];
			}
			
			F[i] = (1.0 + g) * prod * (i > 0 ? (1.0 - x_f[M-i-1]) : 1.0);
		}
		
		return F;
	};
	
	public static final ParetoFrontGeometry Convex = (M, G, A, x_f) -> {
		double[] F = new double[M];
		
		for (int i = 0; i < M; i++) {
			double prod = 1.0;
			double g = Vector.dot(G, A[i]);
			
			for (int j = 0; j < M-i-1; j++) {
				prod *= Math.cos(0.5 * Math.PI * x_f[j]);
			}

			F[i] = (1.0 + g) * prod * (i > 0 ? Math.sin(0.5 * Math.PI * x_f[M-i-1]) : 1.0);
		}
		
		return F;
	};
	
	public static final ParetoFrontGeometry Disconnected = (M, G, A, x_f) -> {
		double[] F = new double[M];
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = x_f[i];
		}
		
		double g = 1.0 + Vector.dot(G, A[M-1]);
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			sum += F[i] / (1.0 + g) * (1.0 + Math.sin(3.0 * Math.PI * F[i]));
		}
		
		F[M-1] = (1.0 + g) * (M - sum);
		
		return F;
	};

}
