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
package org.moeaframework.problem.WFG;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.ProblemTest;

public class WFGTest extends ProblemTest {

	@Test
	public void testWFG1_2D() {
		test("WFG1", 2);
	}

	@Test
	public void testWFG1_3D() {
		test("WFG1", 3);
	}

	@Test
	public void testWFG2_2D() {
		test("WFG2", 2);
	}

	@Test
	public void testWFG2_3D() {
		test("WFG2", 3);
	}

	@Test
	public void testWFG3_2D() {
		test("WFG3", 2);
	}

	@Test
	public void testWFG3_3D() {
		test("WFG3", 3);
	}

	@Test
	public void testWFG4_2D() {
		test("WFG4", 2);
	}

	@Test
	public void testWFG4_3D() {
		test("WFG4", 3);
	}

	@Test
	public void testWFG5_2D() {
		test("WFG5", 2);
	}

	@Test
	public void testWFG5_3D() {
		test("WFG5", 3);
	}

	@Test
	public void testWFG6_2D() {
		test("WFG6", 2);
	}

	@Test
	public void testWFG6_3D() {
		test("WFG6", 3);
	}

	@Test
	public void testWFG7_2D() {
		test("WFG7", 2);
	}

	@Test
	public void testWFG7_3D() {
		test("WFG7", 3);
	}

	@Test
	public void testWFG8_2D() {
		test("WFG8", 2);
	}

	@Test
	public void testWFG8_3D() {
		test("WFG8", 3);
	}

	@Test
	public void testWFG9_2D() {
		test("WFG9", 2);
	}

	@Test
	public void testWFG9_3D() {
		test("WFG9", 3);
	}

	private void test(String problem, int M) {
		String problemName = problem + "_" + M;
		
		assertProblemDefined(problemName, M);
		testAgainstJMetal(problemName);
		
		testGenerate(problemName);
	}
	
	private void testGenerate(String problemName) {
		WFG problem = (WFG)ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation result = new NondominatedPopulation(DuplicateMode.ALLOW_DUPLICATES);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			result.add(problem.generate());
		}
		
		Assume.assumeFalse("WFG2 is disjoint and can generate dominated solutions", problem instanceof WFG2);
		Assert.assertEquals(TestThresholds.SAMPLES, result.size());
	}

}
