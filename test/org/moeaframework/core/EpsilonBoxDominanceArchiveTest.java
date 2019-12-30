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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link EpsilonBoxDominanceArchive} class.
 */
public class EpsilonBoxDominanceArchiveTest {

	/**
	 * The &epsilon;-box dominance archive used for testing.
	 */
	private EpsilonBoxDominanceArchive archive;

	/**
	 * Setup the archive for use by all test methods.
	 */
	@Before
	public void setUp() {
		archive = new EpsilonBoxDominanceArchive(0.5);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		archive = null;
	}

	/**
	 * Tests if the improvement counts correct discount discarded solutions.
	 */
	@Test
	public void test1() {
		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution3 = new Solution(new double[] { 1.0, 0.0 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertFalse(archive.add(solution2));
		Assert.assertFalse(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(0, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(1, archive.getNumberOfImprovements());
		Assert.assertTrue(archive.get(0) == solution1);
	}

	/**
	 * Tests if the improvement counts correctly records dominating solutions.
	 */
	@Test
	public void test2() {
		Solution solution1 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 1.0, 0.0 });
		Solution solution3 = new Solution(new double[] { 0.0, 0.0 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(2, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(3, archive.getNumberOfImprovements());
		Assert.assertTrue(archive.get(0) == solution3);
	}

	/**
	 * Tests if the improvement counts correctly records non-dominated
	 * improvements.
	 */
	@Test
	public void test3() {
		Solution solution1 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.25, 0.75 });
		Solution solution3 = new Solution(new double[] { 0.75, 0.25 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(2, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(3, archive.getNumberOfImprovements());
	}

	/**
	 * Tests if the improvement counts correctly discounts dominated, but
	 * same box cases.
	 */
	@Test
	public void test4() {
		Solution solution1 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.4, 0.4 });
		Solution solution3 = new Solution(new double[] { 0.3, 0.3 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(2, archive.getNumberOfImprovements());
	}

	/**
	 * Tests if the improvement counts correctly records non-dominated, same
	 * box cases.
	 */
	@Test
	public void test5() {
		Solution solution1 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.24, 0.26 });
		Solution solution3 = new Solution(new double[] { 0.26, 0.24 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertFalse(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(2, archive.getNumberOfImprovements());
	}

}
