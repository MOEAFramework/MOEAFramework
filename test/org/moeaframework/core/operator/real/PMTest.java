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
import org.moeaframework.core.operator.MeanCentricVariationTest;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.RealVariable;

@RunWith(TravisRunner.class)
public class PMTest extends MeanCentricVariationTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new PM(1.0, 20.0));
	}

	@Test
	@RetryOnTravis
	public void testDistribution() {
		PM pm = new PM(1.0, 20.0);

		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(1.0, -10.0, 10.0));
		solution.setVariable(1, new RealVariable(-1.0, -10.0, 10.0));

		Solution[] parents = new Solution[] { solution };

		Solution[] offspring = new Solution[TestThresholds.SAMPLES];

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			offspring[i] = pm.evolve(parents)[0];
		}

		check(parents, offspring);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		PM pm = new PM(1.0, 20.0);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0) };

		ParentImmutabilityTest.test(parents, pm);
	}

}
