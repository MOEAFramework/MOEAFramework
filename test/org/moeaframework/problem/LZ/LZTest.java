/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.problem.LZ;

import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class LZTest extends ProblemTest {

	@Test
	public void testLZ1() throws Exception {
		test("LZ1");
	}

	@Test
	public void testLZ2() throws Exception {
		test("LZ2");
	}

	@Test
	public void testLZ3() throws Exception {
		test("LZ3");
	}

	@Test
	public void testLZ4() throws Exception {
		test("LZ4");
	}

	@Test
	public void testLZ5() throws Exception {
		test("LZ5");
	}

	@Test
	public void testLZ6() throws Exception {
		test("LZ6");
	}

	@Test
	public void testLZ7() throws Exception {
		test("LZ7");
	}

	@Test
	public void testLZ8() throws Exception {
		test("LZ8");
	}

	@Test
	public void testLZ9() throws Exception {
		test("LZ9");
	}

	public void test(String problem) throws Exception {
		jmetal.core.Problem problemA;
		Problem problemB;

		if (problem.equals("LZ1")) {
			problemA = new jmetal.problems.LZ09.LZ09_F1("Real");
			problemB = new org.moeaframework.problem.LZ.LZ1();
		} else if (problem.equals("LZ2")) {
			problemA = new jmetal.problems.LZ09.LZ09_F2("Real");
			problemB = new org.moeaframework.problem.LZ.LZ2();
		} else if (problem.equals("LZ3")) {
			problemA = new jmetal.problems.LZ09.LZ09_F3("Real");
			problemB = new org.moeaframework.problem.LZ.LZ3();
		} else if (problem.equals("LZ4")) {
			problemA = new jmetal.problems.LZ09.LZ09_F4("Real");
			problemB = new org.moeaframework.problem.LZ.LZ4();
		} else if (problem.equals("LZ5")) {
			problemA = new jmetal.problems.LZ09.LZ09_F5("Real");
			problemB = new org.moeaframework.problem.LZ.LZ5();
		} else if (problem.equals("LZ6")) {
			problemA = new jmetal.problems.LZ09.LZ09_F6("Real");
			problemB = new org.moeaframework.problem.LZ.LZ6();
		} else if (problem.equals("LZ7")) {
			problemA = new jmetal.problems.LZ09.LZ09_F7("Real");
			problemB = new org.moeaframework.problem.LZ.LZ7();
		} else if (problem.equals("LZ8")) {
			problemA = new jmetal.problems.LZ09.LZ09_F8("Real");
			problemB = new org.moeaframework.problem.LZ.LZ8();
		} else if (problem.equals("LZ9")) {
			problemA = new jmetal.problems.LZ09.LZ09_F9("Real");
			problemB = new org.moeaframework.problem.LZ.LZ9();
		} else {
			throw new IllegalArgumentException();
		}

		test(problemA, problemB);
	}

}
