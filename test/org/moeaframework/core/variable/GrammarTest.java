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
package org.moeaframework.core.variable;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestEnvironment;

@RunWith(CIRunner.class)
public class GrammarTest {

	private Grammar grammar;

	@Before
	public void setUp() {
		grammar = new Grammar(5);
		grammar.fromArray(new int[] { 0, 1, 2, 3, 4 });
	}

	@After
	public void tearDown() {
		grammar = null;
	}
	
	@Test
	public void testName() {
		Assert.assertNull(new Grammar(5).getName());
		Assert.assertEquals("foo", new Grammar("foo", 5).getName());
	}

	@Test
	public void testConstructor() {
		Grammar grammar = new Grammar(5);
		Assert.assertEquals(5, grammar.size());
	}

	@Test
	public void testSize() {
		Assert.assertEquals(5, grammar.size());
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(grammar.equals(null));
		Assert.assertTrue(grammar.equals(grammar));
		Assert.assertTrue(grammar.equals(grammar.copy()));
		Assert.assertFalse(grammar.equals(new Grammar(0)));
		Assert.assertFalse(grammar.equals(new Grammar(4)));
		Assert.assertFalse(grammar.equals(new Grammar(6)));
		
		Grammar other = new Grammar(5);
		other.fromArray(new int[] { 0, 2, 1, 3, 4 });
		Assert.assertFalse(grammar.equals(other));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(grammar.hashCode(), grammar.hashCode());
		Assert.assertEquals(grammar.hashCode(), grammar.copy().hashCode());
	}

	@Test
	public void testGet() {
		for (int i = 0; i < grammar.size(); i++) {
			Assert.assertEquals(i, grammar.get(i));
		}
	}

	@Test
	public void testSet() {
		for (int i = 0; i < grammar.size(); i++) {
			grammar.set(i, 9);
			Assert.assertEquals(9, grammar.get(i));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLargeValue() {
		grammar.set(1, grammar.getMaximumValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNegativeValue() {
		grammar.set(1, -1);
	}

	@Test
	public void testCopy() {
		Grammar copy = grammar.copy();
		Assert.assertNotSame(grammar, copy);
		Assert.assertEquals(grammar, copy);

		copy.set(1, 0);
		Assert.assertFalse(copy.equals(grammar));
		Assert.assertNotEquals(grammar, copy);
	}

	@Test
	public void testToArray() {
		int[] array = grammar.toArray();

		Assert.assertEquals(grammar.size(), array.length);
		for (int i = 0; i < grammar.size(); i++) {
			Assert.assertEquals(grammar.get(i), array[i]);
		}

		array[1] = 0;
		Assert.assertEquals(1, grammar.get(1));
	}

	@Test
	public void testFromArray() {
		int[] array = new int[] { 2, 1, 0 };
		grammar.fromArray(array);

		Assert.assertEquals(array.length, grammar.size());
		for (int i = 0; i < array.length; i++) {
			Assert.assertEquals(array[i], grammar.get(i));
		}

		array[1] = 0;
		Assert.assertEquals(1, grammar.get(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayInvalidValue1() {
		grammar.fromArray(new int[] { 0, 1, grammar.getMaximumValue() });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayInvalidValue2() {
		grammar.fromArray(new int[] { 0, 1, -1 });
	}

	@Test
	public void testCutAll() {
		int[] removed = grammar.cut(0, 4);

		Assert.assertArrayEquals(new int[] {}, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, removed);
	}

	@Test
	public void testCutHead() {
		int[] removed = grammar.cut(2, 4);

		Assert.assertArrayEquals(new int[] { 0, 1 }, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 2, 3, 4 }, removed);
	}

	@Test
	public void testCutTail() {
		int[] removed = grammar.cut(0, 2);

		Assert.assertArrayEquals(new int[] { 3, 4 }, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 0, 1, 2 }, removed);
	}

	@Test
	public void testInsertHead() {
		grammar.insert(0, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 5, 6, 0, 1, 2, 3, 4 }, grammar.toArray());
	}

	@Test
	public void testInsertTail() {
		grammar.insert(5, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, grammar.toArray());
	}

	@Test
	public void testInsertMiddle() {
		grammar.insert(2, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 0, 1, 5, 6, 2, 3, 4 }, grammar.toArray());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testCutOutOfBounds1() {
		grammar.cut(3, 5);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testCutOutOfBounds2() {
		grammar.cut(-1, 2);
	}

	@Test(expected = NegativeArraySizeException.class)
	public void testCutExceedsSize() {
		grammar.cut(0, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCutEndLessThanStart() {
		grammar.cut(4, 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertOutOfBounds1() {
		grammar.insert(6, new int[] { 0, 1 });
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertOutOfBounds2() {
		grammar.insert(-1, new int[] { 0, 1 });
	}
	
	@Test
	public void testToString() {
		grammar.fromArray(new int[] { 50, 10, 5, 0, 100 });
		Assert.assertEquals("Grammar(50,10,5,0,100)", grammar.toString());
	}
	
	@Test
	public void testEncodeDecode() {
		Grammar newVariable = new Grammar(5);
		newVariable.decode(grammar.encode());
		Assert.assertArrayEquals(grammar.toArray(), newVariable.toArray());
	}
	
	@Test
	@Retryable
	public void testRandomize() {
		grammar.setMaximumValue(5);
		DescriptiveStatistics[] valueStats = new DescriptiveStatistics[grammar.size()];
		
		for (int j = 0; j < grammar.size(); j++) {
			valueStats[j] = new DescriptiveStatistics();
		}
		
		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			grammar.randomize();
			
			for (int j = 0; j < grammar.size(); j++) {
				valueStats[j].addValue(grammar.get(j));
			}
		}
		
		for (int j = 0; j < grammar.size(); j++) {
			Assert.assertUniformDistribution(0, grammar.getMaximumValue()-1, valueStats[j]);
		}
	}
	
	@Test
	public void testDefinition() {
		Assert.assertEquals("Grammar(5)", new Grammar(5).getDefinition());
		Assert.assertEquals("Grammar(\"foo\",5)", new Grammar("foo", 5).getDefinition());
	}

}
