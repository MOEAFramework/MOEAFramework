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
package org.moeaframework.core;

import java.util.Comparator;
import java.util.ConcurrentModificationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link Population} class.
 */
public class PopulationTest {

	/**
	 * The population being tested.
	 */
	private Population population;

	/**
	 * Constructs any shared objects used by this class.
	 */
	@Before
	public void setUp() {
		population = new Population();

		population.add(new Solution(new double[] { 3.0, 2.0, 3.0 }));
		population.add(new Solution(new double[] { 1.0, 2.0, 2.0 }));
		population.add(new Solution(new double[] { 2.0, 2.0, 3.0 }));
		population.add(new Solution(new double[] { 4.0, 3.0, 2.0 }));
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		population = null;
	}

	/**
	 * Tests the copy constructor to ensure the new population is an identical
	 * copy of the original.
	 */
	@Test
	public void testCopyConstructor() {
		Population copy = new Population(population);

		Assert.assertEquals(population.size(), copy.size());
		Assert.assertTrue(copy.containsAll(population));
		Assert.assertTrue(population.containsAll(copy));
	}

	/**
	 * Tests if the {@code get} method throws an exception if accessing an
	 * invalid index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetBoundaryCheck1() {
		population.get(4);
	}

	/**
	 * Tests if the {@code get} method throws an exception if accessing an
	 * invalid index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetBoundaryCheck2() {
		population.get(-1);
	}

	/**
	 * Tests if the {@code remove} method throws an exception if accessing an
	 * invalid index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveBoundaryCheck1() {
		population.remove(4);
	}

	/**
	 * Tests if the {@code remove} method throws an exception if accessing an
	 * invalid index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveBoundaryCheck2() {
		population.remove(-1);
	}

	/**
	 * Tests if concurrent modifications are detected.
	 */
	@Test(expected = ConcurrentModificationException.class)
	public void testConcurrentModification1() {
		for (Solution solution : population) {
			population.remove(solution);
		}
	}

	/**
	 * Tests if concurrent modifications are detected.
	 */
	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModification2() {
		for (Solution solution : population) {
			population.remove(0);
		}
	}

	/**
	 * Tests if concurrent modifications are detected.
	 */
	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModification3() {
		for (Solution solution : population) {
			population.clear();
		}
	}

	/**
	 * Tests several of the trivial operations that should be provided by the
	 * underlying collection used in the implementation, including {@code get},
	 * {@code contains}, {@code containsAll}, {@code remove}, {@code removeAll},
	 * {@code size}, {@code isEmpty}, {@code add}, {@code addAll} and
	 * {@code clear}.
	 */
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

	/**
	 * Tests if the {@code sort} and {@code truncate} methods work correctly.
	 */
	@Test
	public void testSortAndTruncate() {
		population.truncate(1, new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				return Double.compare(o1.getObjective(0), o2.getObjective(0));
			}

		});

		Assert.assertEquals(1, population.size());
		Assert.assertEquals(1.0, population.get(0).getObjective(0),
				Settings.EPS);
	}

}
