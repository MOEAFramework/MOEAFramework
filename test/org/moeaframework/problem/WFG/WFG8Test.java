/* Copyright 2009-2025 David Hadka
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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;

public class WFG8Test extends WFGTest {
	
	@Test
	public void test() {
		Problem problem = new WFG8(2);
		
		Assert.assertArrayEquals(new double[] { 1.0, 5.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.0, 1.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("WFG8_2");
	}
	
	@Test
	public void testJMetal3D() {
		testAgainstJMetal("WFG8_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("WFG8", 2);
		assertProblemDefined("WFG8_2", 2);
		assertProblemDefined("WFG8_3", 3);
	}
	
	@Test
	public void testGenerate() {
		testGenerate("WFG8_2");
		testGenerate("WFG8_3");
	}

}
