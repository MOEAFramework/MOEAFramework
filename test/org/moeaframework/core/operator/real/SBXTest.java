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
package org.moeaframework.core.operator.real;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TravisRunner;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentCentricVariationTest;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.RealVariable;

@RunWith(TravisRunner.class)
public class SBXTest extends ParentCentricVariationTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new SBX(1.0, 20.0));
	}

	/**
	 * Tests if the offspring form clusters distributed around each parent.
	 */
	@Test
	@RetryOnTravis
	public void testDistribution1() {
		SBX sbx = new SBX(1.0, 20.0);

		Solution s1 = new Solution(2, 0);
		s1.setVariable(0, new RealVariable(2.0, -10.0, 10.0));
		s1.setVariable(1, new RealVariable(2.0, -10.0, 10.0));

		Solution s2 = new Solution(2, 0);
		s2.setVariable(0, new RealVariable(-2.0, -10.0, 10.0));
		s2.setVariable(1, new RealVariable(-2.0, -10.0, 10.0));

		Solution[] parents = new Solution[] { s1, s2 };

		Solution[] offspring = new Solution[TestThresholds.SAMPLES];

		for (int i = 0; i < TestThresholds.SAMPLES; i += 2) {
			Solution[] children = sbx.evolve(parents);
			offspring[i] = children[0];
			offspring[i + 1] = children[1];
		}

		Solution[] centroids = new Solution[] { s1, s2, newSolution(2.0, -2.0),
				newSolution(-2.0, 2.0) };

		check(centroids, offspring);
	}
	
	/**
	 * Similar to {@code testDistribution1}, except the parents are swapped.
	 */
	@Test
	public void testDistribution2() {
		SBX sbx = new SBX(1.0, 20.0);

		Solution s1 = new Solution(2, 0);
		s1.setVariable(0, new RealVariable(-2.0, -10.0, 10.0));
		s1.setVariable(1, new RealVariable(-2.0, -10.0, 10.0));

		Solution s2 = new Solution(2, 0);
		s2.setVariable(0, new RealVariable(2.0, -10.0, 10.0));
		s2.setVariable(1, new RealVariable(2.0, -10.0, 10.0));

		Solution[] parents = new Solution[] { s1, s2 };

		Solution[] offspring = new Solution[TestThresholds.SAMPLES];

		for (int i = 0; i < TestThresholds.SAMPLES; i += 2) {
			Solution[] children = sbx.evolve(parents);
			offspring[i] = children[0];
			offspring[i + 1] = children[1];
		}

		Solution[] centroids = new Solution[] { s1, s2, newSolution(2.0, -2.0),
				newSolution(-2.0, 2.0) };

		check(centroids, offspring);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		SBX sbx = new SBX(1.0, 20.0);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0) };

		ParentImmutabilityTest.test(parents, sbx);
	}
	
	/**
	 * Test to ensure the SBX operator works correctly if the PRNG draws a
	 * value of 1.0.  The SBX code results in out-of-bounds values if this
	 * happens and must be guarded against.
	 */
	@Test
	public void testRNGError() {
		Random originalRandom = PRNG.getRandom();
		
		PRNG.setRandom(new Random() {

			private static final long serialVersionUID = 5106394622429956004L;

			@Override
			public double nextDouble() {
				return 1.0;
			}
			
		});
		
		SBX sbx = new SBX(1.0, 20.0);

		Solution s1 = new Solution(2, 0);
		s1.setVariable(0, new RealVariable(-2.0, -10.0, 10.0));
		s1.setVariable(1, new RealVariable(-2.0, -10.0, 10.0));

		Solution s2 = new Solution(2, 0);
		s2.setVariable(0, new RealVariable(2.0, -10.0, 10.0));
		s2.setVariable(1, new RealVariable(2.0, -10.0, 10.0));

		Solution[] parents = new Solution[] { s1, s2 };

		// if error, RealVariable#setValue will throw an
		// IllegalArgumentException 
		sbx.evolve(parents);
		
		PRNG.setRandom(originalRandom);
	}

}
