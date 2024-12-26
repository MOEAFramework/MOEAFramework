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

import java.util.BitSet;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.mock.MockBinaryProblem;
import org.moeaframework.mock.MockSolution;

public class NondominatedPopulationTest {

	@Test
	public void testAddSimilar() {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0, Settings.EPS / 2.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, Settings.EPS / 2.0, 0.0);

		Assert.assertTrue(population.add(solution1));
		Assert.assertFalse(population.add(solution2));
		Assert.assertEquals(1, population.size());
		Assert.assertTrue(population.contains(solution1));
	}

	@Test
	public void testAdd() {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution1 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 3.0, 2.0);
		Solution solution3 = MockSolution.of().withObjectives(2.0, 1.0, 3.0);
		Solution solution4 = MockSolution.of().withObjectives(1.0, 1.0, 3.0);
		Solution solution5 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		Solution solution6 = MockSolution.of().withObjectives(1.0, 1.0, 3.0);

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
	
	@Test(expected = UnsupportedOperationException.class)
	public void testReplace() {
		NondominatedPopulation population = new NondominatedPopulation();

		Solution solution1 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 3.0, 2.0);

		population.add(solution1);
		population.replace(0, solution2);
	}

	@Test
	public void testNoDuplicates() {
		NondominatedPopulation population = new NondominatedPopulation(DuplicateMode.NO_DUPLICATE_OBJECTIVES);
		
		try (MockBinaryProblem problem = new MockBinaryProblem()) {
			Solution solution1 = MockSolution.of(problem).withObjectives(0.5);
			BinaryVariable.setBitSet(solution1.getVariable(0), new BitSet(10));
			
			Solution solution2 = solution1.copy();
			
			Solution solution3 = solution1.copy();
			BitSet bits = new BitSet(10);
			bits.set(3);
			BinaryVariable.setBitSet(solution3.getVariable(0), bits);
			
			Assert.assertTrue(population.add(solution1));
			Assert.assertFalse(population.add(solution2));
			Assert.assertFalse(population.add(solution3));
		}
	}
	
	@Test
	public void testAllowDuplicates() {
		NondominatedPopulation population = new NondominatedPopulation(DuplicateMode.ALLOW_DUPLICATES);
		
		try (MockBinaryProblem problem = new MockBinaryProblem()) {
			Solution solution1 = MockSolution.of(problem).withObjectives(0.5);
			BinaryVariable.setBitSet(solution1.getVariable(0), new BitSet(10));
			
			Solution solution2 = solution1.copy();
			
			Solution solution3 = solution1.copy();
			BitSet bits = new BitSet(10);
			bits.set(3);
			BinaryVariable.setBitSet(solution3.getVariable(0), bits);
			
			Assert.assertTrue(population.add(solution1));
			Assert.assertTrue(population.add(solution2));
			Assert.assertTrue(population.add(solution3));
		}
	}
	
	@Test
	public void testAllowDuplicateObjectives() {
		NondominatedPopulation population = new NondominatedPopulation(DuplicateMode.ALLOW_DUPLICATE_OBJECTIVES);
		
		try (MockBinaryProblem problem = new MockBinaryProblem()) {
			Solution solution1 = MockSolution.of(problem).withObjectives(0.5);
			BinaryVariable.setBitSet(solution1.getVariable(0), new BitSet(10));
			
			Solution solution2 = solution1.copy();
			
			Solution solution3 = solution1.copy();
			BitSet bits = new BitSet(10);
			bits.set(3);
			BinaryVariable.setBitSet(solution3.getVariable(0), bits);
			
			Assert.assertTrue(population.add(solution1));
			Assert.assertFalse(population.add(solution2));
			Assert.assertTrue(population.add(solution3));
		}
	}
	
	@Test
	public void testCopy() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		NondominatedPopulation copy = population.copy();
		
		Assert.assertNotSame(population, copy);
		Assert.assertSame(population.getComparator(), copy.getComparator());
		Assert.assertEquals(population, copy, true);
	}

}
