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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

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

}
