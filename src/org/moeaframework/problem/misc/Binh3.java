/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Binh (3) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Binh, T. T (1999).  "A Multiobjective Evolutionary Algorithm: The
 *       Study Cases."  Technical Report, Institute for Automation and
 *       Communication, Barleben, Germany.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh3 extends AbstractProblem {

	/**
	 * Constructs the Binh (3) problem.
	 */
	public Binh3() {
		super(2, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		
		solution.setObjective(0, x - 1e6);
		solution.setObjective(1, y - 2e-6);
		solution.setObjective(2, x*y - 2.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 3);
		
		solution.setVariable(0, new RealVariable(1e-6, 1e6));
		solution.setVariable(1, new RealVariable(1e-6, 1e6));
		
		return solution;
	}

}
