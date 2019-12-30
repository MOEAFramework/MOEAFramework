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
package org.moeaframework.core;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.MockBinaryProblem;

/**
 * Tests the {@link NondominatedPopulation} class.
 */
public class NondominatedPopulationTest {

	/**
	 * Tests the {@code distance} method to ensure it computes the Euclidean
	 * distance between solutions correctly.
	 */
	@Test
	public void testDistance() {
		Solution s1 = new Solution(new double[] { 0.0, 1.0, 0.0 });
		Solution s2 = new Solution(new double[] { 0.0, 0.0, -1.0 });

		Assert.assertEquals(Math.sqrt(2.0),
				NondominatedPopulation.distance(s1, s2),
				Settings.EPS);
		Assert.assertEquals(Math.sqrt(2.0),
				NondominatedPopulation.distance(s2, s1),
				Settings.EPS);
		Assert.assertEquals(0.0,
				NondominatedPopulation.distance(s1, s1),
				Settings.EPS);
		Assert.assertEquals(0.0,
				NondominatedPopulation.distance(s2, s2),
				Settings.EPS);
	}

	/**
	 * Tests that a {@code NonDominatedPopulation} rejects adding nearly
	 * identical solutions.
	 */
	@Test
	public void testAddSimilar() {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution1 = new Solution(new double[] { 0.0, 0.0,
				Settings.EPS / 2.0 });
		Solution solution2 = new Solution(new double[] { 0.0,
				Settings.EPS / 2.0, 0.0 });

		Assert.assertTrue(population.add(solution1));
		Assert.assertFalse(population.add(solution2));
		Assert.assertEquals(1, population.size());
		Assert.assertTrue(population.contains(solution1));
	}

	/**
	 * Tests that a {@code NonDominatedPopulation} maintains a non-dominated
	 * population.
	 */
	@Test
	public void testAdd() {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution1 = new Solution(new double[] { 1.0, 2.0, 3.0 });
		Solution solution2 = new Solution(new double[] { 1.0, 3.0, 2.0 });
		Solution solution3 = new Solution(new double[] { 2.0, 1.0, 3.0 });
		Solution solution4 = new Solution(new double[] { 1.0, 1.0, 3.0 });
		Solution solution5 = new Solution(new double[] { 1.0, 2.0, 3.0 });
		Solution solution6 = new Solution(new double[] { 1.0, 1.0, 3.0 });

		Assert.assertTrue(population.add(solution1));
		Assert.assertTrue(population.add(solution2));
		Assert.assertEquals(2, population.size());
		Assert.assertTrue(population.add(solution3));
		Assert.assertEquals(3, population.size());
		Assert.assertTrue(population.add(solution4));
		Assert.assertEquals(2, population.size());
		Assert.assertFalse(population.add(solution5));
		Assert.assertEquals(2, population.size());
		Assert.assertFalse(population.add(solution6));
		Assert.assertEquals(2, population.size());
		Assert.assertTrue(population.contains(solution2));
		Assert.assertTrue(population.contains(solution4));
	}
	
	/**
	 * This is similar to {@link #testAddSimilar()} but tests if a solution
	 * with different variables is also rejected.
	 */
	@Test
	public void testNoDuplicates() {
		NondominatedPopulation population = new NondominatedPopulation(
				DuplicateMode.NO_DUPLICATE_OBJECTIVES);
		
		MockBinaryProblem problem = new MockBinaryProblem();
		
		Solution solution1 = problem.newSolution();
		EncodingUtils.setBitSet(solution1.getVariable(0), new BitSet(10));
		solution1.setObjectives(new double[] { 0.5 });
		
		Solution solution2 = solution1.copy();
		
		Solution solution3 = solution1.copy();
		BitSet bits = new BitSet(10);
		bits.set(3);
		EncodingUtils.setBitSet(solution3.getVariable(0), bits);
		
		Assert.assertTrue(population.add(solution1));
		Assert.assertFalse(population.add(solution2));
		Assert.assertFalse(population.add(solution3));
	}
	
	@Test
	public void testAllowDuplicates() {
		NondominatedPopulation population = new NondominatedPopulation(
				DuplicateMode.ALLOW_DUPLICATES);
		
		MockBinaryProblem problem = new MockBinaryProblem();
		
		Solution solution1 = problem.newSolution();
		EncodingUtils.setBitSet(solution1.getVariable(0), new BitSet(10));
		solution1.setObjectives(new double[] { 0.5 });
		
		Solution solution2 = solution1.copy();
		
		Solution solution3 = solution1.copy();
		BitSet bits = new BitSet(10);
		bits.set(3);
		EncodingUtils.setBitSet(solution3.getVariable(0), bits);
		
		Assert.assertTrue(population.add(solution1));
		Assert.assertTrue(population.add(solution2));
		Assert.assertTrue(population.add(solution3));
	}
	
	@Test
	public void testAllowDuplicateObjectives() {
		NondominatedPopulation population = new NondominatedPopulation(
				DuplicateMode.ALLOW_DUPLICATE_OBJECTIVES);
		
		MockBinaryProblem problem = new MockBinaryProblem();
		
		Solution solution1 = problem.newSolution();
		EncodingUtils.setBitSet(solution1.getVariable(0), new BitSet(10));
		solution1.setObjectives(new double[] { 0.5 });
		
		Solution solution2 = solution1.copy();
		
		Solution solution3 = solution1.copy();
		BitSet bits = new BitSet(10);
		bits.set(3);
		EncodingUtils.setBitSet(solution3.getVariable(0), bits);
		
		Assert.assertTrue(population.add(solution1));
		Assert.assertFalse(population.add(solution2));
		Assert.assertTrue(population.add(solution3));
	}

}
