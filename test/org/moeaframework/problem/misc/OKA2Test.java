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

public class OKA2Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new OKA2();
		
		Assert.assertArrayEquals(new double[] { 0.0, 3.0/4.0 + Math.pow(5.0, 1.0/3.0) }, 
				evaluateAt(problem, 0.0, 0.0, 0.0).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { Math.PI/2.0, 7.0/16.0 + Math.pow(5.0, 1.0/3.0) }, 
				evaluateAt(problem, Math.PI/2.0, 0.0, 0.0).getObjectiveValues(),
				0.0001);
	}

}
