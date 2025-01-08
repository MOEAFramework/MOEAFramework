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
package org.moeaframework.core.population;

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.NormalizedObjectives;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

public class ReferenceVectorGuidedPopulationTest {
	
	@Test
	public void testCosine() {
		Assert.assertEquals(Math.sqrt(2.0) / 2.0,
				ReferenceVectorGuidedPopulation.cosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 0.0, 3.0 }),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(1.0,
				ReferenceVectorGuidedPopulation.cosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 2.0, 2.0 }),
				1e-7);
	}
	
	@Test
	public void testACosine() {
		Assert.assertEquals(Math.PI / 4.0,
				ReferenceVectorGuidedPopulation.acosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 0.0, 3.0 }),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(0.0,
				ReferenceVectorGuidedPopulation.acosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 2.0, 2.0 }),
				1e-7);
	}
	
	@Test
	public void testCalculateIdealPoint() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(4), 2.0);
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		population.calculateIdealPoint();
		
		Assert.assertEquals(0.0, population.idealPoint[0], TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.5, population.idealPoint[1], TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testTranslateByIdealPoint() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(4), 2.0);
		
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		population.calculateIdealPoint();
		population.translateByIdealPoint();

		double[] objectives = NormalizedObjectives.getAttribute(population.get(0));
		
		Assert.assertEquals(0.5, objectives[0], TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, objectives[1], TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testAssociateToReferencePoint() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(1), 2.0);
		
		Solution s1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution s2 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution s3 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.calculateIdealPoint();
		population.translateByIdealPoint();
		
		List<List<Solution>> members = population.associateToReferencePoint(population);
		
		Assert.assertContains(members.get(0), s2);
		Assert.assertContains(members.get(1), s3);
	}
	
	/**
	 * While s2 and s3 are closer to the weight vectors, s1 will be retained since it is closer to the origin and a
	 * scaling factor of 0.0 favors convergence over diversity.
	 */
	@Test
	public void testTruncateWithSmallScalingFactor() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(1), 2.0);
		
		Solution s1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution s2 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution s3 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.truncate();
		
		Assert.assertSize(2, population);
		Assert.assertContains(population, s1);
		Assert.any(() -> Assert.assertContains(population, s2), () -> Assert.assertContains(population, s3));
	}
	
	/**
	 * With a scaling factor of 1.0, the algorithm prefers diversity and selects the two solutions closer to the
	 * weight vectors, s2 and s3.
	 */
	@Test
	public void testTruncateWithLargeScalingFactor() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(1), 2.0);
		
		Solution s1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution s2 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution s3 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.setScalingFactor(1.0);
		population.truncate();
		
		Assert.assertSize(2, population);
		Assert.assertContains(population, s2);
		Assert.assertContains(population, s3);
	}
	
	/**
	 * Tests if the {@code adapt} method works correctly by ensuring that (a) the boundary weights remain on the
	 * primary axis (e.g., (0, 1) or (1, 0)) and (b) intermediate weights appear to be scaled correctly.
	 */
	@Test
	public void testAdapt() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(2), 2.0);
		
		population.add(MockSolution.of().withObjectives(0.4, 0.5));
		population.add(MockSolution.of().withObjectives(0.25, 2.75));

		population.adapt();
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, population.weights[0], TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, population.weights[2], TestThresholds.HIGH_PRECISION);
		Assert.assertLessThanOrEqual(population.weights[1][0], population.weights[1][1]);
	}
	
	@Test
	public void testCopy() {
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				2, new NormalBoundaryDivisions(2), 2.0);
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		ReferenceVectorGuidedPopulation copy = population.copy();
		
		Assert.assertNotSame(population, copy);
		Assert.assertEquals(population.getDivisions(), copy.getDivisions());
		Assert.assertEquals(population.getAlpha(), copy.getAlpha());
		Assert.assertEquals(population, copy, true);
	}

}
