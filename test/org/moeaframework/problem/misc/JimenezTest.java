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

public class JimenezTest extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new Jimenez();
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0 },
				evaluateAt(problem, 0.0, 0.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { -100.0, -150.0, 200.0, 75.0 },
				evaluateAt(problem, 0.0, 0.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 0.0, 0.0).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 400.0, 500.0 },
				evaluateAt(problem, 50.0, 50.0).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 150.0, 100.0, -200.0, -425.0 },
				evaluateAt(problem, 50.0, 50.0).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 50.0, 50.0).isFeasible());
	}

}
