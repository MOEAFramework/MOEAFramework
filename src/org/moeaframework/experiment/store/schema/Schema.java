package org.moeaframework.experiment.store.schema;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Schema {
	
	private final Field<?>[] fields;
	
	protected Schema(Field<?>[] fields) {
		super();
		this.fields = fields;
	}
	
	public int size() {
		return fields.length;
	}
	
	public Field<?> get(int index) {
		return fields[index];
	}
	
	public Field<?> get(String name) {
		for (Field<?> field : fields) {
			if (field.getName().equalsIgnoreCase(name)) {
				return field;
			}
		}
		
		throw new IllegalArgumentException("No field with name '" + name + "' exists");
	}
	
	public List<Field<?>> getFields() {
		return List.of(fields);
	}
	
	public boolean isDefined(String name) {
		for (Field<?> field : fields) {
			if (field.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isSchemaless() {
		return fields.length == 0;
	}
	
	@Override
	public String toString() {
		return "Schema" + List.of(fields).stream().map(x -> x.toString()).collect(Collectors.joining(",", "[", "]"));
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
		return new Schema(new Field<?>[0]);
	}

}
