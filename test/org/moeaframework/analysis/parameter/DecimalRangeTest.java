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
package org.moeaframework.analysis.parameter;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;

public class DecimalRangeTest {
	
	@Test
	public void testParse() {
		DecimalRange parameter = new DecimalRange("foo", 100.0, 1000.0);
		Assert.assertEquals(500.5, parameter.parse("500.5"));
	}
	
	@Test
	public void testParseOutOfBounds() {
		DecimalRange parameter = new DecimalRange("foo", 100.0, 1000.0);
		
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("99.9"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("1000.1"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("foo"));
	}

	@Test
	public void testApply() {
		Sample sample = new Sample();
		DecimalRange parameter = new DecimalRange("foo", 100.0, 1000.0);
		
		parameter.apply(sample, 0.0);
		Assert.assertEquals(100.0, parameter.readValue(sample));
		
		parameter.apply(sample, 1.0);
		Assert.assertEquals(1000.0, parameter.readValue(sample));
		
		parameter.apply(sample, 0.5);
		Assert.assertEquals(550.0, parameter.readValue(sample));
	}
	
	@Test
	public void testApplyOutOfBounds() {
		Sample sample = new Sample();
		DecimalRange parameter = new DecimalRange("foo", 100.0, 1000.0);
		
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, -0.001));
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, 1.001));
	}
	
	@Test
	public void testDecode() {
		Tokenizer tokenizer = new Tokenizer();
		DecimalRange parameter = DecimalRange.decode(tokenizer, "foo decimal 100.0 1000.0");
		
		Assert.assertEquals("foo", parameter.getName());
		Assert.assertEquals(100.0, parameter.getLowerBound());
		Assert.assertEquals(1000.0, parameter.getUpperBound());
	}
	
	@Test
	public void testDecodeInvalid() {
		Tokenizer tokenizer = new Tokenizer();
		
		Assert.assertThrows(InvalidParameterException.class, () -> DecimalRange.decode(tokenizer, "foo"));
		Assert.assertThrows(InvalidParameterException.class, () -> DecimalRange.decode(tokenizer, "foo decimal"));
		Assert.assertThrows(InvalidParameterException.class, () -> DecimalRange.decode(tokenizer, "foo decimal 100.0"));
		Assert.assertThrows(InvalidParameterException.class, () -> DecimalRange.decode(tokenizer, "foo decimal 100.0 1000.0 10.0"));
		Assert.assertThrows(InvalidParameterException.class, () -> DecimalRange.decode(tokenizer, "foo unexpected 100.0 1000.0"));
	}
	
	@Test
	public void testEncode() {
		Tokenizer tokenizer = new Tokenizer();
		DecimalRange parameter = new DecimalRange("foo", 100.0, 1000.0);
		
		Assert.assertEquals("foo decimal 100.0 1000.0", parameter.encode(tokenizer));
	}
	
	@Test
	public void testApplyPrecision() {
		Assert.assertEquals(0.0, DecimalRange.applyPrecision(0.000000000000001), 0.0000000000000001);
		Assert.assertEquals(0.5555555556, DecimalRange.applyPrecision(0.555555555555555), 0.0000000000000001);
		Assert.assertEquals(1.0, DecimalRange.applyPrecision(0.999999999999999), 0.0000000000000001);
	}

}