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
 * A context-free grammar production.
 * 
 * @see ContextFreeGrammar
 * @see Rule
 * @see Symbol
 */
public class Production {

	/**
	 * The symbols contained in this production.
	 */
	private final List<Symbol> symbols;

	/**
	 * Constructs an empty production. At least one symbol must be
	 * provided through the {@link #add(Symbol)} method.
	 */
	public Production() {
		super();

		symbols = new ArrayList<Symbol>();
	}

	/**
	 * Adds a symbol to this production.
	 * 
	 * @param symbol the symbol to be added
	 */
	public void add(Symbol symbol) {
		symbols.add(symbol);
	}

	/**
	 * Returns the number of symbols contained in this production.
	 * 
	 * @return the number of symbols contained in this production
	 */
	public int size() {
		return symbols.size();
	}

	/**
	 * Returns the symbol at the specified index.
	 * 
	 * @param index the index of the symbol to be returned
	 * @return the symbol at the specified index
	 */
	public Symbol get(int index) {
		return symbols.get(index);
	}

}
