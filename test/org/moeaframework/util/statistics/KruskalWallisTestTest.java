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
import org.junit.Test;
import org.moeaframework.TestThresholds;

/**
 * Tests the {@link KruskalWallisTest} class.
 */
public class KruskalWallisTestTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testOneGroup() {
		new KruskalWallisTest(1);
	}

	/**
	 * Test from Sheskin (2004) in Chapter 22.
	 */
	@Test
	public void testExample1() {
		KruskalWallisTest kw = new KruskalWallisTest(3);
		kw.add(10, 0);
		kw.add(11, 0);
		kw.add(12, 0);
		kw.add(13, 0);
		kw.add(14, 0);
		kw.add(15, 0);
		kw.add(16, 1);
		kw.add(17, 1);
		kw.add(18, 1);
		kw.add(19, 1);
		kw.add(20, 1);
		kw.add(21, 1);
		kw.add(12, 2);
		kw.add(13, 2);
		kw.add(14, 2);
		kw.add(15, 2);
		kw.add(16, 2);
		kw.add(17, 2);

		Assert.assertTrue(kw.test(0.05));
		Assert.assertEquals(11.56, kw.H(), TestThresholds.STATISTICS_EPS);
	}

	/**
	 * Daily bottle-cap production of three machines from Kruskal and Wallis
	 * (1952). This example demonstrates the test without ties.
	 */
	@Test
	public void testExample2() {
		KruskalWallisTest kw = new KruskalWallisTest(3);
		kw.add(340, 0);
		kw.add(345, 0);
		kw.add(330, 0);
		kw.add(342, 0);
		kw.add(338, 0);
		kw.add(339, 1);
		kw.add(333, 1);
		kw.add(344, 1);
		kw.add(347, 2);
		kw.add(343, 2);
		kw.add(349, 2);
		kw.add(355, 2);

		Assert.assertFalse(kw.test(0.05));
		Assert.assertEquals(5.656, kw.H(), 0.001);
	}

	/**
	 * Birth weights of eight litters of pigs example from Kruskal and Wallis
	 * (1952). This example demonstrates the test with ties.
	 */
	@Test
	public void testExample3() {
		KruskalWallisTest kw = new KruskalWallisTest(8);
		kw.add(2.0, 0);
		kw.add(2.8, 0);
		kw.add(3.3, 0);
		kw.add(3.2, 0);
		kw.add(4.4, 0);
		kw.add(3.6, 0);
		kw.add(1.9, 0);
		kw.add(3.3, 0);
		kw.add(2.8, 0);
		kw.add(1.1, 0);
		kw.add(3.5, 1);
		kw.add(2.8, 1);
		kw.add(3.2, 1);
		kw.add(3.5, 1);
		kw.add(2.3, 1);
		kw.add(2.4, 1);
		kw.add(2.0, 1);
		kw.add(1.6, 1);
		kw.add(3.3, 2);
		kw.add(3.6, 2);
		kw.add(2.6, 2);
		kw.add(3.1, 2);
		kw.add(3.2, 2);
		kw.add(3.3, 2);
		kw.add(2.9, 2);
		kw.add(3.4, 2);
		kw.add(3.2, 2);
		kw.add(3.2, 2);
		kw.add(3.2, 3);
		kw.add(3.3, 3);
		kw.add(3.2, 3);
		kw.add(2.9, 3);
		kw.add(3.3, 3);
		kw.add(2.5, 3);
		kw.add(2.6, 3);
		kw.add(2.8, 3);
		kw.add(2.6, 4);
		kw.add(2.6, 4);
		kw.add(2.9, 4);
		kw.add(2.0, 4);
		kw.add(2.0, 4);
		kw.add(2.1, 4);
		kw.add(3.1, 5);
		kw.add(2.9, 5);
		kw.add(3.1, 5);
		kw.add(2.5, 5);
		kw.add(2.6, 6);
		kw.add(2.2, 6);
		kw.add(2.2, 6);
		kw.add(2.5, 6);
		kw.add(1.2, 6);
		kw.add(1.2, 6);
		kw.add(2.5, 7);
		kw.add(2.4, 7);
		kw.add(3.0, 7);
		kw.add(1.4, 7);

		Assert.assertTrue(kw.test(0.01));
		Assert.assertEquals(18.464, kw.H(), 0.001);
		Assert.assertEquals(0.9945, kw.C(), 0.0001);
	}
	
	@Test
	public void testAllEqual() {
		KruskalWallisTest kw = new KruskalWallisTest(2);
		
		// a number around 20 is sufficient to cause the convergence
		// exception if not guarded against
		for (int i=0; i<20; i++) {
			kw.add(10.0, 0);
			kw.add(10.0, 1);
		}

		Assert.assertFalse(kw.test(0.05));
		Assert.assertFalse(kw.test(0.01));
	}

}
