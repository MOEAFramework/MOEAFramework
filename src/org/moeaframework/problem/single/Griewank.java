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
package org.moeaframework.problem.single;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;

/**
 * The single-objective Griewank problem with an optimum at {@code x = (0, ..., 0)} with {@code f(x) = 0}.
 * <p>
 * References:
 * <ol>
 *   <li>Molga, M. and Smutnicki, C. Test Functions for Optimization Needs."  (2005).
 * </ol>
 */
public class Griewank extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Griewank problem with two decision variables.
	 */
	public Griewank() {
		this(2);
	}
	
	/**
	 * Constructs a new instance of the Griewank problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Griewank(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double sum = 0.0;
		double product = 1.0;
		
		for (int i = 0; i < numberOfVariables; i++) {
			sum += x[i]*x[i] / 4000.0;
			product *= Math.cos(x[i] / Math.sqrt(i+1));
		}
		
		solution.setObjective(0, sum - product + 1.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-600, 600));
		}

		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution idealPoint = newSolution();
		EncodingUtils.setReal(idealPoint, Vector.of(numberOfVariables, 0.0));
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}