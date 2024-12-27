/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.variable;

import java.io.Serializable;

import org.moeaframework.core.Copyable;
import org.moeaframework.core.Defined;
import org.moeaframework.core.Named;

/**
 * Interface for decision variables.
 * <p>
 * Implementations are encouraged to also override {@link Object#equals(Object)} and {@link Object#hashCode()}.  These
 * are used, for example, to exclude duplicate solutions from populations.
 */
public interface Variable extends Copyable<Variable>, Serializable, Defined, Named {
	
	/**
	 * Randomly assign the value of this variable.
	 */
	public void randomize();
	
	/**
	 * Returns a human-readable representation of this value.
	 * 
	 * @return the value of this variable formatted as a string
	 */
	@Override
	public String toString();
	
	/**
	 * Encodes the value of this variable as a string.  This should reflect the internal representation of the value
	 * and not necessarily the actual value.  For example, a binary-encoded integer should display the bits and not the
	 * integer value.
	 * <p>
	 * Implementations should make an effort to display the value in a meaningful format, but that is not required.
	 * Instead, use {@link #toString()} if a human-readable format is required.
	 * <p>
	 * This method along with {@link #decode(String)} are used primarily for storing values in text files.  To make
	 * parsing easier, the resulting string must:
	 * <ol>
	 *   <li>Only contain ASCII characters
	 *   <li>Contain no whitespace (no spaces, tabs, newlines, etc.)
	 * </ol>
	 * 
	 * @return the encoded value as a string
	 */
	public String encode();
	
	/**
	 * Parses and loads the value of this variable from a string.  This must be able to process any string produced
	 * by {@link #encode()}.
	 * 
	 * @param value the value as a string
	 */
	public void decode(String value);
	
	/**
	 * Returns the name of the variable, using either the name assigned to the variable or deriving the name from its
	 * index.
	 * 
	 * @param variable the variable
	 * @param index the index of the variable
	 * @return the name of the variable
	 */
	public static String getNameOrDefault(Variable variable, int index) {
		return variable.getName() == null ? "Var" + (index + 1) : variable.getName();
	}

}
