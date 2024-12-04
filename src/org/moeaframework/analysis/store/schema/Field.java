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
package org.moeaframework.analysis.store.schema;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.Named;

/**
 * A field defined in a schema.  Fields are case-insensitive and provide methods for ordering and matching fields
 * in a case-insensitive manner.
 * 
 * @param <T> the type of this field
 */
public class Field<T extends Comparable<? super T>> implements Comparable<Field<T>>, Named {
	
	private final String name;
	
	private final String normalizedName;
	
	private final Class<T> type;
		
	/**
	 * Constructs a new field with the given name and type.
	 * 
	 * @param name the field name
	 * @param type the field type
	 */
	Field(String name, Class<T> type) {
		super();
		this.name = name;
		this.normalizedName = normalize(name);
		this.type = type;
	}
	
	/**
	 * Produces a normalized version of that name that allows for case-insensitive operations.  This is based on the
	 * implementation of Apache Commons CaseInsensitiveMap.
	 * 
	 * @param name the original field name
	 * @return the normalized field name
	 * @see <a href="https://issues.apache.org/jira/browse/COLLECTIONS-294">COLLECTIONS-294</a>
	 */
	private final String normalize(String name) {
		final char[] chars = name.toCharArray();
		
		for (int i = chars.length - 1; i >= 0; i--) {
			chars[i] = Character.toLowerCase(Character.toUpperCase(chars[i]));
		}
		
		return String.valueOf(chars);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the normalized name of this field, which is safe to use in case-insensitive operations.
	 * 
	 * @return the normalized name
	 */
	public String getNormalizedName() {
		return normalizedName;
	}
	
	/**
	 * Returns the type of this field.
	 * 
	 * @return the type
	 */
	public Class<T> getType() {
		return type;
	}
	
	/**
	 * Returns the normalized value, which is safe to use in case-insensitive operations.
	 * 
	 * @param value the original value
	 * @return the normalized value
	 */
	public String getNormalizedValue(String value) {
		return normalize(value);
	}
	
	/**
	 * Returns the normalized value, which is safe to use in case-insensitive operations.
	 * 
	 * @param reference the reference from which the value is read
	 * @return the normalized value
	 */
	public String getNormalizedValue(Reference reference) {
		return normalize(reference.get(name));
	}
	
	@Override
	public String toString() {
		return "Field(" + name + ":" + type.getSimpleName() + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(normalizedName)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		Field<?> rhs = (Field<?>)obj;
		
		return new EqualsBuilder()
				.append(normalizedName, rhs.normalizedName)
				.isEquals();
	}
	
	@Override
	public int compareTo(Field<T> rhs) {
		return String.CASE_INSENSITIVE_ORDER.compare(normalizedName, rhs.normalizedName);
	}
	
	/**
	 * Returns {@code true} if this field matches the given name; {@code false} otherwise.
	 * 
	 * @param name the name
	 * @return {@code true} if this field matches the given name; {@code false} otherwise
	 */
	public boolean matches(String name) {
		return normalizedName.equals(normalize(name));
	}
	
	/**
	 * Constructs a field builder with the given name.
	 * 
	 * @param name the field name
	 * @return the field builder for configuring this field
	 */
	public static FieldBuilder named(String name) {
		return new FieldBuilder(name);
	}
	
}