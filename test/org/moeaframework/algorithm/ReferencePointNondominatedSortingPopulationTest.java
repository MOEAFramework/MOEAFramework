/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link ReferencePointNondominatedSortingPopulation} class.
 */
public class ReferencePointNondominatedSortingPopulationTest {
	
	@Test
	public void testPointLineDistance() {
		Assert.assertEquals(0.0,
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 0.5, 0.5 }, new double[] { 0.5, 0.5 }),
				Settings.EPS);
		
		Assert.assertEquals(Math.sqrt(2.0)/2.0,
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 0.5, 0.5 }, new double[] { 0.0, 1.0 }),
				Settings.EPS);
		
		Assert.assertEquals(Math.sqrt(2.0)/2.0,
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 0.5, 0.5 }, new double[] { 1.0, 0.0 }),
				Settings.EPS);
		
		Assert.assertEquals(0.0,
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 1.0, 1.0 }, new double[] { 0.5, 0.5 }),
				Settings.EPS);
		
		Assert.assertEquals(0.0,
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 1.0, 1.0, 1.0 }, new double[] { 0.5, 0.5, 0.5 }),
				Settings.EPS);
		
		Assert.assertEquals(Math.sqrt(2.0),
				ReferencePointNondominatedSortingPopulation.pointLineDistance(
						new double[] { 0.0, 0.0, 1.0 }, new double[] { 1.0, 1.0, 1.0 }),
				Settings.EPS);
	}
	
	@Test
	public void testFindSolutionWithMinimumDistance() {
		double[] weight1 = { 0.5, 0.5 };
		double[] weight2 = { 0.0, 1.0 };
		double[] weight3 = { 1.0, 0.0 };
		
		List<Solution> solutions = new ArrayList<Solution>();
		solutions.add(TestUtils.newSolution(0.5, 0.5));
		solutions.add(TestUtils.newSolution(0.25, 0.75));
		solutions.add(TestUtils.newSolution(0.75, 0.25));
		
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.addAll(solutions);
		population.updateIdealPoint();
		population.translateByIdealPoint();
		population.normalizeByIntercepts(new double[] { 1.0, 1.0 });
		
		Assert.assertSame(solutions.get(0),
				population.findSolutionWithMinimumDistance(solutions, weight1));
		
		Assert.assertSame(solutions.get(1),
				population.findSolutionWithMinimumDistance(solutions, weight2));
		
		Assert.assertSame(solutions.get(2),
				population.findSolutionWithMinimumDistance(solutions, weight3));
	}
	
	@Test
	public void testUpdateIdealPoint() {
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.updateIdealPoint();
		
		Assert.assertEquals(0.0, population.idealPoint[0], Settings.EPS);
		Assert.assertEquals(0.5, population.idealPoint[1], Settings.EPS);
	}
	
	@Test
	public void testTranslateByIdealPoint() {
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();

		double[] objectives = (double[])population.get(0).getAttribute(
				ReferencePointNondominatedSortingPopulation.NORMALIZED_OBJECTIVES);
		
		Assert.assertEquals(0.5, objectives[0], Settings.EPS);
		Assert.assertEquals(0.0, objectives[1], Settings.EPS);
	}
	
	@Test
	public void testNormalizeWithIntercepts() {
		double[] intercepts = { 0.5, 2.0 };
		
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();
		population.normalizeByIntercepts(intercepts);
		
		double[] objectives = (double[])population.get(0).getAttribute(
				ReferencePointNondominatedSortingPopulation.NORMALIZED_OBJECTIVES);
		
		Assert.assertEquals(1.0, objectives[0], Settings.EPS);
		Assert.assertEquals(0.0, objectives[1], Settings.EPS);
	}
	
	@Test
	public void testFindExtremePoint() {
		double[] intercepts = { 1.0, 1.0 };
		
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();
		population.normalizeByIntercepts(intercepts);
		
		Assert.assertSame(population.get(0), population.findExtremePoint(0));
		Assert.assertSame(population.get(1), population.findExtremePoint(1));
	}
	
	@Test
	public void testCalculateIntercepts() {
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();
		
		// the points are (0.0, 0.5) and (0.5, 0.0) after translation
		double[] intercepts = population.calculateIntercepts();
		
		Assert.assertEquals(0.5, intercepts[0], Settings.EPS);
		Assert.assertEquals(0.5, intercepts[1], Settings.EPS);
	}
	
	/**
	 * Tests if the truncate method works correctly when it needs to eliminate
	 * a dominated point.
	 */
	@Test
	public void testTruncate1() {
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 4);
		
		Solution s1 = TestUtils.newSolution(0.5, 0.5);
		Solution s2 = TestUtils.newSolution(0.4, 1.5);
		Solution s3 = TestUtils.newSolution(1.0, 1.0);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		
		population.truncate(2);
		
		Assert.assertTrue(population.contains(s1));
		Assert.assertTrue(population.contains(s2));
	}
	
	/**
	 * Tests if the truncate method works correctly when it needs to eliminate
	 * non-dominated solutions by favoring those associated with reference
	 * points.
	 */
	@Test
	public void testTruncate2() {
		ReferencePointNondominatedSortingPopulation population =
				new ReferencePointNondominatedSortingPopulation(2, 1);
		
		Solution s1 = TestUtils.newSolution(0.5, 0.5);
		Solution s2 = TestUtils.newSolution(0.25, 0.75);
		Solution s3 = TestUtils.newSolution(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		
		population.truncate(2);
		
		Assert.assertTrue(population.contains(s2));
		Assert.assertTrue(population.contains(s3));
	}

}
