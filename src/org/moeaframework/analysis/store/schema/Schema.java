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
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.Manifest;

/**
 * Defines the specific fields, including their order, that must be defined by a {@link Key}.
 */
public class Schema {
	
	private final List<Field<?>> fields;
	
	protected Schema(Field<?>... fields) {
		super();
		this.fields = List.of(fields);
	}
	
	public int size() {
		return fields.size();
	}
	
	public Field<?> get(int index) {
		return fields.get(index);
	}
	
	public Field<?> get(String name) {
		for (Field<?> field : fields) {
			if (field.matches(name)) {
				return field;
			}
		}
		
		throw new IllegalArgumentException("No field with name '" + name + "' exists");
	}
	
	public List<Field<?>> getFields() {
		return Collections.unmodifiableList(fields);
	}
	
	public List<Pair<Field<?>, String>> project(Reference reference) {
		List<Pair<Field<?>, String>> result = new ArrayList<>();
		
		if (isSchemaless()) {
			for (String field : reference.fields()) {
				result.add(Pair.of(Field.named(field).asString(), reference.get(field)));
			}
			
			result.sort(null);
		} else {
			for (Field<?> field : fields) {
				result.add(Pair.of(field, reference.get(field.getName())));
			}
		}
		
		return result;
	}

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
		return "Schema" + List.of(fields).stream().map(x -> x.toString()).collect(Collectors.joining(",", "(", ")"));
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
	
	@SafeVarargs
	public static Schema of(Field<?>... fields) {
		return new Schema(fields);
	}
	
	public static Schema schemaless() {
		return new Schema();
	}

}
