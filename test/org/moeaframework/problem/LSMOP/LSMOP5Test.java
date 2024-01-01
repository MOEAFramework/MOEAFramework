/* Copyright 2009-2023 David Hadka
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
package org.moeaframework.problem.LSMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Vector;

public class LSMOP5Test {
	
	@Test
	public void test2() {
		Problem problem = new LSMOP5(2);
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 32.5957 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 24.9239, 13.5047 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				0.0001);
	}
	
	@Test
	public void test3() {
		Problem problem = new LSMOP5(3);
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 6.9666 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 17.1180, 18.7333, 14.3634 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				0.0001);
	}
	
	@Test
	public void test4() {
		Problem problem = new LSMOP5(4);
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 10.4444 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 11.8544, 12.3688, 18.7809, 14.1931 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testProvider() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("LSMOP5_2"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("LSMOP5_2"));
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("LSMOP5_3"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("LSMOP5_3"));
	}

}
