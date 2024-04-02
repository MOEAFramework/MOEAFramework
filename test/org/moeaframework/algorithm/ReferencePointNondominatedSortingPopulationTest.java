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
package org.moeaframework.algorithm;

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

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
		
		List<Solution> solutions = List.of(
				MockSolution.of().withObjectives(0.5, 0.5),
				MockSolution.of().withObjectives(0.25, 0.75),
				MockSolution.of().withObjectives(0.75, 0.25));
		
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.addAll(solutions);
		population.updateIdealPoint();
		population.translateByIdealPoint();
		population.normalizeByIntercepts(new double[] { 1.0, 1.0 });
		
		Assert.assertSame(solutions.get(0), population.findSolutionWithMinimumDistance(solutions, weight1));
		Assert.assertSame(solutions.get(1), population.findSolutionWithMinimumDistance(solutions, weight2));
		Assert.assertSame(solutions.get(2), population.findSolutionWithMinimumDistance(solutions, weight3));
	}
	
	@Test
	public void testUpdateIdealPoint() {
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		population.updateIdealPoint();
		
		Assert.assertEquals(0.0, population.idealPoint[0], Settings.EPS);
		Assert.assertEquals(0.5, population.idealPoint[1], Settings.EPS);
	}
	
	@Test
	public void testTranslateByIdealPoint() {
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
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
		
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
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
		
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();
		population.normalizeByIntercepts(intercepts);
		
		Assert.assertSame(population.get(0), population.findExtremePoint(0));
		Assert.assertSame(population.get(1), population.findExtremePoint(1));
	}
	
	@Test
	public void testCalculateIntercepts() {
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		population.updateIdealPoint();
		population.translateByIdealPoint();
		
		// the points are (0.0, 0.5) and (0.5, 0.0) after translation
		double[] intercepts = population.calculateIntercepts();
		
		Assert.assertEquals(0.5, intercepts[0], Settings.EPS);
		Assert.assertEquals(0.5, intercepts[1], Settings.EPS);
	}
	
	@Test
	public void testTruncateDominatedPoint() {
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(4));
		
		Solution[] solutions = new Solution[] {
				MockSolution.of().withObjectives(0.5, 0.5),
				MockSolution.of().withObjectives(0.4, 1.5),
				MockSolution.of().withObjectives(1.0, 1.0) };
		
		population.addAll(solutions);
		population.truncate(2);
		
		Assert.assertEquals(2, population.size());
		Assert.assertContains(population, solutions[0]);
		Assert.assertContains(population, solutions[1]);
	}
	
	@Test
	public void testTruncateNondominatedPoint() {
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(2,
						new NormalBoundaryDivisions(1));
		
		Solution[] solutions = new Solution[] {
				MockSolution.of().withObjectives(0.5, 0.5),
				MockSolution.of().withObjectives(0.25, 0.75),
				MockSolution.of().withObjectives(0.75, 0.25) };
		
		population.addAll(solutions);
		
		population.truncate(2);
		
		Assert.assertEquals(2, population.size());
		Assert.assertContains(population, solutions[1]);
		Assert.assertContains(population, solutions[2]);
	}

}
