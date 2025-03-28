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
package org.moeaframework.core.operator.permutation;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.operator.AbstractPermutationOperatorTest;
import org.moeaframework.core.variable.Permutation;

public class PMXTest extends AbstractPermutationOperatorTest<PMX> {
	
	@Override
	public PMX createInstance() {
		return new PMX(1.0);
	}

	@Test
	public void testEvolve() {
		for (int i = 0; i < 1000; i++) {
			Permutation p1 = new Permutation(6);
			p1.fromArray(new int[] { 0, 1, 2, 3, 4, 5 });
			
			Permutation p2 = new Permutation(6);
			p2.fromArray(new int[] { 5, 4, 3, 2, 1, 0 });

			new PMX().evolve(p1, p2);

			Assert.assertTrue(Permutation.isPermutation(p1.toArray()));
			Assert.assertTrue(Permutation.isPermutation(p2.toArray()));
		}
	}

}
