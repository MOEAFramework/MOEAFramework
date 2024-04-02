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
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.problem.ProblemTest;

public class TanakaTest extends ProblemTest {

	@Test
	public void testJMetal() {
		testAgainstJMetal("Tanaka", false);
	}
	
	@Test
	public void test() {
		Problem problem = new Tanaka();
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.1 }, 
				evaluateAt(problem, 0.1, 0.1).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 1.08, 0.0 }, 
				evaluateAt(problem, 0.1, 0.1).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { Math.PI, Math.PI }, 
				evaluateAt(problem, Math.PI, Math.PI).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 13.456 }, 
				evaluateAt(problem, Math.PI, Math.PI).getConstraints(),
				0.001);
	}

}
