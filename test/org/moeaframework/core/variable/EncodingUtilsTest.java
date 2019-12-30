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
package org.moeaframework.core.variable;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

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
	
	@Test
	public void testIntEncoding() {
		Variable variable = EncodingUtils.newInt(3, 8);
		
		EncodingUtils.setReal(variable, 5.25);
		Assert.assertEquals(5, EncodingUtils.getInt(variable));
		
		EncodingUtils.setReal(variable, 8.999);
		Assert.assertEquals(8, EncodingUtils.getInt(variable));
	}
	
	@Test
	public void testBinaryIntEncoding() {
		Variable variable = EncodingUtils.newBinaryInt(3, 8);
		
		EncodingUtils.setInt(variable, 5);
		Assert.assertEquals(5, EncodingUtils.getInt(variable));
		
		EncodingUtils.setInt(variable, 8);
		Assert.assertEquals(8, EncodingUtils.getInt(variable));
	}
	
	@Test
	public void testBinaryEncoding() {
		Variable variable = EncodingUtils.newBinary(3);
		
		EncodingUtils.setBinary(variable, new boolean[] { false, false, true });
		BitSet bitSet = EncodingUtils.getBitSet(variable);
		
		Assert.assertFalse(bitSet.get(0));
		Assert.assertFalse(bitSet.get(1));
		Assert.assertTrue(bitSet.get(2));
		Assert.assertEquals(3, bitSet.length());
		
		bitSet.flip(0);
		bitSet.flip(2);
		EncodingUtils.setBitSet(variable, bitSet);
		boolean[] binary = EncodingUtils.getBinary(variable);
		
		Assert.assertEquals(3, binary.length);
		Assert.assertTrue(binary[0]);
		Assert.assertFalse(binary[1]);
		Assert.assertFalse(binary[2]);
	}
	
	@Test
	public void testBooleanEncoding() {
		Variable variable = EncodingUtils.newBoolean();
		
		EncodingUtils.setBoolean(variable, true);
		Assert.assertTrue(EncodingUtils.getBoolean(variable));
		
		EncodingUtils.setBoolean(variable, false);
		Assert.assertFalse(EncodingUtils.getBoolean(variable));
	}
	
	@Test
	public void testPermutationEncoding() {
		Variable variable = EncodingUtils.newPermutation(3);
		
		EncodingUtils.setPermutation(variable, new int[] { 0, 2, 1 });
		Assert.assertArrayEquals(new int[] { 0, 2, 1 },
				EncodingUtils.getPermutation(variable));
	}
	
	@Test
	public void testSubsetEncoding() {
		Variable variable = EncodingUtils.newSubset(5, 10);
		
		EncodingUtils.setSubset(variable, new int[] { 1, 3, 5, 6, 7 });
		Assert.assertArrayEquals(new int[] { 1, 3, 5, 6, 7 },
				EncodingUtils.getSubset(variable));
	}
	
	@Test
	public void testSubsetEncodingAsBinary() {
		Variable variable = EncodingUtils.newSubset(5, 10);
		boolean[] values = new boolean[] { false, false, true, true,
				false, true, false, true, true, false};
		
		EncodingUtils.setSubset(variable, values);
		Assert.assertArrayEquals(new int[] { 2, 3, 5, 7, 8 },
				EncodingUtils.getSubset(variable));
		
		TestUtils.assertEquals(values, EncodingUtils.getSubsetAsBinary(variable));
	}
	
	@Test
	public void testSubsetEncodingAsBitSet() {
		Variable variable = EncodingUtils.newSubset(5, 10);
		
		BitSet bitSet = new BitSet(10);
		bitSet.set(2);
		bitSet.set(3);
		bitSet.set(5);
		bitSet.set(7);
		bitSet.set(8);
		
		EncodingUtils.setSubset(variable, bitSet);
		Assert.assertArrayEquals(new int[] { 2, 3, 5, 7, 8 },
				EncodingUtils.getSubset(variable));
		Assert.assertEquals(bitSet, EncodingUtils.getSubsetAsBitSet(variable));
	}
	
	@Test
	public void testRealArrayEncoding() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newReal(2.0, 4.0));
		solution.setVariable(2, EncodingUtils.newReal(-1.0, 1.0));
		
		EncodingUtils.setReal(solution, new double[] { 0.5, 3.0, 0.0 });
		Assert.assertArrayEquals(new double[] { 0.5, 3.0, 0.0 },
				EncodingUtils.getReal(solution), Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 3.0, 0.0 }, 
				EncodingUtils.getReal(solution, 1, 3), Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 3.0 },
				EncodingUtils.getReal(solution, 1, 2), Settings.EPS);
		
		Assert.assertArrayEquals(new double[0],
				EncodingUtils.getReal(solution, 1, 1), Settings.EPS);
		
		EncodingUtils.setReal(solution, 1, 3, new double[] { 2.0, -1.0 });
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, -1.0 },
				EncodingUtils.getReal(solution), Settings.EPS);
		
		EncodingUtils.setReal(solution, 2, 3, new double[] { 1.0 });
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, 1.0 },
				EncodingUtils.getReal(solution), Settings.EPS);
		
		EncodingUtils.setReal(solution, 2, 2, new double[0]);
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, 1.0 },
				EncodingUtils.getReal(solution), Settings.EPS);
	}
	
	@Test
	public void testIntArrayEncoding() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newInt(0, 1));
		solution.setVariable(1, EncodingUtils.newInt(2, 4));
		solution.setVariable(2, EncodingUtils.newInt(-1, 1));
		
		EncodingUtils.setInt(solution, new int[] { 0, 3, 0 });
		Assert.assertArrayEquals(new int[] { 0, 3, 0 },
				EncodingUtils.getInt(solution));
		
		Assert.assertArrayEquals(new int[] { 3, 0 }, 
				EncodingUtils.getInt(solution, 1, 3));
		
		Assert.assertArrayEquals(new int[] { 3 },
				EncodingUtils.getInt(solution, 1, 2));
		
		Assert.assertArrayEquals(new int[0],
				EncodingUtils.getInt(solution, 1, 1));
		
		EncodingUtils.setInt(solution, 1, 3, new int[] { 2, -1 });
		
		Assert.assertArrayEquals(new int[] { 0, 2, -1 },
				EncodingUtils.getInt(solution));
		
		EncodingUtils.setInt(solution, 2, 3, new int[] { 1 });
		
		Assert.assertArrayEquals(new int[] { 0, 2, 1 },
				EncodingUtils.getInt(solution));
		
		EncodingUtils.setInt(solution, 2, 2, new int[0]);
		
		Assert.assertArrayEquals(new int[] { 0, 2, 1 },
				EncodingUtils.getInt(solution));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealInvalidType1() {
		EncodingUtils.setReal(EncodingUtils.newBinary(3), 1.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntInvalidType1() {
		EncodingUtils.setInt(EncodingUtils.newBinary(3), 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBinaryInvalidType1() {
		EncodingUtils.setBinary(EncodingUtils.newReal(0.0, 1.0),
				new boolean[] { false, false, true });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBitSetInvalidType1() {
		EncodingUtils.setBitSet(EncodingUtils.newReal(0.0, 1.0), new BitSet(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBooleanInvalidType1() {
		EncodingUtils.setBoolean(EncodingUtils.newReal(0.0, 1.0), true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPermutationInvalidType1() {
		EncodingUtils.setPermutation(EncodingUtils.newBinary(3),
				new int[] { 0, 2, 1 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidType1() {
		EncodingUtils.setSubset(EncodingUtils.newBinary(3),
				new int[] { 1, 3, 5, 6, 7 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealInvalidType2() {
		EncodingUtils.getReal(EncodingUtils.newBinary(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntInvalidType2() {
		EncodingUtils.getInt(EncodingUtils.newBinary(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBinaryInvalidType2() {
		EncodingUtils.getBinary(EncodingUtils.newReal(0.0, 1.0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBitSetInvalidType2() {
		EncodingUtils.getBitSet(EncodingUtils.newReal(0.0, 1.0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBooleanInvalidType2() {
		EncodingUtils.getBoolean(EncodingUtils.newReal(0.0, 1.0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPermutationInvalidType2() {
		EncodingUtils.getPermutation(EncodingUtils.newBinary(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidType2() {
		EncodingUtils.getSubset(EncodingUtils.newBinary(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBinaryInvalidNumberOfBits1() {
		Variable variable = EncodingUtils.newBinary(2);
		EncodingUtils.setBinary(variable, new boolean[] { false, false, true });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBinaryInvalidNumberOfBits2() {
		Variable variable = EncodingUtils.newBinary(2);
		EncodingUtils.setBinary(variable, new boolean[] { false });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBooleanInvalidNumberOfBits1() {
		Variable variable = EncodingUtils.newBinary(2);
		EncodingUtils.getBoolean(variable);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBooleanInvalidNumberOfBits2() {
		Variable variable = EncodingUtils.newBinary(2);
		EncodingUtils.setBoolean(variable, true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealArrayInvalidType1() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newBinary(2));
		solution.setVariable(2, EncodingUtils.newReal(-1.0, 1.0));
		
		EncodingUtils.setReal(solution, new double[] { 0.0, 0.0, 0.0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealArrayInvalidType2() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newBinary(2));
		solution.setVariable(2, EncodingUtils.newReal(-1.0, 1.0));
		
		EncodingUtils.getReal(solution);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntArrayInvalidType1() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newInt(0, 1));
		solution.setVariable(1, EncodingUtils.newBinary(2));
		solution.setVariable(2, EncodingUtils.newInt(-1, 1));
		
		EncodingUtils.setInt(solution, new int[] { 0, 0, 0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntArrayInvalidType2() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, EncodingUtils.newInt(0, 1));
		solution.setVariable(1, EncodingUtils.newBinary(2));
		solution.setVariable(2, EncodingUtils.newInt(-1, 1));
		
		EncodingUtils.getInt(solution);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealArrayInvalidLength1() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newReal(0.0, 1.0));
		EncodingUtils.setReal(solution, new double[] { 0.25 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRealArrayInvalidLength2() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newReal(0.0, 1.0));
		EncodingUtils.setReal(solution, new double[] { 0.25, 0.75, 0.5 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntArrayInvalidLength1() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, EncodingUtils.newInt(0, 1));
		solution.setVariable(1, EncodingUtils.newInt(0, 1));
		EncodingUtils.setInt(solution, new int[] { 0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntArrayInvalidLength2() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, EncodingUtils.newInt(0, 1));
		solution.setVariable(1, EncodingUtils.newInt(0, 1));
		EncodingUtils.setInt(solution, new int[] { 0, 1, 0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidLength1() {
		Subset subset = new Subset(5, 10);
		EncodingUtils.setSubset(subset, new int[] { 2, 4 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidLength2() {
		Subset subset = new Subset(5, 10);
		EncodingUtils.setSubset(subset, new boolean[] { false, false, true, true, false });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidLength3() {
		Subset subset = new Subset(5, 10);
		
		BitSet bitSet = new BitSet(5);
		bitSet.set(2);
		bitSet.set(3);
		
		EncodingUtils.setSubset(subset, bitSet);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidValue1() {
		Subset subset = new Subset(5, 10);
		EncodingUtils.setSubset(subset, new int[] { 0, 1, 2, 3, 10 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidValue2() {
		Subset subset = new Subset(5, 10);
		EncodingUtils.setSubset(subset, new int[] { -1, 0, 1, 2, 3 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidValue3() {
		Subset subset = new Subset(5, 10);
		EncodingUtils.setSubset(subset, new boolean[] { false, false, true, true,
				true, true, false, false, false, false, true });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubsetInvalidValue4() {
		Subset subset = new Subset(5, 10);
		
		BitSet bitSet = new BitSet(5);
		bitSet.set(2);
		bitSet.set(3);
		bitSet.set(4);
		bitSet.set(5);
		bitSet.set(10);
		
		EncodingUtils.setSubset(subset, bitSet);
	}

}
