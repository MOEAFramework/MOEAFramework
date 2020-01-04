/* Copyright 2009-2020 David Hadka
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

public class TanakaTest extends ProblemTest {

	@Test
	@Ignore("JMetal 5.9 modifies the constraint values causing a difference")
	public void testJMetal() throws Exception {
		test(new org.uma.jmetal.problem.multiobjective.Tanaka(), new Tanaka());
	}
	
	@Test
	public void test() {
		Problem problem = new Tanaka();
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.1 }, 
				TestUtils.evaluateAt(problem, 0.1, 0.1).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 1.08, 0.0 }, 
				TestUtils.evaluateAt(problem, 0.1, 0.1).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { Math.PI, Math.PI }, 
				TestUtils.evaluateAt(problem, Math.PI, Math.PI).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 13.456 }, 
				TestUtils.evaluateAt(problem, Math.PI, Math.PI).getConstraints(),
				0.001);
	}

}
