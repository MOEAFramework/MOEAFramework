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
package org.moeaframework.core.operator.permutation;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Permutation;

/**
 * Tests the {@link PMX} class.
 */
public class PMXTest {

	/**
	 * Tests if the PMX crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new PMX(1.0));
	}

	/**
	 * Tests if the {@link PMX#evolve} method produces valid permutations.
	 */
	@Test
	public void testEvolve() {
		for (int i = 0; i < 1000; i++) {
			Permutation p1 = new Permutation(new int[] { 0, 1, 2, 3, 4, 5 });
			Permutation p2 = new Permutation(new int[] { 5, 4, 3, 2, 1, 0 });

			PMX.evolve(p1, p2);

			Assert.assertTrue(Permutation.isPermutation(p1.toArray()));
			Assert.assertTrue(Permutation.isPermutation(p2.toArray()));
		}
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		PMX pmx = new PMX(1.0);

		Permutation p1 = new Permutation(new int[] { 0, 1, 2, 3, 4, 5 });
		Permutation p2 = new Permutation(new int[] { 5, 4, 3, 2, 1, 0 });

		Solution s1 = new Solution(1, 0);
		s1.setVariable(0, p1);

		Solution s2 = new Solution(1, 0);
		s2.setVariable(0, p2);

		Solution[] parents = new Solution[] { s1, s2 };

		ParentImmutabilityTest.test(parents, pmx);
	}

}
