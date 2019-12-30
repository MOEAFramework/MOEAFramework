/* Copyright 2009-2019 David Hadka
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

/**
 * Tests the {@link Grammar} class.
 */
public class GrammarTest {
	
	/**
	 * The string representation of the grammar used for testing.
	 */
	public static final String GRAMMAR = 
		"<expr> ::= <expr> <op> <expr> | 'func(' <expr> ')' | <val>\n" + 
		"<val> ::= x | y | z\n" + 
		"<op> ::= + | - | * | /";

	/**
	 * The shared grammar used for testing.
	 */
	private ContextFreeGrammar grammar;

	/**
	 * Constructs the shared grammar used for testing.
	 */
	@Before
	public void setUp() throws IOException {
		grammar = Parser.load(new StringReader(GRAMMAR));
	}

	/**
	 * Removes references to the shared grammar for garbage collection.
	 */
	@After
	public void tearDown() {
		grammar = null;
	}

	/**
	 * Tests if the grammar produces valid derivations.
	 */
	@Test
	public void testDerivations() {
		Assert.assertEquals("func(x)-x", grammar.build(new int[] { 0, 1, 2 }));
		Assert.assertEquals("func(func(y))", grammar
				.build(new int[] { 1, 1, 2 }));
		Assert.assertEquals("x", grammar.build(new int[] { 2, 0, 2 }));
		Assert.assertEquals("func(x*func(y))", grammar.build(new int[] { 1, 0,
				2, 0, 2, 1, 2 }));
	}

	/**
	 * Tests if the grammar returns {@code null} on non-terminating derivations.
	 */
	@Test
	public void testNonterminating() {
		Assert.assertEquals(null, grammar.build(new int[] { 0 }));
	}

	/**
	 * Tests if an exception is thrown when passed an empty codon array.
	 */
	@Test(expected = GrammarException.class)
	public void testEmptyCodon() {
		grammar.build(new int[] {});
	}

	/**
	 * Tests if the {@link Grammar#isValid} method correctly identifies valid
	 * and invalid grammars.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testIsValid() throws IOException {
		Assert.assertTrue(grammar.isValid());

		Assert.assertFalse(Parser.load(new StringReader("")).isValid());
		Assert.assertFalse(Parser.load(new StringReader("<foo> ::= <bar>"))
				.isValid());
	}

}
