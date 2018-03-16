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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A context-free grammar symbol.
 * 
 * @see ContextFreeGrammar
 * @see Production
 */
public class Symbol {

	/**
	 * The value of this symbol.
	 */
	private final String value;

	/**
	 * {@code true} if this symbol is a terminal; {@code false} otherwise.
	 */
	private final boolean isTerminal;

	/**
	 * Constructs a symbol with the specified value.
	 * 
	 * @param value the value of this symbol
	 * @param isTerminal {@code true} if this symbol is a terminal;
	 *        {@code false} otherwise
	 */
	public Symbol(String value, boolean isTerminal) {
		super();
		this.value = value;
		this.isTerminal = isTerminal;
	}

	/**
	 * Returns the value of this symbol.
	 * 
	 * @return the value of this symbol
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns {@code true} if this symbol is a terminal; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if this symbol is a terminal; {@code false}
	 *         otherwise
	 */
	public boolean isTerminal() {
		return isTerminal;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(isTerminal)
				.append(value)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Symbol rhs = (Symbol)obj;
			
			return new EqualsBuilder()
					.append(isTerminal, rhs.isTerminal)
					.append(value, rhs.value)
					.isEquals();
		}
	}

}
