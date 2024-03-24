/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.core;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.ConcurrentModificationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.format.TabularData;

public class PopulationTest {

	private Population population;

	@Before
	public void setUp() {
		population = new Population();

		population.add(MockSolution.of().withObjectives(3.0, 2.0, 3.0));
		population.add(MockSolution.of().withObjectives(1.0, 2.0, 2.0));
		population.add(MockSolution.of().withObjectives(2.0, 2.0, 3.0));
		population.add(MockSolution.of().withObjectives(4.0, 3.0, 2.0));
	}

	@After
	public void tearDown() {
		population = null;
	}

	@Test
	public void testCopyConstructor() {
		Population copy = new Population(population);

		Assert.assertEquals(population.size(), copy.size());
		Assert.assertTrue(copy.containsAll(population));
		Assert.assertTrue(population.containsAll(copy));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBounds1() {
		population.get(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBounds2() {
		population.get(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveOutOfBounds1() {
		population.remove(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveOutOfBounds2() {
		population.remove(-1);
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testConcurrentModificationWithObject() {
		for (Solution solution : population) {
			population.remove(solution);
		}
	}

	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModificationWithIndex() {
		for (Solution solution : population) {
			population.remove(0);
		}
	}

	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModificationOnClear() {
		for (Solution solution : population) {
			population.clear();
		}
	}

	@Test
	public void test() {
		Population subset = new Population();
		Assert.assertTrue(subset.add(population.get(1)));
		Assert.assertTrue(subset.add(population.get(3)));

		Assert.assertFalse(subset.contains(population.get(0)));
		Assert.assertTrue(subset.contains(population.get(1)));
		Assert.assertTrue(population.containsAll(subset));
		Assert.assertFalse(subset.containsAll(population));

		Assert.assertTrue(population.removeAll(subset));
		Assert.assertFalse(population.removeAll(subset));

		Assert.assertEquals(2, population.size());

		Solution s0 = population.get(0);
		Solution s1 = population.get(1);

		Assert.assertEquals(0, population.indexOf(s0));
		Assert.assertEquals(1, population.indexOf(s1));

		population.remove(0);

		Assert.assertEquals(1, population.size());
		Assert.assertEquals(-1, population.indexOf(s0));
		Assert.assertEquals(0, population.indexOf(s1));

		Assert.assertFalse(population.remove(s0));
		Assert.assertEquals(1, population.size());
		Assert.assertFalse(population.isEmpty());
		Assert.assertTrue(population.remove(s1));
		Assert.assertEquals(0, population.size());
		Assert.assertTrue(population.isEmpty());

		Assert.assertTrue(population.addAll(subset));
		Assert.assertTrue(population.add(subset.get(0)));
		Assert.assertEquals(3, population.size());

		population.clear();

		Assert.assertTrue(population.isEmpty());
	}

	@Test
	public void testSortAndTruncate() {
		population.truncate(1, new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				return Double.compare(o1.getObjective(0), o2.getObjective(0));
			}

		});

		Assert.assertEquals(1, population.size());
		Assert.assertEquals(1.0, population.get(0).getObjective(0), Settings.EPS);
	}
	
	@Test
	public void testAsTabularData() throws IOException {
		File tempFile = TestUtils.createTempFile();
		TabularData<Solution> data = population.asTabularData();
		
		data.saveCSV(tempFile);
		Assert.assertEquals(population.size() + 1, TestUtils.lineCount(tempFile));
	}

}
