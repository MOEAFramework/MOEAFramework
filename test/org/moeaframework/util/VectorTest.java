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
package org.moeaframework.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Settings;

public class VectorTest {

	@Test
	public void testSubtract() {
		Assert.assertArrayEquals(new double[0], Vector.subtract(new double[0], new double[0]), TestThresholds.HIGH_PRECISION);

		Assert.assertArrayEquals(new double[] { 0.0, -1.0, 1.0 },
				Vector.subtract(new double[] { 1.0, 0.0, 2.0 }, new double[] { 1.0, 1.0, 1.0 }),
				TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testAdd() {
		Assert.assertArrayEquals(new double[0], Vector.add(new double[0], new double[0]), TestThresholds.HIGH_PRECISION);

		Assert.assertArrayEquals(new double[] { 2.0, 0.0, -1.0 },
				Vector.add(new double[] { 1.0, 0.0, -2.0 }, new double[] { 1.0, 0.0, 1.0 }),
				TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testMultiply() {
		Assert.assertArrayEquals(new double[0], Vector.multiply(2.0, new double[0]), TestThresholds.HIGH_PRECISION);

		Assert.assertArrayEquals(new double[] { 2.0, 0.0, -2.0 },
				Vector.multiply(2.0, new double[] { 1.0, 0.0, -1.0 }),
				TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testDivide() {
		Assert.assertArrayEquals(new double[0], Vector.divide(new double[0], 2.0), TestThresholds.HIGH_PRECISION);

		Assert.assertArrayEquals(new double[] { 0.5, 0.0, -0.5 }, Vector.divide(new double[] { 1.0, 0.0, -1.0 }, 2.0),
				TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testDot() {
		Assert.assertEquals(0.0, Vector.dot(new double[0], new double[0]), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals(3.0, Vector.dot(new double[] { 1.0, 0.0, -1.0 }, new double[] { 2.0, 1.0, -1.0 }),
				TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testMagnitude() {
		Assert.assertEquals(0.0, Vector.magnitude(new double[0]), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals(Math.sqrt(2.0), Vector.magnitude(new double[] { 1.0, 0.0, -1.0 }), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testNormalize() {
		Assert.assertArrayEquals(
				new double[] { 1.0 / Math.sqrt(2.0), 0.0, -1.0 / Math.sqrt(2.0) },
				Vector.normalize(new double[] { 1.0, 0.0, -1.0 }),
				TestThresholds.HIGH_PRECISION);
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
				TestThresholds.HIGH_PRECISION);
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
			Assert.assertArrayEquals(expected[i], output[i], TestThresholds.HIGH_PRECISION);
		}
	}

	@Test
	public void testOrthogonalizeConstructedPiecewise() {
		double[][] input = new double[][] { { 3.0, 1.0 }, { 2.0, 2.0 } };
		double[][] expected = new double[][] { { 3.0, 1.0 }, { -2.0 / 5.0, 6.0 / 5.0 } };

		List<double[]> basis = new ArrayList<>();

		Assert.assertArrayEquals(expected[0], Vector.orthogonalize(input[0], basis), TestThresholds.HIGH_PRECISION);

		basis.add(input[0]);

		Assert.assertArrayEquals(expected[1], Vector.orthogonalize(input[1], basis), TestThresholds.HIGH_PRECISION);
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
		Assert.assertArrayEquals(new double[0], Vector.of(0, 5.0), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 5.0, 5.0 }, Vector.of(2, 5.0), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testUniform() {
		Assert.assertEquals(1.0, StatUtils.sum(Vector.uniform(3)), TestThresholds.HIGH_PRECISION);
		Assert.assertGreaterThan(Vector.magnitude(Vector.subtract(Vector.uniform(3), Vector.uniform(3))), 0.0);
	}
	
	@Test
	public void testPointLineDistance() {
		Assert.assertEquals(Math.sqrt(2.0)/2.0, Vector.pointLineDistance(new double[] { 0.0, 1.0 }, new double[] { 1.0, 1.0 }));
		Assert.assertEquals(Math.sqrt(2.0)/2.0, Vector.pointLineDistance(new double[] { 0.0, -1.0 }, new double[] { 1.0, 1.0 }));
		Assert.assertEquals(Math.sqrt(2.0)/2.0, Vector.pointLineDistance(new double[] { 0.0, 1.0 }, new double[] { 0.5, 0.5 }));
		Assert.assertEquals(Math.sqrt(2.0)/2.0, Vector.pointLineDistance(new double[] { 0.0, -1.0 }, new double[] { 0.5, 0.5 }));
		Assert.assertEquals(0.0, Vector.pointLineDistance(new double[] { 2.0, 2.0 }, new double[] { 1.0, 1.0 }));
		Assert.assertEquals(0.0, Vector.pointLineDistance(new double[] { 0.0, 0.0 }, new double[] { 1.0, 1.0 }));
		
		Assert.assertEquals(Math.sqrt(2.0), Vector.pointLineDistance(new double[] { 1.0, 1.0, 1.0 }, new double[] { 0.0, 0.0, 1.0 }));
		Assert.assertEquals(0.0, Vector.pointLineDistance(new double[] { 0.5, 0.5, 0.5 }, new double[] { 1.0, 1.0, 1.0 }));
	}
	
	@Test
	public void testPointLineDistanceWithMalformedLine() {
		Assert.assertEquals(Double.NaN, Vector.pointLineDistance(new double[] { 0.0, 2.0 }, new double[] { 0.0, 0.0 }));
	}

}
