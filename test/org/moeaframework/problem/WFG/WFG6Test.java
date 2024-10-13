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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;

public class WFG6Test extends WFGTest {
	
	@Test
	public void test() {
		Problem problem = new WFG6(2);
		
		Assert.assertArrayEquals(new double[] { 0.181818, 4.181818 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.181818, 0.181818 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("WFG6_2");
	}
	
	@Test
	public void testJMetal3D() {
		testAgainstJMetal("WFG6_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("WFG6", 2);
		assertProblemDefined("WFG6_2", 2);
		assertProblemDefined("WFG6_3", 3);
	}
	
	@Test
	public void testGenerate() {
		testGenerate("WFG6_2");
		testGenerate("WFG6_3");
	}

}
