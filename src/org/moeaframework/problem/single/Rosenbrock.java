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
 * The single-objective Rosenbrock problem with an optimum at {@code x = (1, 1)} with {@code f(x) = 0}.  While this
 * implements the two variable version, there does exist a generalized version to {@code N} variables.
 * <p>
 * References:
 * <ol>
 *   <li>Rosenbrock, H.H. (1960). "An Automatic Method for Finding the Greatest or Least Value of a Function". The
 *       Computer Journal. 3 (3): 175–184. doi:10.1093/comjnl/3.3.175. ISSN 0010-4620.
 * </ol>
 */
public class Rosenbrock extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Rosenbrock problem with two decision variables.
	 */
	public Rosenbrock() {
		this(2);
	}
	
	/**
	 * Constructs a new instance of the Rosenbrock problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Rosenbrock(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double sum = 0.0;
		
		for (int i = 0; i < numberOfVariables - 1; i++) {
			sum += 100*Math.pow(x[i+1] - x[i]*x[i], 2.0) + Math.pow(1 - x[i], 2.0);
		}
		
		solution.setObjective(0, sum);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-10, 10));
		}

		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution idealPoint = newSolution();
		EncodingUtils.setReal(idealPoint, Vector.of(numberOfVariables, 1.0));
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}