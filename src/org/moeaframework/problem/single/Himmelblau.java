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

import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * The single-objective Himmelblau's function with four local minima, all with {@code f(x) = 0}.
 * <p>
 * References:
 * <ol>
 *   <li>Himmelblau, D. (1972). Applied Nonlinear Programming. McGraw-Hill. ISBN 0-07-028921-2.
 * </ol>
 */
public class Himmelblau extends AbstractSingleObjectiveProblem {
	
	/**
	 * Constructs a new instance of the Himmelblau problem.
	 */
	public Himmelblau() {
		super(2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		solution.setObjective(0, Math.pow(x*x + y - 11.0, 2.0) + Math.pow(x + y*y - 7.0, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, EncodingUtils.newReal(-6, 6));
		solution.setVariable(1, EncodingUtils.newReal(-6, 6));
		return solution;
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		Solution solution1 = newSolution();
		EncodingUtils.setReal(solution1, new double[] { 3.0, 2.0 });
		evaluate(solution1);
		
		Solution solution2 = newSolution();
		EncodingUtils.setReal(solution2, new double[] { -2.805118, 3.131312 });
		evaluate(solution2);
		
		Solution solution3 = newSolution();
		EncodingUtils.setReal(solution3, new double[] { -3.779310, -3.283186 });
		evaluate(solution3);
		
		Solution solution4 = newSolution();
		EncodingUtils.setReal(solution4, new double[] { 3.584428, -1.848126 });
		evaluate(solution4);		
		
		result.addAll(List.of(solution1, solution2, solution3, solution4));
		return result;
	}

}