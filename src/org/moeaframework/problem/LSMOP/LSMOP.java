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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public abstract class LSMOP implements Problem {
	
	/**
	 * The default number of subcomponents in each decision variable group.
	 */
	public static final int DEFAULT_N_k = 5;
	
	/**
	 * The configured number of objectives.
	 */
	private final int M;
	
	/**
	 * The configured number of subcomponents in each decision variable group.
	 */
	private final int N_k;
	
	/**
	 * The shape function used for even indices.
	 */
	private final ShapeFunction g1;
	
	/**
	 * The shape function used for odd indices.
	 */
	private final ShapeFunction g2;
	
	/**
	 * The shape of the Pareto front.
	 */
	private final ParetoFrontGeometry pf;
		
	/**
	 * The computed number of decision variables.
	 */
	private int D;
	
	/**
	 * The computed number of subcomponents in each decision variable group.
	 */
	private int[] NNg;
	
	/**
	 * The computed linkage function.
	 */
	private double[] L;
	
	/**
	 * The computed correlation matrix.
	 */
	private double[][] A;
	
	public LSMOP(int M, int N_k, ShapeFunction g1, ShapeFunction g2, LinkageFunction linkage,
			CorrelationMatrix correlationMatrix, ParetoFrontGeometry pf) {
		super();
		this.M = M;
		this.N_k = N_k;
		this.g1 = g1;
		this.g2 = g2;
		this.pf = pf;
		
		int N_ns = 100 * M;
		
		// Chaos-based random number generator
		final double a = 3.8;
		double[] C = new double[M];
		double sumC = 0.0;
		
		C[0] = a * 0.1 * (1.0 - 0.1);
		sumC += C[0];
		
		for (int i = 1; i < M; i++) {
			C[i] = a * C[i-1] * (1.0 - C[i-1]);
			sumC += C[i];
		}
		
		// Non-uniform subcomponent sizes
		NNg = new int[M];
		int sumNNg = 0;
		
		for (int i = 0; i < M; i++) {
			NNg[i] = (int)Math.ceil(Math.round(C[i] / sumC * N_ns) / (double)N_k);
			sumNNg += NNg[i];
		}
		
		// Compute number of decision variables
		N_ns = sumNNg * N_k;
		D = (M - 1) + N_ns;
		
		// Compute linkage function and correlation matrix
		L = linkage.apply(M, D, N_ns);
		A = correlationMatrix.apply(M);
	}

	@Override
	public int getNumberOfVariables() {
		return D;
	}

	@Override
	public int getNumberOfObjectives() {
		return M;
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(D, M, 0);
		
		for (int i = 0; i < D; i++) {
			if (i < M - 1) {
				solution.setVariable(i, EncodingUtils.newReal(0.0, 1.0));
			} else {
				solution.setVariable(i, EncodingUtils.newReal(0.0, 10.0));
			}
		}
		
		return solution;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		
		for (int i = 0; i < D - M + 1; i++) {
			x[i + M - 1] = L[i] * x[i + M - 1] - 10.0 * x[0];
		}
		
		double[] x_f = Arrays.copyOfRange(x, 0, M - 1);
		List<double[]> x_s = new ArrayList<double[]>();
		
		for (int i = 0; i < M; i++) {
			int sumNNg = 0;
			
			for (int j = 0; j < i; j++) {
				sumNNg += NNg[j];
			}
			
			int index1 = (i > 0 ? M + N_k * sumNNg : M) - 1;
			int index2 = index1 + N_k*NNg[i];
						
			x_s.add(Arrays.copyOfRange(x, index1, index2));
		}
		
		double[] G = new double[M];
		
		for (int i = 0; i < M; i++) {
			ShapeFunction g_func = i % 2 == 0 ? g1 : g2;
			double g = 0.0;
			
			for (int j = 0; j < N_k; j++) {
				double[] x_ss = Arrays.copyOfRange(x_s.get(i), j*NNg[i], (j+1)*NNg[i]);
				g += g_func.apply(x_ss) / NNg[i];
			}
			
			G[i] = g / N_k;
		}
				
		solution.setObjectives(pf.apply(M, G, A, x_f));
	}

}
