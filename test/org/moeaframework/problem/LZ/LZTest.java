/* Copyright 2009-2023 David Hadka
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
import org.moeaframework.problem.ProblemTest;

public class LZTest extends ProblemTest {

	@Test
	@Ignore("JMetal uses 10 variables instead of 30")
	public void testLZ1() {
		test("LZ1");
	}

	@Test
	public void testLZ2() {
		test("LZ2");
	}

	@Test
	public void testLZ3() {
		test("LZ3");
	}

	@Test
	public void testLZ4() {
		test("LZ4");
	}

	@Test
	public void testLZ5() {
		test("LZ5");
	}

	@Test
	public void testLZ6() {
		test("LZ6");
	}

	@Test
	public void testLZ7() {
		test("LZ7");
	}

	@Test
	public void testLZ8() {
		test("LZ8");
	}

	@Test
	public void testLZ9() {
		test("LZ9");
	}

}
