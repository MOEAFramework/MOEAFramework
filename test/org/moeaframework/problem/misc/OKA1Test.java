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
import org.moeaframework.problem.ProblemTest;

public class OKA1Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new OKA1();
		
		Assert.assertArrayEquals(new double[] { 2.221441469079183, 3.7889583370899755 }, 
				evaluateAt(problem, Math.PI, Math.PI).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.9208936072384664, 4.049324370013574 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 5.86229169994112, 2.532902255226027 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
	}

}
