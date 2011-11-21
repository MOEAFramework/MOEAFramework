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
package org.moeaframework.studies.multioperator;

import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public abstract class MultioperatorTestProblem extends AbstractProblem {
	
	public MultioperatorTestProblem() {
		super(2, 1);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, new RealVariable(0, 1));
		solution.setVariable(1, new RealVariable(0, 1));
		return solution;
	}
	
	public abstract boolean isOptimum(Solution solution, double epsilon);
	
	public boolean isAtOptimum(Population population, double epsilon) {
		int count = 0;
		
		for (Solution solution : population) {
			if (isOptimum(solution, epsilon)) {
				count++;
			}
		}
		
		return (count >= population.size()) && (count > 0);
	}

}
