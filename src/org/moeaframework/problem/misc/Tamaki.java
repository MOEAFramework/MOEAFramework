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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.objective.Maximize;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Tamaki problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected and curved Pareto set
 *   <li>Curved Pareto front
 *   <li>Constrained
 *   <li>Maximization
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: Classifications, Analyses, and New
 *       Innovations."  Air Force Institute of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Tamaki extends AbstractProblem {

	/**
	 * Constructs the Tamaki problem.
	 */
	public Tamaki() {
		super(3, 3, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double z = ((RealVariable)solution.getVariable(2)).getValue();
		double c = Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0) - 1.0;
		
		solution.setObjectiveValue(0, x);
		solution.setObjectiveValue(1, y);
		solution.setObjectiveValue(2, z);
		solution.setConstraintValue(0, c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 3, 1);
		
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		solution.setVariable(2, new RealVariable(0.0, 1.0));
		
		solution.setObjective(0, new Maximize());
		solution.setObjective(1, new Maximize());
		solution.setObjective(2, new Maximize());
		
		solution.setConstraint(0, LessThanOrEqual.to(0.0));
		
		return solution;
	}

}
