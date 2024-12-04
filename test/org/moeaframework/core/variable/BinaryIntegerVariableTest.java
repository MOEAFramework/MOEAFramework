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
package org.moeaframework.core.variable;

import java.util.BitSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;

public class BinaryIntegerVariableTest {

	private BinaryIntegerVariable variable;

	@Before
	public void setUp() {
		variable = new BinaryIntegerVariable(7, 5, 10);
	}

	@After
	public void tearDown() {
		variable = null;
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(7, variable.getValue());
		Assert.assertEquals(5, variable.getLowerBound());
		Assert.assertEquals(10, variable.getUpperBound());
	}

	@Test
	public void testSetValue() {
		for (int i = variable.getLowerBound(); i <= variable.getUpperBound(); i++) {
			variable.setValue(i);
			Assert.assertEquals(i, variable.getValue());
		}
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(variable.equals(null));
		Assert.assertTrue(variable.equals(variable));
		Assert.assertTrue(variable.equals(new BinaryIntegerVariable(7, 5, 10)));
		Assert.assertFalse(variable.equals(new BinaryIntegerVariable(9, 5, 10)));
		Assert.assertFalse(variable.equals(new BinaryIntegerVariable(7, 2, 10)));
		Assert.assertFalse(variable.equals(new BinaryIntegerVariable(7, 5, 9)));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(variable.hashCode(), variable.hashCode());
		Assert.assertEquals(variable.hashCode(), new BinaryIntegerVariable(7, 5, 10).hashCode());
	}

