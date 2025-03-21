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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.GreaterThan;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Viennet (4) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected and asymmetric Pareto set
 *   <li>Curved Pareto front
 *   <li>Constrained
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Viennet, R., et al (1996).  "Multicriteria Optimization Using a Genetic Algorithm for Determining a Pareto
 *       Set."  International Journal of Systems Science, 27(2):255-260.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: Classifications, Analyses, and New
 *       Innovations."  Air Force Institute of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Viennet4 extends AbstractProblem {

	/**
	 * Constructs the Viennet (4) problem.
	 */
	public Viennet4() {
		super(2, 3, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = Math.pow(x - 2.0, 2.0) / 2.0 + Math.pow(y + 1.0, 2.0) / 13.0 + 3.0;
		double f2 = Math.pow(x + y - 3.0, 2.0) / 175.0 + Math.pow(2.0*y - x, 2.0) / 17.0 - 13.0;
		double f3 = Math.pow(3.0*x - 2.0*y + 4.0, 2.0) / 8.0 + Math.pow(x - y + 1.0, 2.0) / 27.0 + 15.0;
		
		double c1 = -4.0*x + 4.0 - y;
		double c2 = x + 1.0;
		double c3 = y - x + 2.0;
		
		solution.setObjectiveValue(0, f1);
		solution.setObjectiveValue(1, f2);
		solution.setObjectiveValue(2, f3);
		solution.setConstraintValue(0, c1);
		solution.setConstraintValue(1, c2);
		solution.setConstraintValue(2, c3);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 3, 3);
		
		solution.setVariable(0, new RealVariable(-4.0, 4.0));
		solution.setVariable(1, new RealVariable(-4.0, 4.0));
		
		solution.setConstraint(0, GreaterThan.value(0.0));
		solution.setConstraint(1, GreaterThan.value(0.0));
		solution.setConstraint(2, GreaterThan.value(0.0));
		
		return solution;
	}

}
