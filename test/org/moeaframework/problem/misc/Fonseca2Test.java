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
package org.moeaframework.problem.misc;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class Fonseca2Test extends ProblemTest {

	@Test
	public void testJMetal() {
		test("Fonseca2");
	}
	
	@Test
	public void test2() {
		Problem problem = new Fonseca2(2);
		
		Assert.assertArrayEquals(new double[] { 0.63212, 0.63212 }, // 1 - 1/e 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, 
				TestUtils.evaluateAt(problem, -4.0, -4.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, 
				TestUtils.evaluateAt(problem, 4.0, 4.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.98168 }, 
				TestUtils.evaluateAt(problem,
						1.0 / Math.sqrt(2.0), 1.0 / Math.sqrt(2.0)).getObjectives(),
				0.00001);
	}
	
	@Test
	public void test3() {
		Problem problem = new Fonseca2(3);
		
		Assert.assertArrayEquals(new double[] { 0.63212, 0.63212 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0, 0.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, 
				TestUtils.evaluateAt(problem, -4.0, -4.0, -4.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, 
				TestUtils.evaluateAt(problem, 4.0, 4.0, 4.0).getObjectives(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.98168 }, 
				TestUtils.evaluateAt(problem,
						1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0)).getObjectives(),
				0.00001);
	}

}
