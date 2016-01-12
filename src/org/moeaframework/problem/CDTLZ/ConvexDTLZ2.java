/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.CDTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * The convex DTLZ2 problem.
 */
public class ConvexDTLZ2 extends DTLZ2 {

	/**
	 * Constructs a convex DTLZ2 test problem with the specified number of
	 * variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public ConvexDTLZ2(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	/**
	 * Constructs a convex DTLZ2 test problem with the specified number of
	 * objectives.  This is equivalent to calling
	 * {@code new DTLZ2(numberOfObjectives+9, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public ConvexDTLZ2(int numberOfObjectives) {
		super(numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		for (int i = 0; i < numberOfObjectives-1; i++) {
			solution.setObjective(i, Math.pow(solution.getObjective(i), 4.0));
		}
		
		solution.setObjective(numberOfObjectives-1,
				Math.pow(solution.getObjective(numberOfObjectives-1), 2.0));
	}

	@Override
	public Solution generate() {
		throw new UnsupportedOperationException();
	}

}
