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
package org.moeaframework.problem.DTLZ;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.ProblemTest;

public class DTLZ7Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new DTLZ7(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 6.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0, 31.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ7_2");
	}

	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ7_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ7_2", 2);
		assertProblemDefined("DTLZ7_3", 3);
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("DTLZ7_2", ProblemFactory.getInstance().getProblem("DTLZ7").getName());
	}
	
	@Test
	public void testEpsilons() {
		assertEpsilonsDefined("DTLZ7_2");
		assertEpsilonsDefined("DTLZ7_3");
	}

}
