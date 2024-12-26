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
package org.moeaframework.analysis.store.schema;

/**
 * Builder for constructing fields of specific types.
 */
public class FieldBuilder {
	
	private final String name;
	
	FieldBuilder(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Builds and returns a field storing an integer value.
	 * 
	 * @return the field
	 */
	public Field<Integer> asInt() {
		return new Field<>(name, Integer.class);
	}
	
	/**
	 * Builds and returns a field storing a long value.
	 * 
	 * @return the field
	 */
	public Field<Long> asLong() {
		return new Field<>(name, Long.class);
	}
	
	/**
	 * Builds and returns a field storing a decial value.
	 * 
	 * @return the field
	 */
	public Field<Double> asDecimal() {
		return new Field<>(name, Double.class);
	}
	
	/**
	 * Builds and returns a field storing a string value.
	 * 
	 * @return the field
	 */
	public Field<String> asString() {
		return new Field<>(name, String.class);
	}

}
