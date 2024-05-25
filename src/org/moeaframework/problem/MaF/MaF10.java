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

import org.moeaframework.problem.WFG.WFG1;

/**
 * The MaF10 test problem, which is identical to the {@link WFG1} test problem.  This problem exhibits the following
 * properties:
 * <ul>
 *   <li>Mixed variable separability
 *   <li>Biased
 * </ul>
 */
public class MaF10 extends WFG1 {

	/**
	 * Constructs an MaF10 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF10(int numberOfObjectives) {
		super(numberOfObjectives);
	}

}
