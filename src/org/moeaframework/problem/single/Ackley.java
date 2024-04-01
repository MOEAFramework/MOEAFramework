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

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * The single-objective Ackley problem with an optimum at {@code x = (0, 0)} with {@code f(x) = 0}.
 * <p>
 * References:
 * <ol>
 *   <li>Ackley, D. H. (1987) "A Connectionist Machine for Genetic Hillclimbing", Kluwer Academic Publishers, Boston MA.
 * </ol>
 */
public class Ackley extends AbstractProblem {
	
	/**
	 * Constructs a new instance of the Ackley problem.
	 */
	public Ackley() {
		super(2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		solution.setObjective(0, -20.0 * Math.exp(-0.2 * Math.sqrt(0.5 * (x*x + y*y))) -
				Math.exp(0.5 * (Math.cos(2.0 * Math.PI * x) + Math.cos(2.0 * Math.PI * y))) +
				Math.E + 20.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, EncodingUtils.newReal(-10, 10));
		solution.setVariable(1, EncodingUtils.newReal(-10, 10));
		return solution;
	}

}