/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.core.variable;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;

public class EncodingUtilsTest {

	@Test
	public void test3BitBinaryEncoding() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(0, variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(1, variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(2, variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(3, variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(4, variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(5, variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(6, variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(7, variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertTrue(variable.get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBinaryEncodingRangeCheck() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(8, variable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBinaryEncodingValueCheck() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(-1, variable);
	}

	@Test
	public void test3BitBinaryDecoding() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(0, variable);
		Assert.assertEquals(0, EncodingUtils.decode(variable));

		EncodingUtils.encode(1, variable);
		Assert.assertEquals(1, EncodingUtils.decode(variable));

		EncodingUtils.encode(2, variable);
		Assert.assertEquals(2, EncodingUtils.decode(variable));

		EncodingUtils.encode(3, variable);
		Assert.assertEquals(3, EncodingUtils.decode(variable));

		EncodingUtils.encode(4, variable);
		Assert.assertEquals(4, EncodingUtils.decode(variable));

		EncodingUtils.encode(5, variable);
		Assert.assertEquals(5, EncodingUtils.decode(variable));

		EncodingUtils.encode(6, variable);
		Assert.assertEquals(6, EncodingUtils.decode(variable));

		EncodingUtils.encode(7, variable);
		Assert.assertEquals(7, EncodingUtils.decode(variable));
	}

	@Test
	public void test3BitBinaryToGrayConversion() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(0, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(1, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(2, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(3, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertFalse(variable.get(2));

		EncodingUtils.encode(4, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(5, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertTrue(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(6, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertTrue(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertTrue(variable.get(2));

		EncodingUtils.encode(7, variable);
		EncodingUtils.binaryToGray(variable);
		Assert.assertFalse(variable.get(0));
		Assert.assertFalse(variable.get(1));
		Assert.assertTrue(variable.get(2));
	}

	@Test
	public void test3BitGrayToBinaryEncoding() {
		BinaryVariable variable = new BinaryVariable(3);

		EncodingUtils.encode(0, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(0, EncodingUtils.decode(variable));

		EncodingUtils.encode(1, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(1, EncodingUtils.decode(variable));

		EncodingUtils.encode(2, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(2, EncodingUtils.decode(variable));

		EncodingUtils.encode(3, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(3, EncodingUtils.decode(variable));

		EncodingUtils.encode(4, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(4, EncodingUtils.decode(variable));

		EncodingUtils.encode(5, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(5, EncodingUtils.decode(variable));

		EncodingUtils.encode(6, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(6, EncodingUtils.decode(variable));

		EncodingUtils.encode(7, variable);
		EncodingUtils.binaryToGray(variable);
		EncodingUtils.grayToBinary(variable);
		Assert.assertEquals(7, EncodingUtils.decode(variable));
	}

	@Test
	public void testDoubleToBinaryEncoding() {
		double lowerBound = -25.0;
		double upperBound = 50.0;
		int numberOfBits = 10;

		RealVariable doubleVariable = new RealVariable(lowerBound, upperBound);
		BinaryVariable binaryVariable = new BinaryVariable(numberOfBits);

		double diff = (upperBound - lowerBound)
				/ (2.0 * ((1L << numberOfBits) - 1));

		for (double i = lowerBound; i <= upperBound; i += 0.333) {
			doubleVariable.setValue(i);

			// the first encoding/decoding can introduce error bounded by diff
			EncodingUtils.encode(doubleVariable, binaryVariable);
			EncodingUtils.decode(binaryVariable, doubleVariable);

			double value = doubleVariable.getValue();
			Assert.assertEquals(i, value, diff);

			// ensure a second encoding/decoding returns the exact value
			EncodingUtils.encode(doubleVariable, binaryVariable);
			EncodingUtils.decode(binaryVariable, doubleVariable);

			Assert.assertEquals(value, doubleVariable.getValue(), Settings.EPS);
		}
	}

}
