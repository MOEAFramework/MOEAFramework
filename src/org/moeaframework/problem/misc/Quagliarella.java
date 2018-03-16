/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Quagliarella problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Convex Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 *   <li>Quagliarella, D., and Vicini, A. (1998).  "Sub-population Policies for
 *       a Parallel Multiobjective Genetic Algorithm with Applications to Wing
 *       Design."  In proceedings of the 1998 IEEE International Conference on
 *       Systems, Man, and Cybernetics, pp. 3142-3147.
 * </ol>
 */
public class Quagliarella extends AbstractProblem {
	
	/**
	 * Constructs the Quagliarella problem.
	 */
	public Quagliarella() {
		this(16);
	}

	public Quagliarella(int numberOfVariables) {
		super(numberOfVariables, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double A1 = 0.0;
		double A2 = 0.0;
		
		for (int i=0; i<numberOfVariables; i++) {
			A1 += Math.pow(x[i], 2.0) - 10.0*Math.cos(2.0*Math.PI*x[i]) + 10;
			A2 += Math.pow(x[i] - 1.5, 2.0) - 
					10.0*Math.cos(2.0*Math.PI*(x[i] - 1.5)) + 10;
		}
		
		solution.setObjective(0, Math.sqrt(A1 / numberOfVariables));
		solution.setObjective(1, Math.sqrt(A2 / numberOfVariables));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2);
		
		for (int i=0; i<numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-5.12, 5.12));
		}
		
		return solution;
	}

}
