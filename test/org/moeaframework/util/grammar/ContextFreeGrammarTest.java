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
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;

public class ContextFreeGrammarTest {
	
	public static final String GRAMMAR = """
		<expr> ::= <expr> <op> <expr> | 'func(' <expr> ')' | <val>
		<val> ::= x | y | z
		<op> ::= + | - | * | /
		""";

	private ContextFreeGrammar grammar;

	@Before
	public void setUp() throws IOException {
		grammar = ContextFreeGrammar.load(new StringReader(GRAMMAR));
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
		Assert.assertFalse(ContextFreeGrammar.load(new StringReader("")).isValid());
		Assert.assertFalse(ContextFreeGrammar.load(new StringReader("<foo> ::= <bar>")).isValid());
	}
	
	@Test
	public void testEmptyRule() throws IOException {
		ContextFreeGrammar g1 = ContextFreeGrammar.load(new StringReader(""));
		Assert.assertEquals(0, g1.size());

		ContextFreeGrammar g2 = ContextFreeGrammar.load(new StringReader("  \r\n     \r\n\r\n"));
		Assert.assertEquals(0, g2.size());

		ContextFreeGrammar g3 = ContextFreeGrammar.load(new StringReader("  \n     \n\n"));
		Assert.assertEquals(0, g3.size());
	}

