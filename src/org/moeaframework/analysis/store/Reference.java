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
package org.moeaframework.analysis.store;

import java.util.Set;

import org.moeaframework.core.TypedProperties;

/**
 * A reference that uniquely identifies or addresses as specific {@link Container}.
 * <p>
 * References are generic in nature, treating the field names and values as strings.  While names are expected to be
 * case-insensitive, no other requirements are in place regarding their types and ordering.  When such additional
 * requirements are desired, the reference should be used with and validated by a
 * {@link org.moeaframework.analysis.store.schema.Schema}.
 */
public interface Reference {
	
	/**
	 * Returns the fields defined by this reference.
	 * 
	 * @return the field names
	 */
	public Set<String> fields();
	
	/**
	 * Returns the value associated with the given field.
	 * 
	 * @param field the field name
	 * @return the value associated with the field
	 */
	public String get(String field);
	
	/**
	 * Extends this reference, adding or overwriting one field with a new value.
	 * 
	 * @param name the field name
	 * @param value the new value
	 * @return a new reference with this modification
	 */
	public default Reference extend(String name, String value) {
		return new ExtendedReference(this, name, value);
	}
	
	/**
	 * Constructs a reference with all the keys and values contained in a {@link TypedProperties}.
	 * 
	 * @param properties the typed properties object
	 * @return a new reference based on the keys and values in the properties
	 */
	public static Reference of(TypedProperties properties) {
		return new TypedPropertiesReference(properties);
	}

}