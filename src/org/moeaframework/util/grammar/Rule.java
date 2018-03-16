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

import java.util.ArrayList;
import java.util.List;

/**
 * A context-free grammar rule.
 * 
 * @see ContextFreeGrammar
 * @see Production
 * @see Symbol
 */
public class Rule {

	/**
	 * The non-terminal symbol of this rule.
	 */
	private final Symbol symbol;

	/**
	 * The productions defined by this rule.
	 */
	private final List<Production> productions;

	/**
	 * Constructs a rule with the specified symbol. At least one production must
	 * be provided through the {@link #add(Production)} method.
	 * 
	 * @param symbol the non-terminal symbol of this rule
	 */
	public Rule(Symbol symbol) {
		super();
		this.symbol = symbol;

		productions = new ArrayList<Production>();
	}

	/**
	 * Returns the non-terminal symbol of this rule.
	 * 
	 * @return the non-terminal symbol of this rule
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Adds a production to this rule.
	 * 
	 * @param production the production to be added
	 */
	public void add(Production production) {
		productions.add(production);
	}

	/**
	 * Removes a production from this rule.
	 * 
	 * @param production the production to be removed
	 */
	public void remove(Production production) {
		productions.remove(production);
	}

	/**
	 * Returns the number of productions contained in this rule.
	 * 
	 * @return the number of productions contained in this rule
	 */
	public int size() {
		return productions.size();
	}

	/**
	 * Returns the production at the specified index.
	 * 
	 * @param index the index of the production to be returned
	 * @return the production at the specified index
	 * @throws IndexOutOfBoundsException if index is out of range {@code ((index
	 *         < 0) || (index >= size())}
	 */
	public Production get(int index) {
		return productions.get(index);
	}

}
