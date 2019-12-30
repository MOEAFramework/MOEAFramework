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

public class LisTest {
	
	@Test
	public void test() {
		Problem problem = new Lis();
		
		Assert.assertArrayEquals(new double[] { 0.0, Math.pow(0.5, 1.0/4.0) }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 
				Math.pow(50.0, 1.0/8.0), Math.pow(60.5, 1.0/4.0) }, 
				TestUtils.evaluateAt(problem, -5.0, -5.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 
				Math.pow(200.0, 1.0/8.0), Math.pow(180.5, 1.0/4.0) }, 
				TestUtils.evaluateAt(problem, 10.0, 10.0).getObjectives(),
				Settings.EPS);
	}

}
