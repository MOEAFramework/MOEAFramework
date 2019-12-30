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
package org.moeaframework.core.indicator;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link IndicatorUtils} class.
 */
public class IndicatorUtilsTest {

	/**
	 * Tests if Manhattan distances are calculated correctly.
	 */
	@Test
	public void testManhattanDistance() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		Assert.assertEquals(2.0, IndicatorUtils.manhattanDistance(problem, 
				TestUtils.newSolution(0.0, 1.0), 
				TestUtils.newSolution(1.0, 0.0)), Settings.EPS);
		
		Assert.assertEquals(1.0, IndicatorUtils.manhattanDistance(problem, 
				TestUtils.newSolution(0.0, 0.0), 
				TestUtils.newSolution(1.0, 0.0)), Settings.EPS);
		
		Assert.assertEquals(0.0, IndicatorUtils.manhattanDistance(problem, 
				TestUtils.newSolution(0.0, 0.0), 
				TestUtils.newSolution(0.0, 0.0)), Settings.EPS);
	}
	
	/**
	 * Tests if Euclidean distances are calculated correctly.
	 */
	@Test
	public void testEuclideanDistance() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		Assert.assertEquals(Math.sqrt(2.0), IndicatorUtils.euclideanDistance(
				problem, TestUtils.newSolution(0.0, 1.0), 
				TestUtils.newSolution(1.0, 0.0)), Settings.EPS);
		
		Assert.assertEquals(1.0, IndicatorUtils.euclideanDistance(problem, 
				TestUtils.newSolution(0.0, 0.0), 
				TestUtils.newSolution(1.0, 0.0)), Settings.EPS);
		
		Assert.assertEquals(0.0, IndicatorUtils.euclideanDistance(problem, 
				TestUtils.newSolution(0.0, 0.0), 
				TestUtils.newSolution(0.0, 0.0)), Settings.EPS);
	}
	
	/**
	 * Tests the the distance to the nearest neighboring solution is calculated
	 * correctly.
	 */
	@Test
	public void testDistanceToNearestSolution() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(1.0, 0.0));
		
		Assert.assertEquals(0.0, IndicatorUtils.distanceToNearestSolution(
				problem, TestUtils.newSolution(0.0, 1.0), population),
				Settings.EPS);
		
		Assert.assertEquals(0.0, IndicatorUtils.distanceToNearestSolution(
				problem, TestUtils.newSolution(1.0, 0.0), population),
				Settings.EPS);
		
		Assert.assertEquals(Math.sqrt(0.5), 
				IndicatorUtils.distanceToNearestSolution(problem, 
						TestUtils.newSolution(0.5, 0.5), population),
				Settings.EPS);
		
		Assert.assertEquals(Math.sqrt(0.125), 
				IndicatorUtils.distanceToNearestSolution(problem, 
						TestUtils.newSolution(0.25, 0.75), population),
				Settings.EPS);
	}
	
}
