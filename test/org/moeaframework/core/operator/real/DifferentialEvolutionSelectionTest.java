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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

public class DifferentialEvolutionSelectionTest {

	private DifferentialEvolutionSelection selection;
	private Population population;

	@Before
	public void setUp() {
		selection = new DifferentialEvolutionSelection();
		population = new Population();

		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
		population.add(new Solution(0, 0));
	}

	@After
	public void tearDown() {
		selection = null;
		population = null;
	}

	@Test
	public void testCurrentIndex() {
		for (int i = 0; i < population.size(); i++) {
			selection.setCurrentIndex(i);
			
			Assert.assertEquals(population.get(i), selection.select(4,
					population)[0]);
		}
	}

	@Test
	public void testUniqueness() {
		for (int i = 0; i < 100; i++) {
			selection.setCurrentIndex(i % population.size());
			
			Solution[] result = selection.select(4, population);

			for (int j = 0; j < result.length - 1; j++) {
				for (int k = j + 1; k < result.length; k++) {
					Assert.assertNotSame(result[j], result[k]);
				}
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulationSize() {
		selection.select(9, population);
	}

}
