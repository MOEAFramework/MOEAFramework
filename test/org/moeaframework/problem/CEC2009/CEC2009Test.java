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
package org.moeaframework.problem.CEC2009;

import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class CEC2009Test extends ProblemTest {

	@Test
	public void testUF1() throws Exception {
		test("UF1");
	}

	@Test
	public void testUF2() throws Exception {
		test("UF2");
	}

	@Test
	public void testUF3() throws Exception {
		test("UF3");
	}

	@Test
	public void testUF4() throws Exception {
		test("UF4");
	}

	@Test
	public void testUF5() throws Exception {
		test("UF5");
	}

	@Test
	public void testUF6() throws Exception {
		test("UF6");
	}

	@Test
	public void testUF7() throws Exception {
		test("UF7");
	}

	@Test
	public void testUF8() throws Exception {
		test("UF8");
	}

	@Test
	public void testUF9() throws Exception {
		test("UF9");
	}

	@Test
	public void testUF10() throws Exception {
		test("UF10");
	}

	public void test(String problem) throws Exception {
		jmetal.core.Problem problemA;
		Problem problemB;

		if (problem.equals("UF1")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF1(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF1();
		} else if (problem.equals("UF2")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF2(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF2();
		} else if (problem.equals("UF3")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF3(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF3();
		} else if (problem.equals("UF4")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF4(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF4();
		} else if (problem.equals("UF5")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF5(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF5();
		} else if (problem.equals("UF6")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF6(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF6();
		} else if (problem.equals("UF7")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF7(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF7();
		} else if (problem.equals("UF8")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF8(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF8();
		} else if (problem.equals("UF9")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF9(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF9();
		} else if (problem.equals("UF10")) {
			problemA = new jmetal.problems.cec2009Competition.CEC2009_UF10(
					"Real");
			problemB = new org.moeaframework.problem.CEC2009.UF10();
		} else {
			throw new IllegalArgumentException();
		}

		test(problemA, problemB);
	}

}
