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
package org.moeaframework.problem.misc;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class Viennet3Test extends ProblemTest {

	@Test
	public void testJMetal() {
		testAgainstJMetal("Viennet3");
	}
	
	@Test
	public void test() {
		Problem problem = new Viennet3();
		
		Assert.assertArrayEquals(new double[] { 0.0, 460.0 / 27.0, -0.1 },
				evaluateAt(problem, 0.0, 0.0).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 8.24901, 3275.0 / 216.0, 0.05263 },
				evaluateAt(problem, -3.0, -3.0).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 8.24901, 4571.0 / 216.0, 0.05263 },
				evaluateAt(problem, 3.0, 3.0).getObjectiveValues(),
				0.00001);
	}

}
