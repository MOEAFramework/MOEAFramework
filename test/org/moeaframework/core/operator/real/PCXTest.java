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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TravisRunner;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentCentricVariationTest;
import org.moeaframework.core.operator.ParentImmutabilityTest;

/**
 * Tests parent-centric crossover (PCX) to ensure clusters are formed centered
 * around each parent.
 */
@RunWith(TravisRunner.class)
public class PCXTest extends ParentCentricVariationTest {

	/**
	 * Tests if the offspring form clusters distributed around each parent.
	 */
	@Test
	@RetryOnTravis
	public void testFullDistribution() {
		PCX pcx = new PCX(3, TestThresholds.SAMPLES);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(1.0, 0.0) };

		Solution[] offspring = pcx.evolve(parents);

		check(parents, offspring);
	}

	/**
	 * Tests if the offspring form clusters distributed around each parent, with
	 * the parents being degenerate along one axis.
	 */
	@Test
	@RetryOnTravis
	public void testPartialDistribution() {
		PCX pcx = new PCX(3, TestThresholds.SAMPLES);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(0.0, 2.0) };

		Solution[] offspring = pcx.evolve(parents);

		check(parents, offspring);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		PCX pcx = new PCX(3, 3);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(1.0, 0.0) };

		ParentImmutabilityTest.test(parents, pcx);
	}

}
