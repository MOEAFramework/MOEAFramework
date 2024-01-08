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
package org.moeaframework.problem.CEC2009;

import org.junit.Test;
import org.moeaframework.problem.ProblemTest;

public class CEC2009Test extends ProblemTest {

	@Test
	public void testUF1() {
		assertProblemDefined("UF1", 2);
		testAgainstJMetal("UF1");
	}

	@Test
	public void testUF2() {
		assertProblemDefined("UF2", 2);
		testAgainstJMetal("UF2");
	}

	@Test
	public void testUF3() {
		assertProblemDefined("UF3", 2);
		testAgainstJMetal("UF3");
	}

	@Test
	public void testUF4() {
		assertProblemDefined("UF4", 2);
		testAgainstJMetal("UF4");
	}

	@Test
	public void testUF5() {
		assertProblemDefined("UF5", 2);
		testAgainstJMetal("UF5");
	}

	@Test
	public void testUF6() {
		assertProblemDefined("UF6", 2);
		testAgainstJMetal("UF6");
	}

	@Test
	public void testUF7() {
		assertProblemDefined("UF7", 2);
		testAgainstJMetal("UF7");
	}

	@Test
	public void testUF8() {
		assertProblemDefined("UF8", 3);
		testAgainstJMetal("UF8");
	}

	@Test
	public void testUF9() {
		assertProblemDefined("UF9", 3);
		testAgainstJMetal("UF9");
	}

	@Test
	public void testUF10() {
		assertProblemDefined("UF10", 3);
		testAgainstJMetal("UF10");
	}

}
