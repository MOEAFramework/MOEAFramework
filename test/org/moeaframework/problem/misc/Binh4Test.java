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
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;

public class Binh4Test {
	
	@Test
	public void test() {
		Problem problem = new Binh4();
		
		Assert.assertArrayEquals(new double[] { 1.5, 2.25, 2.625 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 8.75, 0.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 111.5, -987.75, 10012.625 }, 
				TestUtils.evaluateAt(problem, -10.0, -10.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 225.0 }, 
				TestUtils.evaluateAt(problem, -10.0, -10.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 91.5, 992.25, 9992.625 }, 
				TestUtils.evaluateAt(problem, 10.0, 10.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 165.0 }, 
				TestUtils.evaluateAt(problem, 10.0, 10.0).getConstraints(),
				Settings.EPS);
	}

}
