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
package org.moeaframework.util.format;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Formats an object of some type into a string.
 * 
 * @param <T> the type of object this can format
 */
public interface Formatter<T> {
	
	/**
	 * Returns the class (or classes using inheritance) this formatter supports.
	 * 
	 * @return the class
	 */
	public Class<T> getType();
	
	/**
	 * Formats the given value into a string representation.
	 * 
	 * @param value the value
	 * @return the string representation
	 */
	public String format(Object value);
	
	/**
	 * Formats a list of values into a string representation.  The follows the same formatting as
	 * {@link Arrays#toString(Object[])}.
	 * 
	 * @param values the list of values
	 * @return the string representation
	 */
	public default String format(List<? extends T> values) {
		return format(values.stream());
	}
	
	/**
	 * Formats a stream of values into a string representation.  The follows the same formatting as
	 * {@link Arrays#toString(Object[])}.
	 * 
	 * @param values the stream of values
	 * @return the string representation
	 */
	public default String format(Stream<? extends T> values) {
		return Arrays.toString(values.map(value -> format(value)).toArray());
	}

}
