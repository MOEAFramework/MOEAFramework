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
package org.moeaframework.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;

public class VectorTest {

	@Test
	public void testSubtract() {
		Assert.assertArrayEquals(new double[0], Vector.subtract(new double[0], new double[0]), Settings.EPS);

		Assert.assertArrayEquals(new double[] { 0.0, -1.0, 1.0 },
				Vector.subtract(new double[] { 1.0, 0.0, 2.0 }, new double[] { 1.0, 1.0, 1.0 }),
				Settings.EPS);
	}

	@Test
	public void testAdd() {
		Assert.assertArrayEquals(new double[0], Vector.add(new double[0], new double[0]), Settings.EPS);

		Assert.assertArrayEquals(new double[] { 2.0, 0.0, -1.0 },
				Vector.add(new double[] { 1.0, 0.0, -2.0 }, new double[] { 1.0, 0.0, 1.0 }),
				Settings.EPS);
	}

	@Test
	public void testMultiply() {
		Assert.assertArrayEquals(new double[0], Vector.multiply(2.0, new double[0]), Settings.EPS);

		Assert.assertArrayEquals(new double[] { 2.0, 0.0, -2.0 },
				Vector.multiply(2.0, new double[] { 1.0, 0.0, -1.0 }),
				Settings.EPS);
	}

	@Test
	public void testNegate() {
		Assert.assertArrayEquals(new double[0], Vector.negate(new double[0]), Settings.EPS);

		Assert.assertArrayEquals(new double[] { -2.0, 0.0, 2.0 },
				Vector.negate(new double[] { 2.0, 0.0, -2.0 }),
				Settings.EPS);
	}

	@Test
	public void testDivide() {
		Assert.assertArrayEquals(new double[0], Vector.divide(new double[0], 2.0), Settings.EPS);

		Assert.assertArrayEquals(new double[] { 0.5, 0.0, -0.5 }, Vector.divide(new double[] { 1.0, 0.0, -1.0 }, 2.0),
				Settings.EPS);
	}

	@Test
	public void testDot() {
		Assert.assertEquals(0.0, Vector.dot(new double[0], new double[0]), Settings.EPS);

		Assert.assertEquals(3.0, Vector.dot(new double[] { 1.0, 0.0, -1.0 }, new double[] { 2.0, 1.0, -1.0 }),
				Settings.EPS);
	}

	@Test
	public void testMagnitude() {
		Assert.assertEquals(0.0, Vector.magnitude(new double[0]), Settings.EPS);

		Assert.assertEquals(Math.sqrt(2.0), Vector.magnitude(new double[] { 1.0, 0.0, -1.0 }), Settings.EPS);
	}

	@Test
	public void testNormalize() {
		Assert.assertArrayEquals(
				new double[] { 1.0 / Math.sqrt(2.0), 0.0, -1.0 / Math.sqrt(2.0) },
				Vector.normalize(new double[] { 1.0, 0.0, -1.0 }),
				Settings.EPS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNormalizeException() {
		Vector.normalize(new double[] { 0.0, 0.0, 0.0 });
	}

	@Test
	public void testIsZero() {
		Assert.assertTrue(Vector.isZero(new double[0]));
		Assert.assertTrue(Vector.isZero(new double[] { Settings.EPS }));
		Assert.assertFalse(Vector.isZero(new double[] { 2.0 * Settings.EPS }));
	}

	@Test
	public void testMean() {
		Assert.assertArrayEquals(new double[] { 2.0 / 3.0, 0.0, -2.0 / 3.0 },
				Vector.mean(new double[][] { { 0.0, 1.0, -1.0 }, { 1.0, -1.0, 0.0 }, { 1.0, 0.0, -1.0 } }), 
				Settings.EPS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMeanEmpty() {
		Vector.mean(new double[0][0]);
	}

	@Test
	public void testOrthogonalizeWith2DArray() {
		double[][] input = new double[][] { { 3.0, 1.0 }, { 2.0, 2.0 } };
		double[][] expected = new double[][] { { 3.0, 1.0 }, { -2.0 / 5.0, 6.0 / 5.0 } };
		double[][] output = Vector.orthogonalize(input);

		for (int i = 0; i < expected.length; i++) {
			Assert.assertArrayEquals(expected[i], output[i], Settings.EPS);
		}
	}

	@Test
	public void testOrthogonalizeConstructedPiecewise() {
		double[][] input = new double[][] { { 3.0, 1.0 }, { 2.0, 2.0 } };
		double[][] expected = new double[][] { { 3.0, 1.0 }, { -2.0 / 5.0, 6.0 / 5.0 } };

		List<double[]> basis = new ArrayList<double[]>();

		Assert.assertArrayEquals(expected[0], Vector.orthogonalize(input[0], basis), Settings.EPS);

		basis.add(input[0]);

		Assert.assertArrayEquals(expected[1], Vector.orthogonalize(input[1], basis), Settings.EPS);
	}
	
	@Test
	public void testLength() {
		Assert.assertEquals(0, Vector.length(new double[0], new double[0]));
		Assert.assertEquals(5, Vector.length(new double[5], new double[5]));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLengthDifferent() {
		Vector.length(new double[5], new double[6]);
	}
	
	@Test
	public void testOf() {
		Assert.assertArrayEquals(new double[0], Vector.of(0, 5.0), Settings.EPS);
		Assert.assertArrayEquals(new double[] { 5.0, 5.0 }, Vector.of(2, 5.0), Settings.EPS);
	}
	

}
