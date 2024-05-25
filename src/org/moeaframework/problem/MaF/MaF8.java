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

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The MaF8 test problem.  This problem exhibits the following properties:
 * <ul>
 *   <li>Linear Pareto front
 *   <li>Degenerate
 * </ul>
 */
public class MaF8 extends AbstractProblem {
	
	private final Polygon polygon;

	/**
	 * Constructs an MaF8 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF8(int numberOfObjectives) {
		super(2, numberOfObjectives);
		polygon = Polygon.createStandardPolygon(numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		Vector2D point = new Vector2D(EncodingUtils.getReal(solution));
		double[] f = new double[numberOfObjectives];
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = polygon.getVertex(i).distance(point);
		}
		
		solution.setObjectives(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-10000, 10000));
		}

		return solution;
	}

}
