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
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.sensitivity.ProblemStub;

/**
 * Tests the {@link AdaptiveGridArchive} class.
 */
public class AdaptiveGridArchiveTest {

	/**
	 * The shared problem used by these tests.
	 */
	private Problem problem;

	/**
	 * Sets up the shared problem used by these tests.
	 */
	@Before
	public void setUp() {
		problem = new ProblemStub(2);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		problem = null;
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#findIndex(Solution)} method
	 * returns the correct value if the archive is empty.
	 */
	@Test
	public void testFindIndexEmpty() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(2, problem, 2);

		Assert.assertEquals(-1, archive.findIndex(TestUtils.newSolution(0.0,
				0.0)));
		Assert.assertEquals(-1, archive.findIndex(TestUtils.newSolution(1.0,
				1.0)));
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#findIndex(Solution)} method
	 * returns the correct value if the archive consists of a single entry.
	 */
	@Test
	public void testFindIndexSingleEntry() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(2, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 0.0));

		Assert.assertEquals(0, archive.findIndex(TestUtils
				.newSolution(0.0, 0.0)));
		Assert.assertEquals(-1, archive.findIndex(TestUtils.newSolution(1.0,
				1.0)));
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#findIndex(Solution)} method
	 * returns the correct value if the archive consists of multiple entries.
	 */
	@Test
	public void testFindIndexMultipleEntries() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(2, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));

		Assert.assertEquals(0, archive.findIndex(TestUtils.newSolution(0.25,
				0.25)));
		Assert.assertEquals(1, archive.findIndex(TestUtils.newSolution(0.75,
				0.25)));
		Assert.assertEquals(2, archive.findIndex(TestUtils.newSolution(0.25,
				0.75)));
		Assert.assertEquals(3, archive.findIndex(TestUtils.newSolution(0.75,
				0.75)));
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#findDensestIndex()} returns the
	 * correct value.
	 */
	@Test
	public void testFindDensestIndex() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(3, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.7, 0.2));
		
		Solution densestSolution = archive.pickSolutionFromDensestCell();

		Assert.assertTrue(archive.get(1) == densestSolution ||
				archive.get(2) == densestSolution);
	}

	/**
	 * Tests if the archive correctly adapts its internal settings as its
	 * contents change.
	 */
	@Test
	public void testAdaptGrid() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(4, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(0.7, 0.2));
		archive.add(TestUtils.newSolution(0.6, 0.3));
		archive.add(TestUtils.newSolution(0.8, 0.1));

		Assert.assertArrayEquals(new int[] { 0, 3, 1, 0 }, archive.density);
		Assert.assertArrayEquals(new double[] { 0.0, 0.1 }, archive.minimum,
				Settings.EPS);
		Assert.assertArrayEquals(new double[] { 0.8, 1.0 }, archive.maximum,
				Settings.EPS);
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#add(Solution)} method correctly
	 * adds dominating solutions.
	 */
	@Test
	public void testAddDominating() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(4, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));
		archive.add(TestUtils.newSolution(0.0, 0.0));

		Assert.assertEquals(1, archive.size());
		
		int index = archive.findIndex(archive.get(0));
		
		int[] expectedDensity = new int[4];
		expectedDensity[index] = 1;
		
		Assert.assertArrayEquals(expectedDensity, archive.density);
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#remove(int)} method correctly
	 * removes the solution and updates the archive settings.
	 */
	@Test
	public void testRemoveIndex() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(4, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));

		archive.remove(1);

		Assert.assertEquals(1, archive.size());
		
		int index = archive.findIndex(archive.get(0));
		
		int[] expectedDensity = new int[4];
		expectedDensity[index] = 1;
		
		Assert.assertArrayEquals(expectedDensity, archive.density);
	}

	/**
	 * Tests if the {@link AdaptiveGridArchive#remove(Solution)} method
	 * correctly removes the solution and updates the archive settings.
	 */
	@Test
	public void testRemoveSolution() {
		AdaptiveGridArchive archive = new AdaptiveGridArchive(4, problem, 2);

		archive.add(TestUtils.newSolution(0.0, 1.0));
		archive.add(TestUtils.newSolution(1.0, 0.0));

		archive.remove(archive.get(1));

		Assert.assertEquals(1, archive.size());
		
		int index = archive.findIndex(archive.get(0));
		
		int[] expectedDensity = new int[4];
		expectedDensity[index] = 1;
		
		Assert.assertArrayEquals(expectedDensity, archive.density);
	}
	
	/**
	 * Since the adaptive grid archive uses an array internally, we must prevent
	 * the number of divisions exceeding the maximum length of arrays.
	 */
	@Test(expected=FrameworkException.class)
	public void testOverflow() {
		new AdaptiveGridArchive(100, new ProblemStub(4), 256);
	}

}
