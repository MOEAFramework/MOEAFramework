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
 * The single-objective Schwefel problem with an optimum at {@code x = (420.9687, ..., 420.9687)} with {@code f(x) = 0}.
 * <p>
 * References:
 * <ol>
 *   <li>Laguna, M. and Marti, R.  "Experimental Testing of Advanced Scatter Search Designs for Global Optimization of
 *       Multimodal Functions."  (2002).
 * </ol>
 */
public class Schwefel extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Schwefel problem with two decision variables.
	 */
	public Schwefel() {
		this(2);
	}
	
	/**
	 * Constructs a new instance of the Schwefel problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Schwefel(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double sum = 0.0;
		
		for (int i = 0; i < numberOfVariables; i++) {
			sum += x[i] * Math.sin(Math.sqrt(Math.abs(x[i])));
		}
		
		solution.setObjective(0, 418.9829 * numberOfVariables - sum);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-500, 500));
		}

		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution idealPoint = newSolution();
		EncodingUtils.setReal(idealPoint, Vector.of(numberOfVariables, 420.9687));
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}