	@Test
	public void testCopy() {
		BinaryIntegerVariable copy = variable.copy();
		Assert.assertNotSame(variable, copy);
		Assert.assertEquals(variable, copy);
		Assert.assertEquals(variable.getBitSet(), copy.getBitSet());

		copy.setValue(9);
		Assert.assertEquals(7, variable.getValue());
		Assert.assertNotEquals(variable, copy);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckLower() {
		new BinaryIntegerVariable(-1, 0, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckUpper() {
		new BinaryIntegerVariable(11, 0, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckLower() {
		variable.setValue(4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckUpper() {
		variable.setValue(11);
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("7", variable.toString());
	}
	
	@Test
	public void testEncodeDecode() {
		BinaryIntegerVariable newVariable = new BinaryIntegerVariable(5, 10);
		newVariable.decode(variable.encode());
		Assert.assertEquals(variable.getValue(), newVariable.getValue());
	}
	
	@Test
	public void testRandomize() {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			variable.randomize();
			stats.addValue(variable.getValue());
		}
		
		Assert.assertUniformDistribution(variable.getLowerBound(), variable.getUpperBound(), stats);
	}
	
	@Test
	public void testBinaryEncoding() {
		Consumer<Number> test = value -> {
			BitSet bits = BinaryIntegerVariable.encode(value.longValue());
			String expectedString = Long.toBinaryString(value.longValue());
			
			Assert.assertTrue(bits.length() <= expectedString.length());
			
			for (int i = 0; i < expectedString.length(); i++) {
				Assert.assertEquals("bit at index " + i + " differ",
						expectedString.charAt(expectedString.length() - i - 1) == '1', bits.get(i));
			}
		};
		
		test.accept(0);
		test.accept(1);
		test.accept(2);
		test.accept(3);
		test.accept(4);
		test.accept(5);
		test.accept(6);
		test.accept(7);
		test.accept(-1);
		test.accept(Integer.MAX_VALUE);
		test.accept(Integer.MIN_VALUE);
		test.accept(Long.MAX_VALUE);
		test.accept(Long.MIN_VALUE);
	}

	@Test
	public void testBinaryDecoding() {
		Consumer<Number> test = value -> {
			BitSet bits = BinaryIntegerVariable.encode(value.longValue());
			Assert.assertEquals(value.longValue(), BinaryIntegerVariable.decode(bits));
		};
		
		test.accept(0);
		test.accept(1);
		test.accept(2);
		test.accept(3);
		test.accept(4);
		test.accept(5);
		test.accept(6);
		test.accept(7);
		test.accept(-1);
		test.accept(Integer.MAX_VALUE);
		test.accept(Integer.MIN_VALUE);
		test.accept(Long.MAX_VALUE);
		test.accept(Long.MIN_VALUE);
	}

	@Test
	public void testBinaryToGrayConversion() {
		BiConsumer<Number, String> test = (value, expectedString) -> {
			BitSet bits = BinaryIntegerVariable.encode(value.longValue());
			bits = BinaryIntegerVariable.binaryToGray(bits);
			
			Assert.assertTrue(bits.length() <= expectedString.length());
			
			for (int i = 0; i < expectedString.length(); i++) {
				Assert.assertEquals("bit at index " + i + " differ",
						expectedString.charAt(expectedString.length() - i - 1) == '1', bits.get(i));
			}
		};
		
		test.accept(0, "000");
		test.accept(1, "001");
		test.accept(2, "011");
		test.accept(3, "010");
		test.accept(4, "110");
		test.accept(5, "111");
		test.accept(6, "101");
		test.accept(7, "100");
	}

	@Test
	public void testGrayToBinaryEncoding() {
		Consumer<Number> test = value -> {
			BitSet bits = BinaryIntegerVariable.encode(value.longValue());
			bits = BinaryIntegerVariable.binaryToGray(bits);
			bits = BinaryIntegerVariable.grayToBinary(bits);
			Assert.assertEquals(value.longValue(), BinaryIntegerVariable.decode(bits));
		};
		
		test.accept(0);
		test.accept(1);
		test.accept(2);
		test.accept(3);
		test.accept(4);
		test.accept(5);
		test.accept(6);
		test.accept(7);
		test.accept(-1);
		test.accept(Integer.MAX_VALUE);
		test.accept(Integer.MIN_VALUE);
		test.accept(Long.MAX_VALUE);
		test.accept(Long.MIN_VALUE);
	}
	
	@Test
	public void testSolutionEncoding() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new BinaryIntegerVariable(0, 1));
		solution.setVariable(1, new BinaryIntegerVariable(2, 4));
		solution.setVariable(2, new BinaryIntegerVariable(-1, 1));
		
		BinaryIntegerVariable.setInt(solution, new int[] { 0, 3, 0 });
		Assert.assertArrayEquals(new int[] { 0, 3, 0 }, BinaryIntegerVariable.getInt(solution));
		
		Assert.assertArrayEquals(new int[] { 3, 0 }, BinaryIntegerVariable.getInt(solution, 1, 3));
		
		Assert.assertArrayEquals(new int[] { 3 }, BinaryIntegerVariable.getInt(solution, 1, 2));
		
		Assert.assertArrayEquals(new int[0], BinaryIntegerVariable.getInt(solution, 1, 1));
		
		BinaryIntegerVariable.setInt(solution, 1, 3, new int[] { 2, -1 });
		
		Assert.assertArrayEquals(new int[] { 0, 2, -1 }, BinaryIntegerVariable.getInt(solution));
		
		BinaryIntegerVariable.setInt(solution, 2, 3, new int[] { 1 });
		
		Assert.assertArrayEquals(new int[] { 0, 2, 1 }, BinaryIntegerVariable.getInt(solution));
		
		BinaryIntegerVariable.setInt(solution, 2, 2, new int[0]);
		
		Assert.assertArrayEquals(new int[] { 0, 2, 1 }, BinaryIntegerVariable.getInt(solution));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidType() {
		BinaryIntegerVariable.setInt(new BinaryVariable(3), 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetInvalidType() {
		BinaryIntegerVariable.getInt(new BinaryVariable(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetSolutionInvalidType() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new BinaryIntegerVariable(0, 1));
		solution.setVariable(1, new BinaryVariable(2));
		solution.setVariable(2, new BinaryIntegerVariable(-1, 1));
		
		BinaryIntegerVariable.setInt(solution, new int[] { 0, 0, 0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetSolutionInvalidType() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new BinaryIntegerVariable(0, 1));
		solution.setVariable(1, new BinaryVariable(2));
		solution.setVariable(2, new BinaryIntegerVariable(-1, 1));
		
		BinaryIntegerVariable.getInt(solution);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooFewValues() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new BinaryIntegerVariable(0, 1));
		solution.setVariable(1, new BinaryIntegerVariable(0, 1));
		BinaryIntegerVariable.setInt(solution, new int[] { 0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooManyValues() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new BinaryIntegerVariable(0, 1));
		solution.setVariable(1, new BinaryIntegerVariable(0, 1));
		BinaryIntegerVariable.setInt(solution, new int[] { 0, 1, 0 });
	}

}
