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
package org.moeaframework.problem.MaF;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.LSMOP.CorrelationMatrix;
import org.moeaframework.problem.LSMOP.LSMOP;
import org.moeaframework.problem.LSMOP.LSMOP8;
import org.moeaframework.problem.LSMOP.LinkageFunction;
import org.moeaframework.problem.LSMOP.ParetoFrontGeometry;
import org.moeaframework.problem.LSMOP.ShapeFunction;
import org.moeaframework.util.Vector;

/**
 * The MaF15 test problem, which is an inverted version of the {@link LSMOP8} test problem with {@code N_k=2} and
 * {@code N_ns=20}..  This problem exhibits the following properties:
 * <ul>
 *   <li>Convex Pareto front
 *   <li>Partially separable decision variables
 *   <li>Non-uniform correlations between decision variables and objectives
 * </ul>
 */
public class MaF15 extends LSMOP implements AnalyticalProblem {

	/**
	 * Constructs an MaF15 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF15(int numberOfObjectives) {
		super(numberOfObjectives, 2, 20 * numberOfObjectives, ShapeFunction.Griewank, ShapeFunction.Sphere,
				LinkageFunction.NonLinear, CorrelationMatrix.Overlapped, InvertedConvex);
	}
	
	private static final ParetoFrontGeometry InvertedConvex = (M, G, A, x_f) -> {
		double[] F = new double[M];
		
		for (int i = 0; i < M; i++) {
			double prod = 1.0;
			double g = Vector.dot(G, A[i]);
			
			for (int j = 0; j < M-i-1; j++) {
				prod *= Math.cos(0.5 * Math.PI * x_f[j]);
			}

			F[i] = (1.0 + g) * (1.0 - prod * (i > 0 ? Math.sin(0.5 * Math.PI * x_f[M-i-1]) : 1.0));
		}
		
		return F;
	};
	
	@Override
	public Solution generate() {
		double[] f = Vector.uniform(getNumberOfObjectives());
		double divisor = Math.sqrt(StatUtils.sumSq(f));
		
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			f[i] = 1.0 - f[i] / divisor;
		}
		
		Solution solution = new Solution(0, getNumberOfObjectives());
		solution.setObjectives(f);
		return solution;
	}

}
