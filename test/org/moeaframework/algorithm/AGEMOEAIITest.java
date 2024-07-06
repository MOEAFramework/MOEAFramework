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
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.AGEMOEAII.AGEMOEAIIPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.mock.MockSolution;

@RunWith(CIRunner.class)
@Retryable
public class AGEMOEAIITest extends JMetalAlgorithmTest {
	
	public AGEMOEAIITest() {
		super("AGE-MOEA-II");
	}
	
	@Test
	public void testGetExtremePoints() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of().withObjectives(0.75, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.0, 0.75);
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3));

		List<Solution> result = List.of(population.getExtremePoints(population));

		Assert.assertFalse(result.contains(solution1));
		Assert.assertTrue(result.contains(solution2));
		Assert.assertTrue(result.contains(solution3));
	}
	
	@Test
	public void testCalculateIntercepts1() {
		Solution solution1 = MockSolution.of().withObjectives(0.9, 0.1);
		Solution solution2 = MockSolution.of().withObjectives(0.1, 0.9);
		
		Solution[] extremePoints = new Solution[] { solution1, solution2 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2));

		double[] intercepts = population.calculateIntercepts(extremePoints);

		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, intercepts, TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testCalculateIntercepts2() {
		Solution solution1 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution2 = MockSolution.of().withObjectives(0.25, 0.75);
		
		Solution[] extremePoints = new Solution[] { solution1, solution2 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2));

		double[] intercepts = population.calculateIntercepts(extremePoints);

		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, intercepts, TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testCalculateInterceptsDegenerate() {
		Solution solution1 = MockSolution.of().withObjectives(0.9, 0.1);
		Solution solution2 = MockSolution.of().withObjectives(0.9, 0.1);
		
		Solution[] extremePoints = new Solution[] { solution1, solution2 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2));

		double[] intercepts = population.calculateIntercepts(extremePoints);

		Assert.assertArrayEquals(new double[] { 0.9, 0.1 }, intercepts, TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testFitGeometryPaper() {
		// Based on Figure 1 in the 2022 paper
		Solution solution1 = MockSolution.of().withObjectives(Math.cos((Math.PI - 4.0) / 2.0), Math.sin((Math.PI - 4.0) / 2.0));
		Solution solution2 = MockSolution.of().withObjectives(Math.cos(Math.PI / 8.0), Math.sin(Math.PI / 8.0));
		Solution solution3 = MockSolution.of().withObjectives(Math.cos(Math.PI / 16.0), Math.sin(Math.PI / 16.0));
		
		Solution[] extremePoints = new Solution[] { solution1, solution2 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3));

		population.normalize(population, new double[] { 0.0, 0.0 }, new double[] { 1.0, 1.0 });
		double p = population.fitGeometry(population, extremePoints);

		Assert.assertEquals(2.0, p, 0.000001);
	}
	
	@Test
	public void testFitGeometryLinear() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.5, 0.5);
		
		Solution[] extremePoints = new Solution[] { solution1, solution2 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3));

		population.normalize(population, new double[] { 0.0, 0.0 }, new double[] { 1.0, 1.0 });
		double p = population.fitGeometry(population, extremePoints);

		Assert.assertEquals(1.0, p, 0.000001);
	}
	
	@Test
	public void testTruncationKeepsExtremePoints() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.0, 1.0);
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3));

		population.truncate(2);

		Assert.assertSize(2, population);
		Assert.assertFalse(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
	}
	
	@Test
	public void testTruncationKeepsHigherRanks() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(2.0, 2.0);
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3));

		population.truncate(2);

		Assert.assertSize(2, population);
		Assert.assertTrue(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertFalse(population.contains(solution3));
	}
	
	@Test
	public void testTruncationKeepsCentralPointConvex() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution4 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution solution5 = MockSolution.of().withObjectives(0.35, 0.35);

		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3, solution4, solution5));

		population.truncate(3);

		Assert.assertSize(3, population);
		Assert.assertTrue(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertFalse(population.contains(solution3));
		Assert.assertFalse(population.contains(solution4));
		Assert.assertTrue(population.contains(solution5));
	}
	
	@Test
	public void testTruncationKeepsCentralPointConcave() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution4 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution solution5 = MockSolution.of().withObjectives(0.65, 0.65);

		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3, solution4, solution5));

		population.truncate(3);

		Assert.assertSize(3, population);
		Assert.assertTrue(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertFalse(population.contains(solution3));
		Assert.assertFalse(population.contains(solution4));
		Assert.assertTrue(population.contains(solution5));
	}
	
	@Test
	public void testTruncateAssignsFitness() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution4 = MockSolution.of().withObjectives(0.6, 0.75);
		Solution solution5 = MockSolution.of().withObjectives(0.75, 0.6);

		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		population.addAll(List.of(solution1, solution2, solution3, solution4, solution5));

		population.truncate(5);

		Assert.assertSize(5, population);
		
		for (Solution solution : population) {
			Assert.assertTrue(Fitness.hasAttribute(solution));
		}
	}
	
	@Test
	public void testMinkowskiDistance() {
		double[] point1 = new double[] { 0.0, 1.0 };
		double[] point2 = new double[] { 1.0, 0.0 };
		
		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
		
		Assert.assertEquals(2.0, population.minkowskiDistance(point1, point2, 1.0));
		Assert.assertEquals(Math.sqrt(2.0), population.minkowskiDistance(point1, point2, 2.0));
		Assert.assertEquals(4.0, population.minkowskiDistance(point1, point2, 0.5));
	}
	
//	@Test
//	public void testGetPairwiseDistances() {
//		// Section 3.2.2 in the 2022 paper shows this case
//		Solution solution1 = MockSolution.of().withObjectives(1.0, 0.0);
//		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
//		
//		AGEMOEAIIPopulation population = new AGEMOEAIIPopulation(2);
//		population.addAll(List.of(solution1, solution2));
//		
//		population.normalize(population, new double[] { 0.0, 0.0 }, new double[] { 1.0, 1.0 });
//		DistanceMap<Solution> distances = population.getPairwiseDistances(population, 2.0);
//
//		Assert.assertEquals(1.5306, distances.get(solution1, solution2), 0.0005);
//	}

}
