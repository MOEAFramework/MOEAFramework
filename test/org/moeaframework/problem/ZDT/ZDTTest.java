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
package org.moeaframework.problem.ZDT;

import org.junit.Test;
import org.moeaframework.problem.ProblemTest;

/**
 * Tests the ZDT problems.
 */
public class ZDTTest extends ProblemTest {

	@Test
	public void testZDT1() {
		assertProblemDefined("ZDT1", 2);
		testAgainstJMetal("ZDT1");
	}

	@Test
	public void testZDT2() {
		assertProblemDefined("ZDT2", 2);
		testAgainstJMetal("ZDT2");
	}

	@Test
	public void testZDT3() {
		assertProblemDefined("ZDT3", 2);
		testAgainstJMetal("ZDT3");
	}

	@Test
	public void testZDT4() {
		assertProblemDefined("ZDT4", 2);
		testAgainstJMetal("ZDT4");
	}

	@Test
	public void testZDT5() {
		assertProblemDefined("ZDT5", 2);
		testAgainstJMetal("ZDT5");
	}

	@Test
	public void testZDT6() {
		assertProblemDefined("ZDT6", 2);
		testAgainstJMetal("ZDT6");
	}

}
