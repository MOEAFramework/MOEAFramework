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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;

public class EnumerationTest {
	
	@Test
	public void testParse() {
		Enumeration<Integer> parameter = new Enumeration<Integer>("foo", 100, 200);
		Assert.assertEquals(100, parameter.parse("100"));
		Assert.assertEquals(200, parameter.parse("200"));
	}
	
	@Test
	public void testParseOutOfBounds() {
		Enumeration<Integer> parameter = new Enumeration<Integer>("foo", 100, 200);
		
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("99"));
		Assert.assertThrows(InvalidParameterException.class, () -> parameter.parse("foo"));
	}

	@Test
	public void testEnumerate() {
		List<Sample> samples = new ArrayList<Sample>();
		samples.add(new Sample());
		
		Enumeration<Integer> parameter = new Enumeration<Integer>("foo", 100, 200);
		
		List<Sample> result = parameter.enumerate(samples);
		
		Assert.assertSize(2, result);
		Assert.assertEquals(100, parameter.readValue(result.get(0)));
		Assert.assertEquals(200, parameter.readValue(result.get(1)));
	}
	
	@Test
	public void testDecode() {
		Tokenizer tokenizer = new Tokenizer();
		Enumeration<String> parameter = Enumeration.decode(tokenizer, "foo enum 100 200");
		
		Assert.assertEquals("foo", parameter.getName());
		Assert.assertEquals(List.of("100", "200"), parameter.values());
	}
	
	@Test
	public void testDecodeInvalid() {
		Tokenizer tokenizer = new Tokenizer();
		
		Assert.assertThrows(InvalidParameterException.class, () -> Enumeration.decode(tokenizer, "foo"));
		Assert.assertThrows(InvalidParameterException.class, () -> Enumeration.decode(tokenizer, "foo enum"));
		Assert.assertThrows(InvalidParameterException.class, () -> Enumeration.decode(tokenizer, "foo unexpected 100"));
	}
	
	@Test
	public void testEncode() {
		Tokenizer tokenizer = new Tokenizer();
		Enumeration<Integer> parameter = new Enumeration<Integer>("foo", 100, 200);
		
		Assert.assertEquals("foo enum 100 200", parameter.encode(tokenizer));
	}

}
