/* Copyright 2009-2019 David Hadka
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
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.problem.ProblemTest;

public class Viennet4Test extends ProblemTest {

	@Test
	@Ignore("discrepancy between this implementation and jMetal 3.1")
	public void testJMetal() throws Exception {
		test(new jmetal.problems.Viennet4("Real"), new Viennet4());
	}
	
	@Test
	public void test() {
		Problem problem = new Viennet4();
		
		Assert.assertArrayEquals(new double[] { 66.0/13.0, -2266.0/175.0, 460.0/27.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 282.0/13.0, -33818.0/2975.0, 406.0/27.0 }, 
				TestUtils.evaluateAt(problem, -4.0, -4.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, -3.0, 0.0 }, 
				TestUtils.evaluateAt(problem, -4.0, -4.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 90.0/13.0, -1418.0/119.0, 622.0/27.0 }, 
				TestUtils.evaluateAt(problem, 4.0, 4.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { -16.0, 0.0, 0.0 }, 
				TestUtils.evaluateAt(problem, 4.0, 4.0).getConstraints(),
				Settings.EPS);
	}

}
