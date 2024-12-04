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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class Binh4Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new Binh4();
		
		Assert.assertArrayEquals(new double[] { 1.5, 2.25, 2.625 },
				evaluateAt(problem, 0.0, 0.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 8.75, -5.0 },
				evaluateAt(problem, 0.0, 0.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 0.0, 0.0).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 111.5, -987.75, 10012.625 },
				evaluateAt(problem, -10.0, -10.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { -201.25, 225.0 },
				evaluateAt(problem, -10.0, -10.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, -10.0, -10.0).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 91.5, 992.25, 9992.625 },
				evaluateAt(problem, 10.0, 10.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { -181.25, 165.0 },
				evaluateAt(problem, 10.0, 10.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 10.0, 10.0).isFeasible());
	}

}
