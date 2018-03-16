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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A context-free grammar. The rule at index {@code 0} is the starting rule when
 * building derivation trees.
 * 
 * @see Rule
 */
public class ContextFreeGrammar {

	/**
	 * The rules contained in this grammar.
	 */
	private final List<Rule> rules;

	/**
	 * The maximum number of times the builder will wrap around the codon array
	 * before failing to produce a valid derivation.
	 */
	private int wrapLimit;

	/**
	 * Constructs an empty grammar.
	 */
	public ContextFreeGrammar() {
		super();

		rules = new ArrayList<Rule>();
		wrapLimit = 10;
	}

	/**
	 * Returns the maximum number of times the builder will wrap around the
	 * codon array before failing to produce a valid derivation.
	 * 
	 * @return the maximum number of times the builder will wrap around the
	 *         codon array before failing to produce a valid derivation
	 */
	public int getWrapLimit() {
		return wrapLimit;
	}

	/**
	 * Sets the maximum number of times the builder will wrap around the codon
	 * array before failing to produce a valid derivation.
	 * 
	 * @param wrapLimit the maximum number of times the builder will wrap around
	 *        the codon array before failing to produce a valid derivation
	 */
	public void setWrapLimit(int wrapLimit) {
		this.wrapLimit = wrapLimit;
	}

	/**
	 * Adds a rule to this grammar.
	 * 
	 * @param rule the rule to be added
	 */
	public void add(Rule rule) {
		rules.add(rule);
	}

	/**
	 * Removes a rule from this grammar.
	 * 
	 * @param rule the rule to be removed
	 */
	public void remove(Rule rule) {
		rules.remove(rule);
	}

	/**
	 * Returns the number of rules contained in this grammar.
	 * 
	 * @return the number of rules contained in this grammar
	 */
	public int size() {
		return rules.size();
	}

	/**
	 * Returns the rule at the specified index.
	 * 
	 * @param index the index of the rule to be returned
	 * @return the rule at the specified index
	 * @throws IndexOutOfBoundsException if index is out of range {@code ((index
	 *         < 0) || (index >= size())}
	 */
	public Rule get(int index) {
		return rules.get(index);
	}

	/**
	 * Returns the rule for the specified symbol; or {@code null} if no rule
	 * with the specified symbol exists.
	 * 
	 * @param symbol the symbol of the rule to be returned
	 * @return the rule for the specified symbol; or {@code null} if no rule
	 *         with the specified symbol exists
	 */
	public Rule get(Symbol symbol) {
		for (Rule rule : rules) {
			if (rule.getSymbol().equals(symbol)) {
				return rule;
			}
		}

		return null;
	}

	/**
	 * Returns the grammar derivation using the construction rules of
	 * Grammatical Evolution on the specified codon array; or {@code null} if
	 * the codon array failed to produce a valid derivation Whenever the
	 * derivation encounters a rule with multiple productions, the production
	 * used to expand the rule is chosen using this codon array.
	 * 
	 * @param array the codon array
	 * @return the grammar derivation using the construction rules of
	 *         Grammatical Evolution on the specified codon array; or
	 *         {@code null} if the codon array failed to produce a valid
	 *         derivation
	 * @throws GrammarException if the codon array is empty
	 */
	public String build(int[] array) {
		if (array.length == 0) {
			throw new GrammarException("codon array is empty");
		}

		StringBuilder sb = new StringBuilder();
		Stack<Symbol> remaining = new Stack<Symbol>();

		int index = 0;
		int wraps = 0;

		remaining.push(rules.get(0).getSymbol());

		while (!remaining.isEmpty()) {
			Symbol symbol = remaining.pop();

			if (symbol.isTerminal()) {
				sb.append(symbol.getValue());
			} else {
				Rule rule = get(symbol);
				int productionIndex = 0;

				if (rule.size() > 1) {
					productionIndex = array[index] % rule.size();
					index++;

					if (index >= array.length) {
						index = 0;
						wraps++;

						if (wraps > wrapLimit) {
							return null;
						}
					}
				}

				Production production = rule.get(productionIndex);

				for (int i = production.size() - 1; i >= 0; i--) {
					remaining.push(production.get(i));
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Returns {@code true} if this grammar is valid; {@code false} otherwise. A
	 * grammar is valid if it is non-empty and all non-terminal symbols are
	 * defined by a rule in the grammar.
	 * 
	 * @return {@code true} if this grammar is valid; {@code false} otherwise
	 */
	public boolean isValid() {
		if (size() == 0) {
			return false;
		}

		Set<Symbol> symbols = new HashSet<Symbol>();

		for (int i = 0; i < size(); i++) {
			Rule rule = get(i);

			for (int j = 0; j < rule.size(); j++) {
				Production production = rule.get(j);

				for (int k = 0; k < production.size(); k++) {
					Symbol symbol = production.get(k);

					if (!symbol.isTerminal()) {
						symbols.add(symbol);
					}
				}
			}
		}

		for (Symbol symbol : symbols) {
			if (get(symbol) == null) {
				return false;
			}
		}

		return true;
	}

}
