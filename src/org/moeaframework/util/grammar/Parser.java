/* Copyright 2009-2018 David Hadka
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
import java.io.Reader;
import java.io.StreamTokenizer;


/**
 * Parses simple context-free grammars in Backus-Naur form (BNF). The following
 * example demonstrates the accepted syntax. Newlines indicate the end of a
 * rule; single and double quotes can be used to escape the control characters
 * (":", "=", "|", "//", etc.); C and C++ style comments are supported.
 * 
 * <pre>
 * {@code
 * <expr> ::= <expr> <op> <expr> | "func(" <expr> ")" | <val>
 * <op> ::= + | - | * | '/'
 * <val> ::= x | y | z
 * }
 * </pre>
 */
public class Parser {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Parser() {
		super();
	}

	/**
	 * Parses the context-free grammar.
	 * 
	 * @param reader the {@link Reader} containing the BNF context-free grammar
	 * @return the grammar
	 * @throws IOException if an I/O error occurred
	 * @throws GrammarException if an error occurred parsing the BNF
	 *         context-free grammar
	 */
	public static ContextFreeGrammar load(Reader reader) throws IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		tokenizer.resetSyntax();
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('<', '<');
		tokenizer.wordChars('>', '>');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars('-', '-');
		tokenizer.wordChars('.', '.');
		tokenizer.wordChars(128 + 32, 255);
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.quoteChar('"');
		tokenizer.quoteChar('\'');
		tokenizer.eolIsSignificant(true);
		tokenizer.slashSlashComments(true);
		tokenizer.slashStarComments(true);

		ContextFreeGrammar grammar = new ContextFreeGrammar();
		Rule rule = null;
		Production production = null;

		while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
			if ((tokenizer.ttype == ':') || (tokenizer.ttype == '=')) {
				do {
					tokenizer.nextToken();
				} while ((tokenizer.ttype == ':') || (tokenizer.ttype == '='));

				if ((rule == null) || (production != null)) {
					throw new GrammarException("unexpected rule separator",
							tokenizer.lineno());
				}

				tokenizer.pushBack();
			} else if (tokenizer.ttype == '|') {
				if ((rule != null) && (production == null)) {
					throw new GrammarException(
							"rule must contain at least one production",
							tokenizer.lineno());
				}

				production = null;
			} else if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
				if ((rule != null) && (production == null)) {
					throw new GrammarException(
							"rule must contain at least one production",
							tokenizer.lineno());
				}

				rule = null;
				production = null;
			} else {
				String string = null;

				if ((tokenizer.ttype == StreamTokenizer.TT_WORD)
						|| (tokenizer.ttype == '\'')
						|| (tokenizer.ttype == '\"')) {
					string = tokenizer.sval;
				} else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
					string = Double.toString(tokenizer.nval);
				} else {
					string = Character.toString((char)tokenizer.ttype);
				}

				if (string.startsWith("<") && string.endsWith(">")) {
					string = string.substring(1, string.length() - 1);

					if (string.isEmpty()) {
						throw new GrammarException("invalid symbol",
								tokenizer.lineno());
					}

					if (rule == null) {
						rule = new Rule(new Symbol(string, false));
						grammar.add(rule);
					} else if (production == null) {
						production = new Production();
						production.add(new Symbol(string, false));
						rule.add(production);
					} else {
						production.add(new Symbol(string, false));
					}
				} else {
					if (rule == null) {
						throw new GrammarException(
								"rule must start with non-terminal", tokenizer
										.lineno());
					} else if (production == null) {
						production = new Production();
						production.add(new Symbol(string, true));
						rule.add(production);
					} else {
						production.add(new Symbol(string, true));
					}
				}
			}
		}

		if ((rule != null) && (production == null)) {
			throw new GrammarException(
					"rule must contain at least one production", tokenizer
							.lineno());
		}

		return grammar;
	}

}
