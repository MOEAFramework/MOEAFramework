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
package org.moeaframework.problem.WFG;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * The WFG6 test problem.
 */
public class WFG6 extends WFG {

	/**
	 * Constructs a WFG6 problem instance with the specified number of 
	 * position-related and distance-related variables and the specified number
	 * of objectives.
	 * 
	 * @param k the number of position-related variables for this problem
	 * @param l the number of distance-related variables for this problem
	 * @param M the number of objectives for this problem
	 */
	public WFG6(int k, int l, int M) {
		super(k, l, M);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] v = EncodingUtils.getReal(solution);
		double[] f = Problems.WFG6(v, k, M);
		solution.setObjectives(f);
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		EncodingUtils.setReal(solution,
				Solutions.WFG_2_thru_7_random_soln(k, l));
		evaluate(solution);
		return solution;
	}

}
