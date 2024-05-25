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

import org.junit.Test;
import org.moeaframework.problem.ProblemTest;

public class MaF10Test extends ProblemTest {
	
	// This problem is identical to WFG1, so no need for specific tests.
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF10_2", 2, false);
		assertProblemDefined("MaF10_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal() {
		testAgainstJMetal("MaF10_3");
	}

}
