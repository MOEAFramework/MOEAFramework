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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Parser} class.
 */
public class ParserTest {

	/**
	 * Tests if the parser correctly handles an empty file, or a file filled
	 * with whitespace.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testEmptyRule() throws IOException {
		ContextFreeGrammar g1 = Parser.load(new StringReader(""));
		Assert.assertEquals(0, g1.size());

		ContextFreeGrammar g2 = Parser.load(new StringReader(
				"  \r\n     \r\n\r\n"));
		Assert.assertEquals(0, g2.size());

		ContextFreeGrammar g3 = Parser.load(new StringReader("  \n     \n\n"));
		Assert.assertEquals(0, g3.size());
	}

	/**
	 * Tests if the parser correctly ignores C and C++ style comments.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testComments() throws IOException {
		ContextFreeGrammar g1 = Parser.load(new StringReader(
				"//<test> ::= <foo> <bar>"));
		Assert.assertEquals(0, g1.size());

		ContextFreeGrammar g2 = Parser.load(new StringReader(
				"/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(0, g2.size());

		ContextFreeGrammar g3 = Parser.load(new StringReader(
				"//<test> ::= <foo> <bar>\n/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(0, g3.size());

		ContextFreeGrammar g4 = Parser.load(new StringReader(
				"//<test> ::= <foo> <bar>\n<real> ::= <production>\n/*<test> ::= <foo> <bar>*/"));
		Assert.assertEquals(1, g4.size());
		Assert.assertEquals(1, g4.get(0).size());

		ContextFreeGrammar g5 = Parser.load(new StringReader(
				"<inline> ::= <production> //<test> ::= <foo> <bar>"));
		Assert.assertEquals(1, g5.size());
		Assert.assertEquals(1, g5.get(0).size());

		ContextFreeGrammar g6 = Parser.load(new StringReader(
				"<inline> ::= /* <commented> */ <production>"));
		Assert.assertEquals(1, g6.size());
		Assert.assertEquals(1, g6.get(0).size());
	}

	/**
	 * Tests if the parser correctly identifies the rule separator in its
	 * various forms.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testRuleSeparator() throws IOException {
		ContextFreeGrammar g1 = Parser
				.load(new StringReader("<foo> ::= <bar>"));
		Assert.assertEquals(1, g1.size());
		Assert.assertEquals(1, g1.get(0).size());

		ContextFreeGrammar g2 = Parser.load(new StringReader("<foo> : <bar>"));
		Assert.assertEquals(1, g2.size());
		Assert.assertEquals(1, g2.get(0).size());

		ContextFreeGrammar g3 = Parser.load(new StringReader("<foo> = <bar>"));
		Assert.assertEquals(1, g3.size());
		Assert.assertEquals(1, g3.get(0).size());

		ContextFreeGrammar g4 = Parser.load(new StringReader(
				"<foo> ::=:==: <bar>"));
		Assert.assertEquals(1, g4.size());
		Assert.assertEquals(1, g4.get(0).size());
	}

	/**
	 * Tests the parser on a simple example.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testSimpleExample() throws IOException {
		ContextFreeGrammar g = Parser.load(new StringReader(
				"<foo> ::= <bar>\n<bar> ::= a | (b) | ( c )"));

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

	/**
	 * Tests if the parser throws an exception if the rule symbol is missing.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testMissingRuleSymbolException() throws IOException {
		Parser.load(new StringReader(" ::= <bar>"));
	}

	/**
	 * Tests if the parser throws an exception if the rule symbol is not
	 * surrounded by angle brackets.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testInvalidRuleSymbolException1() throws IOException {
		Parser.load(new StringReader("foo ::= <bar>"));
	}

	/**
	 * Tests if the parser throws an exception if the rule symbol is not
	 * surrounded by angle brackets.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testInvalidRuleSymbolException2() throws IOException {
		Parser.load(new StringReader("<foo ::= <bar>"));
	}

	/**
	 * Tests if the parser throws an exception if the production is empty.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testEmptyProductionException1() throws IOException {
		Parser.load(new StringReader("<foo> ::= "));
	}

	/**
	 * Tests if the parser throws an exception if the production is incomplete.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testEmptyProductionException2() throws IOException {
		Parser.load(new StringReader("<foo> ::= <bar> | "));
	}

	/**
	 * Tests if the parser throws an exception if the production is missing a
	 * rule.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testEmptyProductionException3() throws IOException {
		Parser.load(new StringReader("<foo> ::= | <bar>"));
	}

	/**
	 * Tests if the parser throws an exception if the rule symbol is the
	 * empty string.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testEmptySymbolException1() throws IOException {
		Parser.load(new StringReader("<> ::= <bar>"));
	}

	/**
	 * Tests if the parser throws an exception if a production symbol is the
	 * empty string.
	 * 
	 * @throws GrammarException the expected outcome
	 */
	@Test(expected = GrammarException.class)
	public void testEmptySymbolException2() throws IOException {
		Parser.load(new StringReader("<foo> ::= <>"));
	}

	/**
	 * Tests if the parser correctly handles escaped characters.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testEscapedCharacters() throws IOException {
		ContextFreeGrammar g = Parser.load(new StringReader(
				"<foo> ::= \":\" | '|' | \"<\" | '>'"));
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

	/**
	 * Tests if the parser correctly handles single and double quotes.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testQuotes() throws IOException {
		ContextFreeGrammar g1 = Parser.load(new StringReader(
				"<foo> ::= \"bar()\""));
		Assert.assertEquals(1, g1.size());
		Assert.assertEquals(1, g1.get(0).size());
		Assert.assertEquals(1, g1.get(0).get(0).size());
		Assert.assertTrue(g1.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("bar()", g1.get(0).get(0).get(0).getValue());

		ContextFreeGrammar g2 = Parser.load(new StringReader(
				"<foo> ::= 'bar()'"));
		Assert.assertEquals(1, g2.size());
		Assert.assertEquals(1, g2.get(0).size());
		Assert.assertEquals(1, g2.get(0).get(0).size());
		Assert.assertTrue(g2.get(0).get(0).get(0).isTerminal());
		Assert.assertEquals("bar()", g2.get(0).get(0).get(0).getValue());
	}

	/**
	 * Tests if the parser correctly handles integer terminals.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testIntegers() throws IOException {
		ContextFreeGrammar g = Parser.load(new StringReader(
				"<numbers> ::= 8 | -64 256"));
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

	/**
	 * Tests if the parser correctly handles decimal terminals.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testDecimals() throws IOException {
		ContextFreeGrammar g = Parser.load(new StringReader(
				"<numbers> ::= 0.0 | -1.2 42.24"));
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
