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
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link MannWhitneyUTest} class.
 */
public class MannWhitneyUTestTest {

	/**
	 * Test from Sheskin (2004) in Chapter 12.
	 */
	@Test
	public void testExample1() {
		MannWhitneyUTest test = new MannWhitneyUTest();
		test.add(11, 0);
		test.add(1, 0);
		test.add(0, 0);
		test.add(2, 0);
		test.add(0, 0);
		test.add(11, 1);
		test.add(11, 1);
		test.add(5, 1);
		test.add(8, 1);
		test.add(4, 1);

		Assert.assertFalse(test.test(0.05));
	}

	/**
	 * This is the Tortoise vs. Hare example from
	 * <a href="http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U">
	 * Wikipedia</a>
	 */
	@Test
	public void testExample2() {
		MannWhitneyUTest test = new MannWhitneyUTest();

		for (int i = 0; i <= 8; i++) {
			test.add(i, 0);
		}

		for (int i = 9; i <= 18; i++) {
			test.add(i, 1);
		}

		for (int i = 19; i <= 28; i++) {
			test.add(i, 0);
		}

		for (int i = 29; i <= 37; i++) {
			test.add(i, 1);
		}

		Assert.assertTrue(test.test(0.05));
		Assert.assertEquals(100, test.lastU, TestThresholds.STATISTICS_EPS);
	}
	
	/**
	 * Commons Math 3.0 introduced their own MannWhitneyUTest class.  The
	 * Commons Math implementation only supports the normal approximation for
	 * the U statistic, and will be inaccurate with small sample sizes.
	 */
	@Test
	public void testCommonsMath() {
		double[] d1 = new double[100];
		double[] d2 = new double[100];
		
		for (int i = 0; i < 100; i++) {
			d1[i] = PRNG.nextGaussian(10.0, 5.0);
			d2[i] = PRNG.nextGaussian(11.0, 6.0);
		}
		
		MannWhitneyUTest test1 = new MannWhitneyUTest();
		test1.addAll(d1, 0);
		test1.addAll(d2, 1);
		test1.test(0.05);
		double u1 = test1.lastU;
		
		org.apache.commons.math3.stat.inference.MannWhitneyUTest test2 = 
				new org.apache.commons.math3.stat.inference.MannWhitneyUTest();
		double u2 = test2.mannWhitneyU(d1, d2);
		
		Assert.assertEquals(100*100 - u2, u1, Settings.EPS);
	}
	
	@Test
	public void testAllEqual() {
		MannWhitneyUTest test = new MannWhitneyUTest();
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);

		Assert.assertFalse(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}

}
