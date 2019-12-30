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
package org.moeaframework.core.operator.subset;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Subset;

/**
 * Tests the {@link SSX} class.
 */
public class SSXTest {

	/**
	 * Tests if the SSX crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new SSX(1.0));
	}

	/**
	 * Tests if the {@link SSX#evolve} method produces valid subset for fixed-size sets.
	 */
	@Test
	public void testEvolveFixedSize() {
		DescriptiveStatistics stats1 = new DescriptiveStatistics();
		DescriptiveStatistics stats2 = new DescriptiveStatistics();
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int k = PRNG.nextInt(0, n);
			Subset s1 = new Subset(k, n);
			Subset s2 = new Subset(k, n);
			
			s1.randomize();
			s2.randomize();
			
			Subset s1copy = s1.copy();
			Subset s2copy = s2.copy();
			
			SSX.evolve(s1copy, s2copy);

			s1copy.validate();
			s2copy.validate();
			
			countSwapped(s1, s2, s1copy, s2copy, stats1, stats2);
		}
		
		Assert.assertEquals(0.5, stats1.getMean(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.5, stats1.getMean(), TestThresholds.STATISTICS_EPS);
	}
	
	/**
	 * Tests if the {@link SSX#evolve} method produces valid subset for variable-size sets.
	 */
	@Test
	public void testEvolveVariableSize() {
		DescriptiveStatistics stats1 = new DescriptiveStatistics();
		DescriptiveStatistics stats2 = new DescriptiveStatistics();
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int l = PRNG.nextInt(0, n-1);
			int u = PRNG.nextInt(l+1, n);
			
			Subset s1 = new Subset(l, u, n);
			Subset s2 = new Subset(l, u, n);
			
			s1.randomize();
			s2.randomize();
			
			Subset s1copy = s1.copy();
			Subset s2copy = s2.copy();
			
			int size1 = s1copy.size();
			int size2 = s2copy.size();
			
			SSX.evolve(s1copy, s2copy);

			s1copy.validate();
			s2copy.validate();
			Assert.assertEquals(size1, s1copy.size());
			Assert.assertEquals(size2, s2copy.size());
			
			countSwapped(s1, s2, s1copy, s2copy, stats1, stats2);
		}
		
		Assert.assertEquals(0.5, stats1.getMean(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.5, stats1.getMean(), TestThresholds.STATISTICS_EPS);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		SSX ssx = new SSX(1.0);

		Subset p1 = new Subset(5, 10);
		Subset p2 = new Subset(5, 10);
		
		p1.randomize();
		p2.randomize();

		Solution s1 = new Solution(1, 0);
		s1.setVariable(0, p1);

		Solution s2 = new Solution(1, 0);
		s2.setVariable(0, p2);

		Solution[] parents = new Solution[] { s1, s2 };

		ParentImmutabilityTest.test(parents, ssx);
	}
	
	/**
	 * Records the percent of swapped values in each subset.
	 * 
	 * @param original1 the first subset
	 * @param original2 the second subset
	 * @param new1 the first evolved subset
	 * @param new2 the second evolved subset
	 * @param stats1 the percent of swapped values for the first subset
	 * @param stats2 the percent of swapped values for the second subset
	 */
	protected void countSwapped(Subset original1, Subset original2, Subset new1, Subset new2,
			DescriptiveStatistics stats1, DescriptiveStatistics stats2) {		
		Set<Integer> original1set = original1.getSet();
		Set<Integer> new1set = new1.getSet();
		Set<Integer> original2set = original2.getSet();
		Set<Integer> new2set = new2.getSet();
		
		Set<Integer> intersection = new HashSet<Integer>(original1set);
		intersection.retainAll(original2set);
		
		original1set.removeAll(intersection);
		new1set.removeAll(intersection);
		original2set.removeAll(intersection);
		new2set.removeAll(intersection);
		
		int original1size = original1set.size();
		int original2size = original2set.size();
		int minSize = Math.min(original1size, original2size);
		
		if (minSize > 0) {
			original1set.retainAll(new1set);
			original2set.retainAll(new2set);
			
			stats1.addValue((original1size - original1set.size()) / (double)minSize);
			stats2.addValue((original2size - original2set.size()) / (double)minSize);
		}
	}

}
