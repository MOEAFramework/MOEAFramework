/* Copyright 2009-2025 David Hadka
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
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class QuagliarellaTest extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new Quagliarella(1);
		
		Assert.assertArrayEquals(new double[] { 0.0, 4.716 },
				evaluateAt(problem, 0.0).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 5.378, 7.817 },
				evaluateAt(problem, -5.12).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 5.378, 5.513 },
				evaluateAt(problem, 5.12).getObjectiveValues(),
				0.001);
	}

}
