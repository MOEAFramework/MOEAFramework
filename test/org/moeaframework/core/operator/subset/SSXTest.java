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
package org.moeaframework.core.operator.subset;

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
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int k = PRNG.nextInt(0, n);
			Subset s1 = new Subset(k, n);
			Subset s2 = new Subset(k, n);
			
			s1.randomize();
			s2.randomize();
			
			SSX.evolve(s1, s2);

			s1.validate();
			s2.validate();
		}
	}
	
	/**
	 * Tests if the {@link SSX#evolve} method produces valid subset for variable-size sets.
	 */
	@Test
	public void testEvolveVariableSize() {
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int l = PRNG.nextInt(0, n-1);
			int u = PRNG.nextInt(l+1, n);
			
			Subset s1 = new Subset(l, u, n);
			Subset s2 = new Subset(l, u, n);
			
			s1.randomize();
			s2.randomize();
			
			int size1 = s1.size();
			int size2 = s2.size();
			
			SSX.evolve(s1, s2);

			s1.validate();
			s2.validate();
			Assert.assertEquals(size1, s1.size());
			Assert.assertEquals(size2, s2.size());
		}
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

}
