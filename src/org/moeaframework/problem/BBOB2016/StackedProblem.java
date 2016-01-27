/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Combines two or more single-objective {@link BBOBFunction}s into a
 * multiobjective problem.  The single-objective functions should all have the
 * same number of decision variables.
 */
public class StackedProblem extends AbstractProblem {

	/**
	 * The single-objective functions.
	 */
	private final BBOBFunction[] functions;
	
	/**
	 * Constructs a new multiobjective problem from two or more single-objective
	 * functions.
	 * 
	 * @param functions the single-objective functions
	 */
	public StackedProblem(BBOBFunction... functions) {
		super(functions[0].getNumberOfVariables(), functions.length);
		this.functions = functions;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		
		for (int i = 0; i < functions.length; i++) {
			Solution instanceSolution = functions[i].newSolution();
			EncodingUtils.setReal(instanceSolution, x);
			
			functions[i].evaluate(instanceSolution);
			
			solution.setObjective(i, instanceSolution.getObjective(0));
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-5.0, 5.0));
		}
		
		return solution;
	}
	
}
