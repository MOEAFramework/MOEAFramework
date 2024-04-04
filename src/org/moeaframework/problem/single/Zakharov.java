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
 * The single-objective Zakharov problem with an optimum at {@code x = (0, ..., 0)} with {@code f(x) = 0}.
 */
public class Zakharov extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Zakharov problem with two decision variables.
	 */
	public Zakharov() {
		this(2);
	}
	
	/**
	 * Constructs a new instance of the Zakharov problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Zakharov(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		
		double sum1 = 0.0;
		double sum2 = 0.0;
		
		for (int i = 0; i < numberOfVariables; i++) {
			sum1 += x[i] * x[i];
			sum2 += 0.5 * (i+1) * x[i];
		}
		
		solution.setObjective(0, sum1 + Math.pow(sum2, 2.0) + Math.pow(sum2, 4.0));
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
		EncodingUtils.setReal(idealPoint, Vector.of(numberOfVariables, 0.0));
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}