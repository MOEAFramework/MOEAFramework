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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.Manifest;

/**
 * A schema that defines the structure of a {@link DataStore}, specifically detailing the required fields, their types,
 * and order.  This is useful for validation, ensuring the data being stored contains all required information.
 * <p>
 * If no fields are defined, this instead operates in <strong>schemaless</strong> mode, wherein such validations are
 * skipped and the structure is inferred from the reference only.  Fields are sorted according to their natural order.
 */
public class Schema {
	
	private final List<Field<?>> fields;
	
	/**
	 * Constructs a schema with the given fields.
	 * 
	 * @param fields the fields, which if empty operates in schemaless mode
	 */
	protected Schema(Field<?>... fields) {
		super();
		this.fields = List.of(fields);
	}
	
	/**
	 * Returns the number of fields defined by this schema.
	 * 
	 * @return the number of fields
	 */
	public int size() {
		return fields.size();
	}
	
	/**
	 * Returns the field at the given index.
	 * 
	 * @param index the index
	 * @return the field at the given index
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public Field<?> get(int index) {
		return fields.get(index);
	}
	
	/**
	 * Returns the field with the given name.
	 * 
	 * @param name the field name
	 * @return the field with the given name
	 * @throws IllegalArgumentException if no such field exists
	 */
	public Field<?> get(String name) {
		for (Field<?> field : fields) {
			if (field.matches(name)) {
				return field;
			}
		}
		
		throw new IllegalArgumentException("No field with name '" + name + "' exists");
	}
	
	/**
	 * Returns all fields defined by this schema in their designated order.
	 * 
	 * @return the fields
	 */
	public List<Field<?>> getFields() {
		return Collections.unmodifiableList(fields);
	}
	
	/**
	 * Resolves the given reference according to this schema.  This process validates that all required fields are
	 * defined and orders them per the schema.
	 * 
	 * @param reference the data reference
	 * @return the resolved fields and values according to this schema
	 * @throws IllegalArgumentException if the reference did not satisfy the requirements of this schema
	 */
	public List<Pair<Field<?>, String>> resolve(Reference reference) {
		List<Pair<Field<?>, String>> result = new ArrayList<>();
		
		Set<String> unusedFields = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		unusedFields.addAll(reference.fields());
		
		if (isSchemaless()) {
			for (String field : reference.fields()) {
				result.add(Pair.of(Field.named(field).asString(), reference.get(field)));
				unusedFields.remove(field);
			}
			
			result.sort(null);
		} else {
			for (Field<?> field : fields) {
				result.add(Pair.of(field, reference.get(field.getName())));
				unusedFields.remove(field.getName());
			}
		}
		
		if (!unusedFields.isEmpty()) {
			throw new IllegalArgumentException("Fields defined in reference were unused: " +
					unusedFields.stream().collect(Collectors.joining(",")));
		}
		
		return result;
	}

	/**
	 * Returns {@code true} if schemaless or has no defined fields; {@code false} otherwise
	 * 
	 * @return @code true} if schemaless; {@code false} otherwise
	 */
	public boolean isSchemaless() {
		return fields.isEmpty();
	}
	
	/**
	 * Updates the manifest with information about this schema.
	 * 
	 * @param manifest the manifest
	 */
	public void updateManifest(Manifest manifest) {
		if (!isSchemaless()) {
			for (int i = 0; i < fields.size(); i++) {
				manifest.setString("Field." + (i+1) + ".Name", fields.get(i).getName());
			}
		}
	}
	
	@Override
	public String toString() {
		return "Schema" + List.of(fields).stream().map(List::toString).collect(Collectors.joining(",", "(", ")"));
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(fields)
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

		Schema rhs = (Schema)obj;
		return new EqualsBuilder()
				.append(fields, rhs.fields)
				.isEquals();
	}
	
	/**
	 * Constructs a schema with the given fields.
	 * 
	 * @param fields the fields
	 * @return the schema
	 */
	@SafeVarargs
	public static Schema of(Field<?>... fields) {
		return new Schema(fields);
	}
	
	/**
	 * Constructs a schema without any defined fields (i.e., schemaless).
	 * 
	 * @return the schema
	 */
	public static Schema schemaless() {
		return new Schema();
	}

}
