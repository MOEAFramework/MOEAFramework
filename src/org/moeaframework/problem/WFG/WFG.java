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
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * Implements methods shared by all problems in the WFG test suite.
 */
public abstract class WFG extends AbstractProblem 
implements AnalyticalProblem {

	/**
	 * The number of position-related variables defined by this problem.
	 */
	protected final int k;

	/**
	 * The number of distance-related variables defined by this problem.
	 */
	protected final int l;

	/**
	 * The number of objectives defined by this problem.
	 */
	protected final int M;

	/**
	 * Constructs a WFG problem instance with the specified number of 
	 * position-related and distance-related variables and the specified number
	 * of objectives.
	 * 
	 * @param k the number of position-related variables defined by this problem
	 * @param l the number of distance-related variables defined by this problem
	 * @param M the number of objectives defined by this problem
	 */
	public WFG(int k, int l, int M) {
		super(k + l, M);
		this.k = k;
		this.l = l;
		this.M = M;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(k + l, M);

		for (int i = 0; i < k + l; i++) {
			solution.setVariable(i, new RealVariable(0.0, 2.0 * (i + 1)));
		}

		return solution;
	}

}
