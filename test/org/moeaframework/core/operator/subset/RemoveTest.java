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
package org.moeaframework.core.operator.subset;

import java.util.Set;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.AbstractSubsetOperatorTest;
import org.moeaframework.core.variable.Subset;

public class RemoveTest extends AbstractSubsetOperatorTest<Remove> {
	
	@Override
	public Remove createInstance() {
		return new Remove(1.0);
	}

	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the remove mutation occurs with the specified probability.
	 * 
	 * @param probability the probability
	 */
	private void testProbability(double probability) {
		Remove remove = new Remove(probability);
		int count = 0;
		int skipped = 0;

		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int l = PRNG.nextInt(0, n);
			int u = PRNG.nextInt(l, n);
			
			Subset subset = new Subset(l, u, n);
			subset.randomize();
			
			Solution original = new Solution(1, 0);
			original.setVariable(0, subset);

			Solution mutated = remove.evolve(new Solution[] { original })[0];
			Subset newSubset = (Subset)mutated.getVariable(0);
			
			if ((subset.size() == newSubset.size()) && (subset.size() == subset.getL())) {
				skipped++;
			} else if (testRemove(subset, newSubset)) {
				count++;
			}
		}

		Assert.assertEquals(probability, (double)count / (TestEnvironment.SAMPLES - skipped),
				TestEnvironment.LOW_PRECISION);
	}

	/**
	 * Returns {@code true} if the result is a valid remove; {@code false} otherwise.
	 * 
	 * @param v1 the first subset
	 * @param v2 the second subset
	 * @return {@code true} if the result is a valid remove; {@code false} otherwise
	 */
	protected boolean testRemove(Subset v1, Subset v2) {
		Set<Integer> set = v1.getSet();
		set.removeAll(v2.getSet());
		return set.size() == 1;
	}

}
