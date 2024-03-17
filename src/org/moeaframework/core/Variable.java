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
package org.moeaframework.core;

import java.io.Serializable;

/**
 * Interface for decision variables. This interface ensures independent copies of decision variables can be
 * constructed.  Implementations are strongly encouraged to also override {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public interface Variable extends Serializable {

	/**
	 * Returns an independent copy of this decision variable. It is required that {@code x.copy()} is completely
	 * independent from {@code x}. This means any method invoked on {@code x.copy()} in no way alters the state
	 * of {@code x} and vice versa. It is typically the case that {@code x.copy().getClass() == x.getClass()} and
	 * {@code x.copy().equals(x)}.
	 * 
	 * @return an independent copy of this decision variable
	 */
	public Variable copy();
	
	/**
	 * Randomly assign the value of this variable.  In general, the randomization should follow a uniform distribution.
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
	 * 
	 * Implementations should make an effort to display the value in a meaningful format, but that is not required.
	 * Instead, use {@link #toString()} if a human-readable format is required.
	 * 
	 * This method along with {@link #decode(String)} are used primarily for storing values in text files.  To make
	 * parsing easier, the resulting string must:
	 * 
	 * 1. Only contain ASCII characters
	 * 2. Contain no whitespace (no spaces, tabs, newlines, etc.)
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

}
