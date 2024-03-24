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
package org.moeaframework.util.statistics;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;

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
		kw.addAll(new double[] { 10, 11, 12, 13, 14, 15 }, 0);
		kw.addAll(new double[] { 16, 17, 18, 19, 20, 21 }, 1);
		kw.addAll(new double[] { 12, 13, 14, 15, 16, 17 }, 2);

		Assert.assertTrue(kw.test(0.05));
		Assert.assertEquals(11.56, kw.H(), TestThresholds.STATISTICS_EPS);
	}

	/**
	 * Daily bottle-cap production of three machines from Kruskal and Wallis (1952). This example demonstrates the test
	 * without ties.
	 */
	@Test
	public void testExample2() {
		KruskalWallisTest kw = new KruskalWallisTest(3);
		kw.addAll(new double[] { 340, 345, 330, 342, 338 }, 0);
		kw.addAll(new double[] { 339, 333, 344 }, 1);
		kw.addAll(new double[] { 347, 343, 349, 355 }, 2);

		Assert.assertFalse(kw.test(0.05));
		Assert.assertEquals(5.656, kw.H(), 0.001);
	}

	/**
	 * Birth weights of eight litters of pigs example from Kruskal and Wallis (1952). This example demonstrates the
	 * test with ties.
	 */
	@Test
	public void testExample3() {
		KruskalWallisTest kw = new KruskalWallisTest(8);
		kw.addAll(new double[] { 2.0, 2.8, 3.3, 3.2, 4.4, 3.6, 1.9, 3.3, 2.8, 1.1 }, 0);
		kw.addAll(new double[] { 3.5, 2.8, 3.2, 3.5, 2.3, 2.4, 2.0, 1.6 }, 1);
		kw.addAll(new double[] { 3.3, 3.6, 2.6, 3.1, 3.2, 3.3, 2.9, 3.4, 3.2, 3.2 }, 2);
		kw.addAll(new double[] { 3.2, 3.3, 3.2, 2.9, 3.3, 2.5, 2.6, 2.8 }, 3);
		kw.addAll(new double[] { 2.6, 2.6, 2.9, 2.0, 2.0, 2.1 }, 4);
		kw.addAll(new double[] { 3.1, 2.9, 3.1, 2.5 }, 5);
		kw.addAll(new double[] { 2.6, 2.2, 2.2, 2.5, 1.2, 1.2 }, 6);
		kw.addAll(new double[] { 2.5, 2.4, 3.0, 1.4 }, 7);

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
