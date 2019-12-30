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

public class Binh3Test {
	
	@Test
	public void test() {
		Problem problem = new Binh3();
		
		Assert.assertArrayEquals(new double[] { 1e-6-1e6, -1e-6, 1e-12-2.0 }, 
				TestUtils.evaluateAt(problem, 1e-6, 1e-6).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1e6-2e-6, 1e12-2.0 }, 
				TestUtils.evaluateAt(problem, 1e6, 1e6).getObjectives(),
				Settings.EPS);
	}

}
