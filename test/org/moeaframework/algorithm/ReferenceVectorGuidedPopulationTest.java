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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;

/**
 * Tests the {@link ReferenceVectorGuidedPopulation} class.
 */
public class ReferenceVectorGuidedPopulationTest {
	
	@Test
	public void testCosine() {
		Assert.assertEquals(Math.sqrt(2.0) / 2.0,
				ReferenceVectorGuidedPopulation.cosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 0.0, 3.0 }),
				Settings.EPS);
		
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
				Settings.EPS);
		
		Assert.assertEquals(0.0,
				ReferenceVectorGuidedPopulation.acosine(
						Vector.normalize(new double[] { 2.0, 2.0 }),
						new double[] { 2.0, 2.0 }),
				1e-7);
	}
	
	@Test
	public void testCalculateIdealPoint() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 4, 2.0);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.calculateIdealPoint();
		
		Assert.assertEquals(0.0, population.idealPoint[0], Settings.EPS);
		Assert.assertEquals(0.5, population.idealPoint[1], Settings.EPS);
	}
	
	@Test
	public void testTranslateByIdealPoint() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 4, 2.0);
		
		population.add(TestUtils.newSolution(0.5, 0.5));
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		population.calculateIdealPoint();
		population.translateByIdealPoint();

		double[] objectives = (double[])population.get(0).getAttribute(
				ReferencePointNondominatedSortingPopulation.NORMALIZED_OBJECTIVES);
		
		Assert.assertEquals(0.5, objectives[0], Settings.EPS);
		Assert.assertEquals(0.0, objectives[1], Settings.EPS);
	}

	@Test
	public void testAssociateToReferencePoint() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 1, 2.0);
		
		Solution s1 = TestUtils.newSolution(0.5, 0.5);
		Solution s2 = TestUtils.newSolution(0.25, 0.75);
		Solution s3 = TestUtils.newSolution(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.calculateIdealPoint();
		population.translateByIdealPoint();
		
		List<List<Solution>> members = population.associateToReferencePoint(population);
		
		Assert.assertTrue(members.get(0).contains(s2));
		Assert.assertTrue(members.get(1).contains(s3));
	}
	
	/**
	 * Tests that the truncate method works correctly with a small scaling
	 * factor.  While s2 and s3 are closer to the weight vectors, s1 will be
	 * retained since it is closer to the origin and a scaling factor of 0.0
	 * favors convergence over diversity.
	 */
	@Test
	public void testTruncate1() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 1, 2.0);
		
		Solution s1 = TestUtils.newSolution(0.5, 0.5);
		Solution s2 = TestUtils.newSolution(0.25, 0.75);
		Solution s3 = TestUtils.newSolution(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.truncate();
		
		Assert.assertTrue(population.size() == 2);
		Assert.assertTrue(population.contains(s1));
		Assert.assertTrue(population.contains(s2) || population.contains(s3));		
	}
	
	/**
	 * Tests that the truncate method works correctly with a large scaling
	 * factor.  With a scaling factor of 1.0, the algorithm prefers diversity
	 * and selects the two solutions closer to the weight vectors, s2 and s3.
	 */
	@Test
	public void testTruncate2() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 1, 2.0);
		
		Solution s1 = TestUtils.newSolution(0.5, 0.5);
		Solution s2 = TestUtils.newSolution(0.25, 0.75);
		Solution s3 = TestUtils.newSolution(0.75, 0.25);
		Solution[] solutions = new Solution[] { s1, s2, s3 };
		
		PRNG.shuffle(solutions);
		
		population.addAll(solutions);
		population.setScalingFactor(1.0);
		population.truncate();
		
		Assert.assertTrue(population.size() == 2);
		Assert.assertTrue(population.contains(s2));
		Assert.assertTrue(population.contains(s3));		
	}
	
	/**
	 * Tests if the {@code adapt} method works correctly by ensuring that
	 * (a) the boundary weights remain on the primary axis (e.g., (0, 1) or
	 * (1, 0)) and (b) intermediate weights appear to be scaled correctly.
	 */
	@Test
	public void testAdapt() {
		ReferenceVectorGuidedPopulation population =
				new ReferenceVectorGuidedPopulation(2, 2, 2.0);
		
		population.add(TestUtils.newSolution(0.4, 0.5));
		population.add(TestUtils.newSolution(0.25, 2.75));

		population.adapt();
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, population.weights.get(0), Settings.EPS);
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, population.weights.get(2), Settings.EPS);
		Assert.assertTrue(population.weights.get(1)[0] <= population.weights.get(1)[1]);
	}

}
