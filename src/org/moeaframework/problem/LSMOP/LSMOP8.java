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
package org.moeaframework.problem.LSMOP;

/**
 * The mixed-modality, mixed-separability LSMOP8 problem.
 */
public class LSMOP8 extends LSMOP {
	
	/**
	 * Constructs the LSMOP8 problem.
	 * 
	 * @param M the number of objectives
	 */
	public LSMOP8(int M) {
		this(M, DEFAULT_N_k);
	}
	
	/**
	 * Constructs the LSMOP8 problem.
	 * 
	 * @param M the number of objectives
	 * @param N_k the number of subcomponents in each decision variable group
	 */
	public LSMOP8(int M, int N_k) {
		super(M, N_k, ShapeFunction.Griewank, ShapeFunction.Sphere, LinkageFunction.NonLinear,
				CorrelationMatrix.Overlapped, ParetoFrontGeometry.Convex);
	}

}
