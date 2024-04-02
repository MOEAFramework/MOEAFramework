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

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Settings;

public class RealVariableTest {

	private RealVariable variable;

	@Before
	public void setUp() {
		variable = new RealVariable(0.5, 0.0, 1.0);
	}

	@After
	public void tearDown() {
		variable = null;
	}
	
	@Test
	public void testEmptyConstructor() {
		Assert.assertTrue(Double.isNaN(new RealVariable(0.0, 1.0).getValue()));
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(0.5, variable.getValue(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, variable.getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, variable.getUpperBound(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetValue() {
		variable.setValue(0.75);
		Assert.assertEquals(0.75, variable.getValue(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(variable.equals(null));
		Assert.assertTrue(variable.equals(variable));
		Assert.assertTrue(variable.equals(new RealVariable(0.5, 0.0, 1.0)));
		Assert.assertFalse(variable.equals(new RealVariable(0.75, 0.0, 1.0)));
		Assert.assertFalse(variable.equals(new RealVariable(0.5, 0.25, 1.0)));
		Assert.assertFalse(variable.equals(new RealVariable(0.5, 0.0, 0.75)));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(variable.hashCode(), variable.hashCode());
		Assert.assertEquals(variable.hashCode(), new RealVariable(0.5, 0.0, 1.0).hashCode());
	}

	@Test
	public void testCopy() {
		RealVariable copy = variable.copy();
		Assert.assertNotSame(variable, copy);
		Assert.assertEquals(variable, copy);

		copy.setValue(0.75);
		Assert.assertEquals(0.5, variable.getValue(), TestThresholds.HIGH_PRECISION);
		Assert.assertNotEquals(variable, copy);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckLower() {
		new RealVariable(0.0 - Settings.EPS, 0.0, 1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckUpper() {
		new RealVariable(1.0 + Settings.EPS, 0.0, 1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckLower() {
		variable.setValue(variable.getLowerBound() - Settings.EPS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckUpper() {
		variable.setValue(variable.getUpperBound() + Settings.EPS);
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("0.5", variable.toString());
	}
	
	@Test
	public void testEncodeDecode() {
		RealVariable newVariable = new RealVariable(0.0, 1.0);
		newVariable.decode(variable.encode());
		Assert.assertEquals(variable.getValue(), newVariable.getValue(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testDecodeInvalidReal() throws IOException {
		RealVariable rv = new RealVariable(0.0, 1.0);
		rv.decode("0.5foo");
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
