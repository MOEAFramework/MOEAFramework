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
import org.moeaframework.TestThresholds;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class Viennet4Test extends ProblemTest {

	@Test
	public void testJMetal() {
		testAgainstJMetal("Viennet4");
	}
	
	@Test
	public void test() {
		Problem problem = new Viennet4();
		
		Assert.assertArrayEquals(new double[] { 66.0/13.0, -2266.0/175.0, 460.0/27.0 },
				evaluateAt(problem, 0.0, 0.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 4.0, 1.0, 2.0 },
				evaluateAt(problem, 0.0, 0.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertTrue(evaluateAt(problem, 0.0, 0.0).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 282.0/13.0, -33818.0/2975.0, 406.0/27.0 },
				evaluateAt(problem, -4.0, -4.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 24.0, -3.0, 2.0 },
				evaluateAt(problem, -4.0, -4.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, -4.0, -4.0).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 90.0/13.0, -1418.0/119.0, 622.0/27.0 },
				evaluateAt(problem, 4.0, 4.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { -16.0, 5.0, 2.0 },
				evaluateAt(problem, 4.0, 4.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 4.0, 4.0).isFeasible());
	}

}
