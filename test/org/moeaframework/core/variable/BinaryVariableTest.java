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
import java.util.BitSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;

@RunWith(CIRunner.class)
public class BinaryVariableTest {

	private BinaryVariable variable;

	@Before
	public void setUp() {
		variable = new BinaryVariable(2);
		variable.set(1, true);
	}

	@After
	public void tearDown() {
		variable = null;
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("01", variable.toString());
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(2, variable.getNumberOfBits());
		Assert.assertFalse(variable.get(0));
		Assert.assertTrue(variable.get(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueBoundsCheckLower() {
		variable.get(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueBoundsCheckUpper() {
		variable.get(3);
	}

	@Test
	public void testSetValue() {
		variable.set(0, true);
		variable.set(1, false);

		Assert.assertTrue(variable.get(0));
		Assert.assertFalse(variable.get(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetValueBoundsCheckLower() {
		variable.set(-1, false);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetValueBoundsCheckUpper() {
		variable.set(3, false);
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(variable.equals(null));
		Assert.assertTrue(variable.equals(variable));

		BinaryVariable trueCase = new BinaryVariable(2);
		trueCase.set(1, true);
		Assert.assertTrue(variable.equals(trueCase));

		BinaryVariable falseCase1 = new BinaryVariable(2);
		falseCase1.set(0, true);
		Assert.assertFalse(variable.equals(falseCase1));

		BinaryVariable falseCase2 = new BinaryVariable(3);
		falseCase2.set(1, true);
		Assert.assertFalse(variable.equals(falseCase2));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(variable.hashCode(), variable.hashCode());
		
		BinaryVariable bv = new BinaryVariable(2);
		bv.set(1, true);
		Assert.assertEquals(variable.hashCode(), bv.hashCode());
	}

	@Test
	public void testCopy() {
		BinaryVariable copy = variable.copy();
		Assert.assertTrue(copy.equals(variable));

		copy.set(1, false);
		Assert.assertTrue(variable.get(1));
		Assert.assertFalse(copy.equals(variable));
	}

	@Test
	public void testClear() {
		variable.clear();
		Assert.assertEquals(2, variable.getNumberOfBits());
		Assert.assertEquals(0, variable.cardinality());
	}

	@Test
	public void testIsEmpty() {
		Assert.assertFalse(variable.isEmpty());

		variable.clear();
		Assert.assertTrue(variable.isEmpty());
	}

	@Test
	public void testCardinality() {
		Assert.assertEquals(1, variable.cardinality());

		variable.clear();
		Assert.assertEquals(0, variable.cardinality());
	}

	@Test
	public void testGetBitSet() {
		BitSet bitSet = variable.getBitSet();

		Assert.assertEquals(2, bitSet.length());
		Assert.assertFalse(bitSet.get(0));
		Assert.assertTrue(bitSet.get(1));

		// ensure the returned BitSet is independent of the BinaryVariable
		bitSet.set(0);
		Assert.assertFalse(variable.get(0));
	}
	
	@Test
	public void testHammingDistance() {
		BinaryVariable b1 = new BinaryVariable(5);
		BinaryVariable b2 = new BinaryVariable(5);
		
		b1.set(2, true);
		b1.set(4, true);
		b2.set(2, true);
		b2.set(3, true);
		
		Assert.assertEquals(2, b1.hammingDistance(b2));
		Assert.assertEquals(0, b1.hammingDistance(b1));
		Assert.assertEquals(0, b2.hammingDistance(b2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHammingDistanceLength() {
		BinaryVariable b1 = new BinaryVariable(5);
		BinaryVariable b2 = new BinaryVariable(6);
		
		b1.hammingDistance(b2);
	}
	
	@Test
	public void testEncodeDecode() {
		BinaryVariable newVariable = new BinaryVariable(2);
		newVariable.decode(variable.encode());
		Assert.assertEquals(variable.getBitSet(), newVariable.getBitSet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidBinary1() throws IOException {
		BinaryVariable bv = new BinaryVariable(5);
		bv.decode("001");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidBinary2() throws IOException {
		BinaryVariable bv = new BinaryVariable(5);
		bv.decode("00200");
	}
	
	@Test
	@Retryable
	public void testRandomize() {
		DescriptiveStatistics[] bitStats = new DescriptiveStatistics[variable.getNumberOfBits()];
		
		for (int j = 0; j < variable.getNumberOfBits(); j++) {
			bitStats[j] = new DescriptiveStatistics();
		}
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			variable.randomize();
			
			for (int j = 0; j < variable.getNumberOfBits(); j++) {
				bitStats[j].addValue(variable.get(j) ? 1 : 0);
			}
		}
				
		for (int j = 0; j < variable.getNumberOfBits(); j++) {
			TestUtils.assertUniformDistribution(0, 1, bitStats[j]);
		}
	}
	
}
