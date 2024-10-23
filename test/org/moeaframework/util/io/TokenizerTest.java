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
package org.moeaframework.util.io;

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;

public class TokenizerTest {

	@Test
	public void testWhitespaceDelimiterDecode() {
		Tokenizer tokenizer = new Tokenizer();
		
		Assert.assertEquals(List.of(), tokenizer.decode(""));
		Assert.assertEquals(List.of(), tokenizer.decode("     "));
		Assert.assertEquals(List.of("foo"), tokenizer.decode("foo"));
		Assert.assertEquals(List.of("foo"), tokenizer.decode("  foo  "));
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("foo bar"));
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("   foo   bar   "));
		Assert.assertEquals(List.of("foo bar"), tokenizer.decode("foo\\ bar"));
		Assert.assertEquals(List.of(" foo "), tokenizer.decode(" \\ foo\\  "));
		Assert.assertEquals(List.of(" foo ", " bar "), tokenizer.decode(" \\ foo\\  \\ bar\\  "));
	}

	@Test
	public void testWhitespaceDelimiterEncode() {
		Tokenizer tokenizer = new Tokenizer();
		
		Assert.assertEquals("", tokenizer.encode(List.of()));
		Assert.assertEquals("\\ \\ ", tokenizer.encode(List.of("  ")));
		Assert.assertEquals("foo", tokenizer.encode(List.of("foo")));
		Assert.assertEquals("foo bar", tokenizer.encode(List.of("foo", "bar")));
		Assert.assertEquals("foo\\ bar", tokenizer.encode(List.of("foo bar")));
		Assert.assertEquals("\\ foo\\ ", tokenizer.encode(List.of(" foo ")));
		Assert.assertEquals("\\ foo\\  \\ bar\\ ", tokenizer.encode(List.of(" foo ", " bar ")));
	}

	@Test
	public void testSpecialCharacters() {
		Tokenizer tokenizer = new Tokenizer();
		
		String specialCharacters = "\"'!@#$=:%^&*()\\\r\n//\t ";
		Assert.assertEquals(List.of(specialCharacters), tokenizer.decode(tokenizer.encode(List.of(specialCharacters))));
	}
	
	@Test
	public void testCustomDelimiterDecode() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setDelimiter(',');
		
		Assert.assertEquals(List.of(), tokenizer.decode(""));
		Assert.assertEquals(List.of(), tokenizer.decode("     "));
		Assert.assertEquals(List.of("foo"), tokenizer.decode("foo"));
		Assert.assertEquals(List.of("foo"), tokenizer.decode("  foo  "));
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("foo,bar"));
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("   foo  ,  bar   "));
		Assert.assertEquals(List.of("foo, bar"), tokenizer.decode("foo\\, bar"));
		Assert.assertEquals(List.of(" foo "), tokenizer.decode(" \\ foo\\  "));
		Assert.assertEquals(List.of(" foo ", " bar "), tokenizer.decode(" \\ foo\\ , \\ bar\\  "));
		
		Assert.assertEquals(List.of("", "foo", "bar"), tokenizer.decode(",foo,bar"));
		Assert.assertEquals(List.of("foo", "bar", ""), tokenizer.decode("foo,bar,"));
		Assert.assertEquals(List.of("foo", "", "bar"), tokenizer.decode("foo,,bar"));
	}

	@Test
	public void testCustomDelimiterEncode() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setDelimiter(',');
		
		Assert.assertEquals("", tokenizer.encode(List.of()));
		Assert.assertEquals("\\ \\ ", tokenizer.encode(List.of("  ")));
		Assert.assertEquals("foo", tokenizer.encode(List.of("foo")));
		Assert.assertEquals("foo,bar", tokenizer.encode(List.of("foo", "bar")));
		Assert.assertEquals("foo\\ bar", tokenizer.encode(List.of("foo bar")));
		Assert.assertEquals("\\ foo\\ ", tokenizer.encode(List.of(" foo ")));
		Assert.assertEquals("\\ foo\\ ,\\ bar\\ ", tokenizer.encode(List.of(" foo ", " bar ")));
	}

}
