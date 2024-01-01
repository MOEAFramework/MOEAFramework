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

public interface LinkageFunction {
	
	public double[] apply(int M, int D, int N_ns);
	
	public static final LinkageFunction Linear = (M, D, N_ns) -> {
		double[] L = new double[N_ns];
		
		for (int i = 0; i < N_ns; i++) {
			L[i] = 1.0 + (i + M) / (double)D;
		}
		
		return L;
	};
	
	public static final LinkageFunction NonLinear = (M, D, N_ns) -> {
		double[] L = new double[N_ns];
		
		for (int i = 0; i < N_ns; i++) {
			L[i] = 1.0 + Math.cos(0.5 * Math.PI * (i + M) / (double)D);
		}
		
		return L;
	};

}
