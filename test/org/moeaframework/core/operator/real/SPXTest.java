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

@RunWith(TravisRunner.class)
public class SPXTest extends MeanCentricVariationTest {

	/**
	 * Test if the offspring centroid is the same as the parent centroid when 3
	 * completely different parents are used.
	 */
	@Test
	@RetryOnTravis
	public void testFullDistribution() {
		SPX sbx = new SPX(3, TestThresholds.SAMPLES);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(1.0, 0.0) };

		Solution[] offspring = sbx.evolve(parents);

		check(parents, offspring);
	}

	/**
	 * Test if the offspring centroid is the same as the parent centroid when
	 * the parents are degenerate along one axis.
	 */
	@Test
	@RetryOnTravis
	public void testPartialDistribution() {
		SPX sbx = new SPX(3, TestThresholds.SAMPLES);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(0.0, 2.0) };

		Solution[] offspring = sbx.evolve(parents);

		check(parents, offspring);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		SPX spx = new SPX(3, 3);

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0),
				newSolution(0.0, 1.0), newSolution(1.0, 0.0) };

		ParentImmutabilityTest.test(parents, spx);
	}

}
