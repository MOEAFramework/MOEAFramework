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

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

public class RealVariableTest {

	private RealVariable variable;

	@Before
	public void setUp() {
		variable = new RealVariable(0.0, 1.0).withValue(0.5);
	}

	@After
	public void tearDown() {
		variable = null;
	}
	
	@Test
	public void testDefaultValue() {
		Assert.assertTrue(Double.isNaN(new RealVariable(0.0, 1.0).getValue()));
	}

	@Test
	public void testName() {
		Assert.assertNull(new RealVariable(0.0, 1.0).getName());
		Assert.assertEquals("foo", new RealVariable("foo", 0.0, 1.0).getName());
	}
	
	@Test
	public void testGetValue() {
		Assert.assertEquals(0.5, variable.getValue(), TestEnvironment.HIGH_PRECISION);
		Assert.assertEquals(0.0, variable.getLowerBound(), TestEnvironment.HIGH_PRECISION);
		Assert.assertEquals(1.0, variable.getUpperBound(), TestEnvironment.HIGH_PRECISION);
	}

	@Test
	public void testSetValue() {
		variable.setValue(0.75);
		Assert.assertEquals(0.75, variable.getValue(), TestEnvironment.HIGH_PRECISION);
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(variable.equals(null));
		Assert.assertTrue(variable.equals(variable));
		Assert.assertTrue(variable.equals(new RealVariable(0.0, 1.0).withValue(0.5)));
		Assert.assertFalse(variable.equals(new RealVariable(0.0, 1.0).withValue(0.75)));
		Assert.assertFalse(variable.equals(new RealVariable(0.25, 1.0).withValue(0.5)));
		Assert.assertFalse(variable.equals(new RealVariable(0.0, 0.75).withValue(0.5)));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(variable.hashCode(), variable.hashCode());
		Assert.assertEquals(variable.hashCode(), new RealVariable(0.0, 1.0).withValue(0.5).hashCode());
	}

	@Test
	public void testCopy() {
		RealVariable copy = variable.copy();
		Assert.assertNotSame(variable, copy);
		Assert.assertEquals(variable, copy);

		copy.setValue(0.75);
		Assert.assertEquals(0.5, variable.getValue(), TestEnvironment.HIGH_PRECISION);
		Assert.assertNotEquals(variable, copy);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidBounds() {
		new RealVariable(1.0, 0.0);
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
		Assert.assertEquals(variable.getValue(), newVariable.getValue(), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testDecodeInvalidReal() throws IOException {
		RealVariable rv = new RealVariable(0.0, 1.0);
		rv.decode("0.5foo");
	}
	
	@Test
	public void testRandomize() {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			variable.randomize();
			stats.addValue(variable.getValue());
		}
		
		Assert.assertUniformDistribution(variable.getLowerBound(), variable.getUpperBound(), stats);
	}
	
	@Test
	public void testSolutionEncoding() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(2.0, 4.0));
		solution.setVariable(2, new RealVariable(-1.0, 1.0));
		
		RealVariable.setReal(solution, new double[] { 0.5, 3.0, 0.0 });
		Assert.assertArrayEquals(new double[] { 0.5, 3.0, 0.0 }, RealVariable.getReal(solution), TestEnvironment.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 3.0, 0.0 }, RealVariable.getReal(solution, 1, 3), TestEnvironment.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 3.0 }, RealVariable.getReal(solution, 1, 2), TestEnvironment.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[0], RealVariable.getReal(solution, 1, 1), TestEnvironment.HIGH_PRECISION);
		
		RealVariable.setReal(solution, 1, 3, new double[] { 2.0, -1.0 });
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, -1.0 }, RealVariable.getReal(solution), TestEnvironment.HIGH_PRECISION);
		
		RealVariable.setReal(solution, 2, 3, new double[] { 1.0 });
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, 1.0 }, RealVariable.getReal(solution), TestEnvironment.HIGH_PRECISION);
		
		RealVariable.setReal(solution, 2, 2, new double[0]);
		
		Assert.assertArrayEquals(new double[] { 0.5, 2.0, 1.0 }, RealVariable.getReal(solution), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidType() {
		RealVariable.setReal(new BinaryVariable(3), 1.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetInvalidType() {
		RealVariable.getReal(new BinaryVariable(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetSolutionInvalidType() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new BinaryVariable(2));
		solution.setVariable(2, new RealVariable(-1.0, 1.0));
		
		RealVariable.setReal(solution, new double[] { 0.0, 0.0, 0.0 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetSolutionInvalidType() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new BinaryVariable(2));
		solution.setVariable(2, new RealVariable(-1.0, 1.0));
		
		RealVariable.getReal(solution);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooFewValues() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		RealVariable.setReal(solution, new double[] { 0.25 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooManyValues() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		RealVariable.setReal(solution, new double[] { 0.25, 0.75, 0.5 });
	}
	
	@Test
	public void testDefinition() {
		Assert.assertEquals("RealVariable(0.0,1.0)", new RealVariable(0.0, 1.0).getDefinition());
		Assert.assertEquals("RealVariable(\"foo\",0.0,1.0)", new RealVariable("foo", 0.0, 1.0).getDefinition());
	}

}
