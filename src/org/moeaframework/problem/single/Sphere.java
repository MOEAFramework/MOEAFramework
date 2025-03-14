/* Copyright 2009-2025 David Hadka
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

import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.Vector;

/**
 * The single-objective Sphere problem with an optimum at {@code x = (0, ..., 0)} with {@code f(x) = 0}.
 */
public class Sphere extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Sphere problem with two decision variables.
	 */
	public Sphere() {
		this(2);
	}
	
	/**
	 * Constructs a new instance of the Sphere problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Sphere(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double sum = 0.0;
		
		for (int i = 0; i < numberOfVariables; i++) {
			sum += Math.pow(x[i], 2.0);
		}
		
		solution.setObjectiveValue(0, sum);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-10.0, 10.0));
		}

		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution idealPoint = newSolution();
		RealVariable.setReal(idealPoint, Vector.of(numberOfVariables, 0.0));
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}