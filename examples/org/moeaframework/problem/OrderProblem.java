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
import org.moeaframework.core.variable.Permutation;

/**
 * A example problem using permutations.  The goal is to find the permutation
 * that is in increasing order.
 */
public class OrderProblem extends AbstractProblem {
	
	private final int size;
	
	public OrderProblem(int size) {
		super(1, 1);
		this.size = size;
	}

	@Override
	public void evaluate(Solution solution) {
		int sum = 0;
		Permutation permutation = (Permutation)solution.getVariable(0);
		
		for (int i=0; i<size-1; i++) {
			if (permutation.get(i) > permutation.get(i+1)) {
				sum++;
			}
		}
		
		solution.setObjective(0, sum);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Permutation(size));
		return solution;
	}

}
