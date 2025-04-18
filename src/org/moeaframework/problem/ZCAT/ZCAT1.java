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
package org.moeaframework.problem.ZCAT;

/**
 * The ZCAT1 test problem.
 */
public class ZCAT1 extends ZCAT {
	
	/**
	 * Constructs the ZCAT1 test problem.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public ZCAT1(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	/**
	 * Constructs the ZCAT1 test problem.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param complicatedPS {@code true} if using the complicated Pareto set shape function; {@code false} otherwise
	 */
	public ZCAT1(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F1,
				complicatedPS ? PSShapeFunction.G4 : PSShapeFunction.G0);
	}

}
