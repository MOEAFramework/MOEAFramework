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

public class Osyczka2Test extends ProblemTest {

	@Test
	@Ignore("discrepancy between this implementation and jMetal 3.1")
	public void testJMetal() throws Exception {
		test(new jmetal.problems.Osyczka2("Real"), new Osyczka2());
	}
	
	@Test
	public void test() {
		Problem problem = new Osyczka2();
		
		Assert.assertArrayEquals(new double[] { -120.0, 2.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { -2.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { -1700.0, 386.0 }, 
				TestUtils.evaluateAt(problem, 10.0, 10.0, 5.0, 6.0, 5.0, 10.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, -14.0, 0.0, 0.0, -6.0, 0.0 }, 
				TestUtils.evaluateAt(problem, 10.0, 10.0, 5.0, 6.0, 5.0, 10.0).getConstraints(),
				Settings.EPS);
	}

}
