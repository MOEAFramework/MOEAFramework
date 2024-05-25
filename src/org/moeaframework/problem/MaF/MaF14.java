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
package org.moeaframework.problem.MaF;

import org.moeaframework.problem.LSMOP.CorrelationMatrix;
import org.moeaframework.problem.LSMOP.LSMOP;
import org.moeaframework.problem.LSMOP.LSMOP3;
import org.moeaframework.problem.LSMOP.LinkageFunction;
import org.moeaframework.problem.LSMOP.ParetoFrontGeometry;
import org.moeaframework.problem.LSMOP.ShapeFunction;

/**
 * The MaF14 test problem, which is identical to the {@link LSMOP3} test problem with {@code N_k=2} and {@code N_ns=20}.
 * This problem exhibits the following properties:
 * <ul>
 *   <li>Linear Pareto front
 *   <li>Partially separable decision variables
 *   <li>Non-uniform correlations between decision variables and objectives
 * </ul>
 */
public class MaF14 extends LSMOP {

	/**
	 * Constructs an MaF14 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF14(int numberOfObjectives) {
		super(numberOfObjectives, 2, 20 * numberOfObjectives, ShapeFunction.Rastrigin, ShapeFunction.Rosenbrock,
				LinkageFunction.Linear, CorrelationMatrix.Separable, ParetoFrontGeometry.Linear);
	}

}