	@Test
	public void testComments() throws IOException {
		ContextFreeGrammar g1 = ContextFreeGrammar.load(new StringReader("//<test> ::= <foo> <bar>"));
		Assert.assertEquals(0, g1.size());

		ContextFreeGrammar g2 = ContextFreeGrammar.load(new StringReader("/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(0, g2.size());

		ContextFreeGrammar g3 = ContextFreeGrammar.load(new StringReader("//<test> ::= <foo> <bar>\n/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(0, g3.size());

		ContextFreeGrammar g4 = ContextFreeGrammar.load(new StringReader(
				"//<test> ::= <foo> <bar>\n<real> ::= <production>\n/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(1, g4.size());
		Assert.assertEquals(1, g4.get(0).size());

		ContextFreeGrammar g5 = ContextFreeGrammar.load(new StringReader("<inline> ::= <production> //<test> ::= <foo> <bar>"));
		Assert.assertEquals(1, g5.size());
		Assert.assertEquals(1, g5.get(0).size());

		ContextFreeGrammar g6 = ContextFreeGrammar.load(new StringReader("<inline> ::= /* <commented> */ <production>"));
		Assert.assertEquals(1, g6.size());
		Assert.assertEquals(1, g6.get(0).size());
	}

	@Test
	public void testRuleSeparator() throws IOException {
		ContextFreeGrammar g1 = ContextFreeGrammar.load(new StringReader("<foo> ::= <bar>"));
		Assert.assertEquals(1, g1.size());
		Assert.assertEquals(1, g1.get(0).size());

		ContextFreeGrammar g2 = ContextFreeGrammar.load(new StringReader("<foo> : <bar>"));
		Assert.assertEquals(1, g2.size());
		Assert.assertEquals(1, g2.get(0).size());

		ContextFreeGrammar g3 = ContextFreeGrammar.load(new StringReader("<foo> = <bar>"));
		Assert.assertEquals(1, g3.size());
		Assert.assertEquals(1, g3.get(0).size());

		ContextFreeGrammar g4 = ContextFreeGrammar.load(new StringReader("<foo> ::=:==: <bar>"));
		Assert.assertEquals(1, g4.size());
		Assert.assertEquals(1, g4.get(0).size());
	}

	@Test
	public void testSimpleExample() throws IOException {
		ContextFreeGrammar g = ContextFreeGrammar.load(new StringReader("<foo> ::= <bar>\n<bar> ::= a | (b) | ( c )"));

		Assert.assertEquals(2, g.size());
		Assert.assertEquals(1, g.get(0).size());
		Assert.assertFalse(g.get(0).getSymbol().isTerminal());
		Assert.assertEquals("foo", g.get(0).getSymbol().getValue());
		Assert.assertEquals(1, g.get(0).get(0).size());
		Assert.assertFalse(g.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("bar", g.get(0).get(0).get(0).getValue());
		Assert.assertEquals(3, g.get(1).size());

		Assert.assertEquals(3, g.get(1).size());
		Assert.assertFalse(g.get(1).getSymbol().isTerminal());
		Assert.assertEquals("bar", g.get(1).getSymbol().getValue());
		Assert.assertEquals(1, g.get(1).get(0).size());
		Assert.assertTrue(g.get(1).get(0).get(0).isTerminal());
		Assert.assertEquals("a", g.get(1).get(0).get(0).getValue());
		Assert.assertEquals(3, g.get(1).get(1).size());
		Assert.assertTrue(g.get(1).get(1).get(0).isTerminal());
		Assert.assertEquals("(", g.get(1).get(1).get(0).getValue());
		Assert.assertTrue(g.get(1).get(1).get(1).isTerminal());
		Assert.assertEquals("b", g.get(1).get(1).get(1).getValue());
		Assert.assertTrue(g.get(1).get(1).get(2).isTerminal());
		Assert.assertEquals(")", g.get(1).get(1).get(2).getValue());
		Assert.assertEquals(3, g.get(1).get(2).size());
		Assert.assertTrue(g.get(1).get(2).get(0).isTerminal());
		Assert.assertEquals("(", g.get(1).get(2).get(0).getValue());
		Assert.assertTrue(g.get(1).get(2).get(1).isTerminal());
		Assert.assertEquals("c", g.get(1).get(2).get(1).getValue());
		Assert.assertTrue(g.get(1).get(2).get(2).isTerminal());
		Assert.assertEquals(")", g.get(1).get(2).get(2).getValue());
	}

	@Test(expected = GrammarException.class)
	public void testMissingRuleSymbolException() throws IOException {
		ContextFreeGrammar.load(new StringReader(" ::= <bar>"));
	}

	@Test(expected = GrammarException.class)
	public void testInvalidRuleSymbolException1() throws IOException {
		ContextFreeGrammar.load(new StringReader("foo ::= <bar>"));
	}

	@Test(expected = GrammarException.class)
	public void testInvalidRuleSymbolException2() throws IOException {
		ContextFreeGrammar.load(new StringReader("<foo ::= <bar>"));
	}

	@Test(expected = GrammarException.class)
	public void testEmptyProductionException1() throws IOException {
		ContextFreeGrammar.load(new StringReader("<foo> ::= "));
	}

	@Test(expected = GrammarException.class)
	public void testEmptyProductionException2() throws IOException {
		ContextFreeGrammar.load(new StringReader("<foo> ::= <bar> | "));
	}

	@Test(expected = GrammarException.class)
	public void testEmptyProductionException3() throws IOException {
		ContextFreeGrammar.load(new StringReader("<foo> ::= | <bar>"));
	}

	@Test(expected = GrammarException.class)
	public void testEmptySymbolException1() throws IOException {
		ContextFreeGrammar.load(new StringReader("<> ::= <bar>"));
	}

	@Test(expected = GrammarException.class)
	public void testEmptySymbolException2() throws IOException {
		ContextFreeGrammar.load(new StringReader("<foo> ::= <>"));
	}

	@Test
	public void testEscapedCharacters() throws IOException {
		ContextFreeGrammar g = ContextFreeGrammar.load(new StringReader("<foo> ::= \":\" | '|' | \"<\" | '>'"));
		Assert.assertEquals(1, g.size());
		Assert.assertEquals(4, g.get(0).size());
		Assert.assertEquals(1, g.get(0).get(0).size());
		Assert.assertTrue(g.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals(":", g.get(0).get(0).get(0).getValue());
		Assert.assertEquals(1, g.get(0).get(1).size());
		Assert.assertTrue(g.get(0).get(1).get(0).isTerminal());
		Assert.assertEquals("|", g.get(0).get(1).get(0).getValue());
		Assert.assertEquals(1, g.get(0).get(2).size());
		Assert.assertTrue(g.get(0).get(2).get(0).isTerminal());
		Assert.assertEquals("<", g.get(0).get(2).get(0).getValue());
		Assert.assertEquals(1, g.get(0).get(3).size());
		Assert.assertTrue(g.get(0).get(3).get(0).isTerminal());
		Assert.assertEquals(">", g.get(0).get(3).get(0).getValue());
	}

	@Test
	public void testQuotes() throws IOException {
		ContextFreeGrammar g1 = ContextFreeGrammar.load(new StringReader("<foo> ::= \"bar()\""));
		Assert.assertEquals(1, g1.size());
		Assert.assertEquals(1, g1.get(0).size());
		Assert.assertEquals(1, g1.get(0).get(0).size());
		Assert.assertTrue(g1.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("bar()", g1.get(0).get(0).get(0).getValue());

		ContextFreeGrammar g2 = ContextFreeGrammar.load(new StringReader("<foo> ::= 'bar()'"));
		Assert.assertEquals(1, g2.size());
		Assert.assertEquals(1, g2.get(0).size());
		Assert.assertEquals(1, g2.get(0).get(0).size());
		Assert.assertTrue(g2.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("bar()", g2.get(0).get(0).get(0).getValue());
	}

	@Test
	public void testIntegers() throws IOException {
		ContextFreeGrammar g = ContextFreeGrammar.load(new StringReader("<numbers> ::= 8 | -64 256"));
		Assert.assertEquals(1, g.size());
		Assert.assertEquals(2, g.get(0).size());
		Assert.assertEquals(1, g.get(0).get(0).size());
		Assert.assertTrue(g.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("8", g.get(0).get(0).get(0).getValue());
		Assert.assertEquals(2, g.get(0).get(1).size());
		Assert.assertTrue(g.get(0).get(1).get(0).isTerminal());
		Assert.assertEquals("-64", g.get(0).get(1).get(0).getValue());
		Assert.assertTrue(g.get(0).get(1).get(1).isTerminal());
		Assert.assertEquals("256", g.get(0).get(1).get(1).getValue());
	}

	@Test
	public void testDecimals() throws IOException {
		ContextFreeGrammar g = ContextFreeGrammar.load(new StringReader("<numbers> ::= 0.0 | -1.2 42.24"));
		Assert.assertEquals(1, g.size());
		Assert.assertEquals(2, g.get(0).size());
		Assert.assertEquals(1, g.get(0).get(0).size());
		Assert.assertTrue(g.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("0.0", g.get(0).get(0).get(0).getValue());
		Assert.assertEquals(2, g.get(0).get(1).size());
		Assert.assertTrue(g.get(0).get(1).get(0).isTerminal());
		Assert.assertEquals("-1.2", g.get(0).get(1).get(0).getValue());
		Assert.assertTrue(g.get(0).get(1).get(1).isTerminal());
		Assert.assertEquals("42.24", g.get(0).get(1).get(1).getValue());
	}

}
