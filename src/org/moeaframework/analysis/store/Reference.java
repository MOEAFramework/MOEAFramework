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
	 * Returns {@code true} if this references the root container.  See {@link DataStore#getRootContainer()} for more
	 * details.
	 * 
	 * @return {@code true} if this references the root container; {@code false} otherwise
	 */
	public default boolean isRoot() {
		return fields().isEmpty();
	}
	
	/**
	 * Extends this reference, adding or overwriting one field with a new value.
	 * 
	 * @param name the field name
	 * @param value the new value
	 * @return a new reference with this modification
	 * @deprecated use {@link #with(String, String)} instead
	 */
	@Deprecated
	public default Reference extend(String name, String value) {
		return new ExtendedReference(this, name, value);
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, byte... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, short... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, int... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, long... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, float... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.  The value can
	 * either be a single value or an array.
	 * 
	 * @param name the new or overwritten field name
	 * @param values the new value(s) assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, double... values) {
		return with(name, TypedProperties.of(name, values).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.
	 * 
	 * @param name the new or overwritten field name
	 * @param value the new value assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, String value) {
		return new ExtendedReference(this, name, value);
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.
	 * 
	 * @param name the new or overwritten field name
	 * @param value the new value assigned to the field
	 * @return a new reference with this modification
	 */
	public default Reference with(String name, boolean value) {
		return with(name, TypedProperties.of(name, value).getString(name));
	}
	
	/**
	 * Creates a new reference with the designated field name added or overwritten with the given value.
	 * 
	 * @param name the new or overwritten field name
	 * @param value the new value assigned to the field
	 * @return a new reference with this modification
	 */
	public default <T extends Enum<?>> Reference with(String name, T value) {
		return with(name, TypedProperties.of(name, value).getString(name));
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
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, byte... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, short... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, int... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, long... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, float... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.  The value can either be a single value or an
	 * array.
	 * 
	 * @param name the field name
	 * @param values the value(s) assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, double... values) {
		return of(TypedProperties.of(name, values));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.
	 * 
	 * @param name the field name
	 * @param value the value assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, String value) {
		return of(TypedProperties.of(name, value));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.
	 * 
	 * @param name the field name
	 * @param value the value assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static Reference of(String name, boolean value) {
		return of(TypedProperties.of(name, value));
	}
	
	/**
	 * Constructs a reference containing the given field name and value.
	 * 
	 * @param name the field name
	 * @param value the value assigned to the field
	 * @return a new reference based on the name and value
	 */
	public static <T extends Enum<?>> Reference of(String name, T value) {
		return of(TypedProperties.of(name, value));
	}
	
	/**
	 * Constructs a reference to the root container.
	 * 
	 * @return a reference to the root container
	 */
	public static Reference root() {
		return of(TypedProperties.of());
	}

}