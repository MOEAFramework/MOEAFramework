/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.problem;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

/**
 * Problem mimicking a CPU intensive task, intended for benchmarking the
 * distributed computing capabilities of this framework.
 */
public class CPUDemo extends AbstractProblem {

	/**
	 * Constructs a CPU intensive problem.
	 */
	public CPUDemo() {
		super(1, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		long count = 0;

		for (long i = 0; i < 400000000; i++) {
			count++;
		}

		solution.setObjective(0, (double)count);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new RealVariable(0, 1));
		return solution;
	}

}
