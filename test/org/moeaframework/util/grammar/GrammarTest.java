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
package org.moeaframework.util.grammar;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GrammarTest {
	
	public static final String GRAMMAR = """
		<expr> ::= <expr> <op> <expr> | 'func(' <expr> ')' | <val>
		<val> ::= x | y | z
		<op> ::= + | - | * | /
		""";

	private ContextFreeGrammar grammar;

	@Before
	public void setUp() throws IOException {
		grammar = Parser.load(new StringReader(GRAMMAR));
	}

	@After
	public void tearDown() {
		grammar = null;
	}

	@Test
	public void testDerivations() {
		Assert.assertEquals("func(x)-x", grammar.build(new int[] { 0, 1, 2 }));
		Assert.assertEquals("func(func(y))", grammar.build(new int[] { 1, 1, 2 }));
		Assert.assertEquals("x", grammar.build(new int[] { 2, 0, 2 }));
		Assert.assertEquals("func(x*func(y))", grammar.build(new int[] { 1, 0, 2, 0, 2, 1, 2 }));
	}

	@Test
	public void testNonterminatingProducesNull() {
		Assert.assertEquals(null, grammar.build(new int[] { 0 }));
	}

	@Test(expected = GrammarException.class)
	public void testEmptyCodon() {
		grammar.build(new int[] {});
	}

	@Test
	public void testIsValid() throws IOException {
		Assert.assertTrue(grammar.isValid());

		Assert.assertFalse(Parser.load(new StringReader("")).isValid());
		Assert.assertFalse(Parser.load(new StringReader("<foo> ::= <bar>")).isValid());
	}

}
