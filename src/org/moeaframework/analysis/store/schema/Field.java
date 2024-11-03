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

import java.util.function.Function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.analysis.store.Reference;

public class Field<T extends Comparable<? super T>> implements Comparable<Field<T>> {
	
	private final String name;
	
	private final String normalizedName;
	
	private final Class<T> type;
	
	private final Function<String, T> valueOf;
	
	Field(String name, Class<T> type, Function<String, T> valueOf) {
		super();
		this.name = name;
		this.normalizedName = normalize(name);
		this.type = type;
		this.valueOf = valueOf;
	}
	
	/**
	 * Produces a normalized version of that name that allows for case-insensitive operations.  This is based on the
	 * implementation of Apache Commons CaseInsensitiveMap.
	 * 
	 * @param name the original field name
	 * @return the normalized field name
	 * @see https://issues.apache.org/jira/browse/COLLECTIONS-294
	 */
	private final String normalize(String name) {
		final char[] chars = name.toCharArray();
		
		for (int i = chars.length - 1; i >= 0; i--) {
			chars[i] = Character.toLowerCase(Character.toUpperCase(chars[i]));
		}
		
		return new String(chars);
	}
	
	public T cast(Object object) {
		return type.cast(object);
	}

	public boolean isDefined(Reference key) {
		return key.fields().contains(name);
	}

	public T valueOf(Reference key) {
		return valueOf.apply(key.get(name));
	}
	
	public String getName() {
		return name;
	}
	
	public String getNormalizedName() {
		return normalizedName;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "Field(" + name + ":" + type.getSimpleName() + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
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
	
	public boolean matches(String name) {
		return normalizedName.equals(normalize(name));
	}
	
	public static FieldBuilder named(String name) {
		return new FieldBuilder(name);
	}

	@Override
	public int compareTo(Field<T> rhs) {
		return String.CASE_INSENSITIVE_ORDER.compare(normalizedName, rhs.normalizedName);
	}
	
}