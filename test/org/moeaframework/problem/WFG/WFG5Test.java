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

public class WFG5Test extends WFGTest {
	
	@Test
	public void test() {
		Problem problem = new WFG5(2);
		
		Assert.assertArrayEquals(new double[] { 0.206918, 4.037669 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.206918, 4.037669 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("WFG5_2");
	}
	
	@Test
	public void testJMetal3D() {
		testAgainstJMetal("WFG5_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("WFG5", 2);
		assertProblemDefined("WFG5_2", 2);
		assertProblemDefined("WFG5_3", 3);
	}
	
	@Test
	public void testGenerate() {
		testGenerate("WFG5_2");
		testGenerate("WFG5_3");
	}

}
