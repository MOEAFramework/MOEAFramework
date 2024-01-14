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
package org.moeaframework.problem.ZCAT;

import static org.moeaframework.problem.ZCAT.PFShapeFunction.valueBetween;

/**
 * The ZCAT20 test problem.
 */
public class ZCAT20 extends ZCAT {
	
	/**
	 * Constructs the ZCAT20 test problem.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public ZCAT20(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	/**
	 * Constructs the ZCAT20 test problem.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param complicatedPS {@code true} if using the complicated Pareto set shape function; {@code false} otherwise
	 */
	public ZCAT20(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F20,
				complicatedPS ? PSShapeFunction.G3 : PSShapeFunction.G0);
	}
	
	@Override
	public int getDimension(double[] y) {
		return valueBetween(y[0], 0.1, 0.4) || valueBetween(y[0], 0.6, 0.9) ? 1 : numberOfObjectives - 1;
	}

}