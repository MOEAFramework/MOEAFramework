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

/**
 * The single-objective Beale problem with an optimum at {@code x = (3.0, 0.5)} with {@code f(x) = 0}.
 */
public class Beale extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Beale problem.
	 */
	public Beale() {
		super(2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		solution.setObjective(0, Math.pow(1.5 - x + x*y, 2.0) + Math.pow(2.25 - x + x*y*y, 2.0)
				+ Math.pow(2.625 - x + x*y*y*y, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, EncodingUtils.newReal(-4.5, 4.5));
		solution.setVariable(1, EncodingUtils.newReal(-4.5, 4.5));
		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution idealPoint = newSolution();
		EncodingUtils.setReal(idealPoint, new double[] { 3.0, 0.5 });
		
		evaluate(idealPoint);
		
		result.add(idealPoint);
		return result;
	}

}