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
package org.moeaframework.core.operator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

public class UniformSelectionTest {

	private Solution solution1;
	private Solution solution2;
	private Solution solution3;

	private Population population;

	@Before
	public void setUp() {
		solution1 = new Solution(new double[] { 0 });
		solution2 = new Solution(new double[] { 1 });
		solution3 = new Solution(new double[] { 2 });

		population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
	}

	@After
	public void tearDown() {
		solution1 = null;
		solution2 = null;
		solution3 = null;
		population = null;
	}

	@Test
	public void testUniformSelectionPressure() {
		int[] counts = new int[3];

		UniformSelection selection = new UniformSelection();

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = selection.select(1, population)[0];
			counts[(int)solution.getObjective(0)]++;
		}

		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(0.333, counts[i]
					/ (double)TestThresholds.SAMPLES,
					TestThresholds.SELECTION_EPS);
		}
	}

}
