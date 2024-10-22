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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;

public class TokenizerTest {
	
	private Tokenizer tokenizer;
	
	@Before
	public void setUp() {
		tokenizer = new Tokenizer();
	}
	
	@After
	public void tearDown() {
		tokenizer = null;
	}
	
	@Test
	public void testDecodeEmptyLine() {
		Assert.assertEquals(List.of(), tokenizer.decode(""));
	}
	
	@Test
	public void testDecodeBlankLine() {
		Assert.assertEquals(List.of(), tokenizer.decode("     "));
	}
	
	@Test
	public void testDecodeSingleToken() {
		Assert.assertEquals(List.of("foo"), tokenizer.decode("foo"));
	}
	
	@Test
	public void testDecodeMultipleTokens() {
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("foo bar"));
	}
	
	@Test
	public void testDecodeLeadingTrailingWhitespace() {
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("   foo bar   "));
	}
	
	@Test
	public void testDecodeEscapedWhitespace() {
		Assert.assertEquals(List.of("foo bar"), tokenizer.decode("foo\\ bar"));
	}
	
	@Test
	public void testEncodeEmpty() {
		Assert.assertEquals("", tokenizer.encode(List.of()));
	}
	
	@Test
	public void testEncodeBlankToken() {
		Assert.assertEquals("\\ \\ ", tokenizer.encode(List.of("  ")));
	}
	
	@Test
	public void testEncodeSingleToken() {
		Assert.assertEquals("foo", tokenizer.encode(List.of("foo")));
	}
	
	@Test
	public void testEncodeMultipleTokens() {
		Assert.assertEquals("foo bar", tokenizer.encode(List.of("foo", "bar")));
	}
	
	@Test
	public void testEncodeWhitespace() {
		Assert.assertEquals("foo\\ bar", tokenizer.encode(List.of("foo bar")));
	}

	@Test
	public void testSpecialCharacters() {
		String specialCharacters = "\"'!@#$=:%^&*()\\\r\n//\t ";
		Assert.assertEquals(List.of(specialCharacters), tokenizer.decode(tokenizer.encode(List.of(specialCharacters))));
	}
	
	@Test
	public void testEncodeCustomDelimiter() {
		tokenizer.setOutputDelimiter("=");
		Assert.assertEquals("foo=bar", tokenizer.encode(List.of("foo", "bar")));
	}
	
	@Test
	public void testDecodeCustomDelimiter() {
		tokenizer.addDelimiter("=");
		Assert.assertEquals(List.of("foo", "bar"), tokenizer.decode("foo = bar"));
	}

}
