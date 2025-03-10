/* Copyright 2009-2025 David Hadka
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class OrdinalStatisticalTestTest {

	private OrdinalStatisticalTest test = null;

	@Before
	public void setUp() {
		test = new OrdinalStatisticalTest(2) {

			@Override
			public boolean test(double alpha) {
				throw new UnsupportedOperationException();
			}

		};
	}

	@After
	public void tearDown() {
		test = null;
	}

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
	
	@Test
	public void testStatistics() {
		test.add(2.0, 0);
		test.add(-1.0, 1);
		test.add(-2.0, 1);
		test.add(3.0, 0);
		test.add(5.0, 1);
		test.add(-1.0, 0);
		test.add(-3.0, 0);
		
		Assert.assertEquals(4, test.getStatistics(0).getN());
		Assert.assertEquals(3, test.getStatistics(1).getN());
	}

	public void checkRank(double value, double rank) {
		for (RankedObservation observation : test.data) {
			if (observation.getValue() == value) {
				Assert.assertEquals(rank, observation.getRank(), TestThresholds.HIGH_PRECISION);
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidGroupException1() {
		test.add(0.0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidGroupException2() {
		test.add(0.0, 2);
	}

}
