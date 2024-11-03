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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
	
	public default Reference extend(String name, String value) {
		return new ExtendedReference(this, name, value);
	}
	
	public static Reference of(TypedProperties properties) {
		return new TypedPropertiesReference(properties);
	}
	
	static abstract class AbstractReference implements Reference {
		
		public AbstractReference() {
			super();
		}

		@Override
		public String toString() {
			return "Reference" + fields().stream().map(x -> x + "=" + get(x)).collect(Collectors.joining(",", "(", ")"));
		}
		
	}
	
	static class TypedPropertiesReference extends AbstractReference {
		
		private final TypedProperties properties;
		
		public TypedPropertiesReference(TypedProperties properties) {
			super();
			this.properties = properties;
		}
		
		public Set<String> fields() {
			return Collections.unmodifiableSet(properties.keySet());
		}
		
		public String get(String field) {
			return properties.getString(field);
		}
		
	}
		
	static class ExtendedReference extends AbstractReference {
		
		private final Reference reference;
		
		private final String name;
		
		private final String value;
		
		public ExtendedReference(Reference reference, String name, String value) {
			super();
			this.reference = reference;
			this.name = name;
			this.value = value;
		}
		
		public Set<String> fields() {
			Set<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			result.addAll(reference.fields());
			result.add(name);
			return result;
		}
		
		public String get(String name) {
			if (this.name.equalsIgnoreCase(name)) {
				return value;
			}
			
			return reference.get(name);
		}
		
	}

}