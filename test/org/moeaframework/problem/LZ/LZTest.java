/* Copyright 2009-2020 David Hadka
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

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class LZTest extends ProblemTest {

	@Test
	@Ignore("JMetal 5.9 uses the incorrect number of variables (10 instead of 30)")
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
		org.uma.jmetal.problem.DoubleProblem problemA;
		Problem problemB;

		if (problem.equals("LZ1")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F1();
			problemB = new org.moeaframework.problem.LZ.LZ1();
		} else if (problem.equals("LZ2")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F2();
			problemB = new org.moeaframework.problem.LZ.LZ2();
		} else if (problem.equals("LZ3")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F3();
			problemB = new org.moeaframework.problem.LZ.LZ3();
		} else if (problem.equals("LZ4")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F4();
			problemB = new org.moeaframework.problem.LZ.LZ4();
		} else if (problem.equals("LZ5")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F5();
			problemB = new org.moeaframework.problem.LZ.LZ5();
		} else if (problem.equals("LZ6")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F6();
			problemB = new org.moeaframework.problem.LZ.LZ6();
		} else if (problem.equals("LZ7")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F7();
			problemB = new org.moeaframework.problem.LZ.LZ7();
		} else if (problem.equals("LZ8")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F8();
			problemB = new org.moeaframework.problem.LZ.LZ8();
		} else if (problem.equals("LZ9")) {
			problemA = new org.uma.jmetal.problem.multiobjective.lz09.LZ09F9();
			problemB = new org.moeaframework.problem.LZ.LZ9();
		} else {
			throw new IllegalArgumentException();
		}

		test(problemA, problemB);
	}

}
