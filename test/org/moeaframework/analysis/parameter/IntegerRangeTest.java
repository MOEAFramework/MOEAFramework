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

public class IntegerRangeTest {
	
	@Test
	public void testParse() {
		IntegerRange parameter = new IntegerRange("foo", 100, 1000);
		Assert.assertEquals(500, parameter.parse("500"));
	}
	
	@Test
	public void testParseOutOfBounds() {
		IntegerRange parameter = new IntegerRange("foo", 100, 1000);
		
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("99"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("1001"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("555.5"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse(Long.toString(Long.MAX_VALUE)));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("foo"));
	}

	@Test
	public void testApply() {
		Sample sample = new Sample();
		IntegerRange parameter = new IntegerRange("foo", 100, 1000);
		
		parameter.apply(sample, 0.0);
		Assert.assertEquals(100, parameter.readValue(sample));
		
		parameter.apply(sample, 1.0);
		Assert.assertEquals(1000, parameter.readValue(sample));
		
		parameter.apply(sample, 0.5);
		Assert.assertEquals(550, parameter.readValue(sample));
	}
	
	@Test
	public void testApplyOutOfBounds() {
		Sample sample = new Sample();
		IntegerRange parameter = new IntegerRange("foo", 100, 1000);
		
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, -0.001));
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, 1.001));
	}
	
	@Test
	public void testDecode() {
		Tokenizer tokenizer = new Tokenizer();
		IntegerRange parameter = IntegerRange.decode(tokenizer, "foo int 100 1000");
		
		Assert.assertEquals("foo", parameter.getName());
		Assert.assertEquals(100, parameter.getLowerBound());
		Assert.assertEquals(1000, parameter.getUpperBound());
	}
	
	@Test
	public void testDecodeInvalid() {
		Tokenizer tokenizer = new Tokenizer();
		
		Assert.assertThrows(InvalidParameterException.class, () -> IntegerRange.decode(tokenizer, "foo"));
		Assert.assertThrows(InvalidParameterException.class, () -> IntegerRange.decode(tokenizer, "foo int"));
		Assert.assertThrows(InvalidParameterException.class, () -> IntegerRange.decode(tokenizer, "foo int 100"));
		Assert.assertThrows(InvalidParameterException.class, () -> IntegerRange.decode(tokenizer, "foo int 100 1000 10"));
		Assert.assertThrows(InvalidParameterException.class, () -> IntegerRange.decode(tokenizer, "foo unexpected 100 1000"));
	}
	
	@Test
	public void testEncode() {
		Tokenizer tokenizer = new Tokenizer();
		IntegerRange parameter = new IntegerRange("foo", 100, 1000);
		
		Assert.assertEquals("foo int 100 1000", parameter.encode(tokenizer));
	}

}
