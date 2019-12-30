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
package org.moeaframework.util.statistics;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link OrdinalStatisticalTest} class.
 */
public class OrdinalStatisticalTestTest {

	/**
	 * The shared ordinal statistical test.
	 */
	private OrdinalStatisticalTest test = null;

	/**
	 * Constructs the shared ordinal statistical test.
	 */
	@Before
	public void setUp() {
		test = new OrdinalStatisticalTest(2) {

			public boolean test(double alpha) {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		test = null;
	}

	/**
	 * Tests if the {@link OrdinalStatisticalTest#update} procedure correctly
	 * ranks observations.
	 */
	@Test
	public void testUpdate() {
		test.add(2.0, 0);
		test.add(-1.0, 1);
		test.add(-2.0, 1);
		test.add(3.0, 0);
		test.add(5.0, 1);
		test.add(-1.0, 0);
		test.add(-3.0, 0);

		test.update();

		Assert.assertEquals(2, test.getNumberOfGroups());
		Assert.assertEquals(7, test.size());

		checkRank(-3.0, 1);
		checkRank(-2.0, 2);
		checkRank(-1.0, 3.5);
		checkRank(2.0, 5);
		checkRank(3.0, 6);
		checkRank(4.0, 7);
	}

	/**
	 * Asserts that any observations in the shared test with the specified value
	 * also have the specified rank.
	 * 
	 * @param value the value of the observation
	 * @param rank the asserted rank of the observation
	 */
	public void checkRank(double value, double rank) {
		for (RankedObservation observation : test.data) {
			if (observation.getValue() == value) {
				Assert.assertEquals(rank, observation.getRank(), Settings.EPS);
			}
		}
	}

	/**
	 * Tests if the {@link OrdinalStatisticalTest#add} method correctly throws
	 * an exception for invalid groups.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupException1() {
		test.add(0.0, -1);
	}

	/**
	 * Tests if the {@link OrdinalStatisticalTest#add} method correctly throws
	 * an exception for invalid groups.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupException2() {
		test.add(0.0, 2);
	}

}
