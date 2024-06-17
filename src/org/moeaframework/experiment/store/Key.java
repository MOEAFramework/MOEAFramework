package org.moeaframework.experiment.store;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.PropertyNotFoundException;
import org.moeaframework.util.TypedProperties;

public class Key {
	
	private final Map<String, Object> entries;
	
	public Key() {
		super();
		entries = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public int size() {
		return entries.size();
	}
	
	<T extends Comparable<T> & Serializable> void set(String field, T value) {
		entries.put(field, value);
	}
	
	<T extends Comparable<T> & Serializable> void set(Field<T> field, T value) {
		set(field.getName(), value);
	}
	
	public Object get(String field) {
		return entries.get(field);
	}
	
	public <T extends Comparable<T> & Serializable> T get(Field<T> field) {
		return field.cast(get(field.getName()));
	}
	
	public boolean contains(String field) {
		return entries.containsKey(field);
	}
	
	public <T extends Comparable<T> & Serializable> boolean contains(Field<T> field) {
		return contains(field.getName());
	}
	
	public Set<String> getDefinedFields() {
		return Collections.unmodifiableSet(entries.keySet());
	}
	
	public TypedProperties toProperties() {
		TypedProperties properties = new TypedProperties();
		
		for (Map.Entry<String, Object> entry : entries.entrySet()) {
			properties.setString(entry.getKey(), entry.getValue().toString());
		}
		
		return properties;
	}
	
	public String[] getSegments(Schema schema) {
		String[] segments = new String[size()];
		boolean partial = false;

		Set<String> extraFields = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		extraFields.addAll(entries.keySet());
		
		for (int i = 0; i < Math.min(size(), schema.size()); i++) {
			Field<?> field = schema.get(i);
			Object value = get(field);
			
			if (value == null) {
				partial = true;
			} else {
				if (partial) {
					throw new DataStoreException("Key is missing fields or is not a proper prefix of the schema");
				}
				
				if (!field.getType().isInstance(value)) {
					throw new DataStoreException("Key contains incorrect type for '" + field.getName() +
							", expected " + field.getType().getName() + " but was " + value.getClass().getName());
				}
				
				segments[i] = value.toString();
				extraFields.remove(field.getName());
			}
		}
		
		if (extraFields.size() > 0) {
			throw new DataStoreException("Schema validation failed, key contains extra fields " +
					extraFields.stream().collect(Collectors.joining(", ")));
		}
		
		return segments;
	}

	@Override
	public String toString() {
		return entries.entrySet().stream()
				.map(x -> x.getKey() + "=" + x.getValue())
				.collect(Collectors.joining(",", "[", "]"));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(entries)
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

		Key rhs = (Key)obj;
		return new EqualsBuilder()
				.append(entries, rhs.entries)
				.isEquals();
	}
	
	public static Key of() {
		return new Key();
	}
	
	public static <T extends Comparable<T> & Serializable> Key of(String fieldName, T value) {
		Key key = new Key();
		key.set(fieldName, value);
		return key;
	}
	
	public static <T extends Comparable<T> & Serializable> Key of(Field<T> field, T value) {
		return of(field.getName(), value);
	}
	
	public static Key join(Key... keys) {
		Key joinKey = new Key();
		
		for (Key key : keys) {
			joinKey.entries.putAll(key.entries);
		}
		
		return joinKey;
	}
	
	public static Key from(Schema schema, TypedProperties properties) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create key prefix");
		}
		
		List<Field<?>> fields = schema.getFields();
		Key key = new Key();
		
		for (int i = 0; i < fields.size(); i++) {
			Field<?> field = fields.get(i);
			
			try {
				key.set(field.getName(), field.valueOf(properties.getString(field.getName())));
			} catch (PropertyNotFoundException e) {
				throw new DataStoreException("Field '" + field.getName() + "' not found in properties");
			}
		}
		
		return key;
	}

	@SafeVarargs
	public static Key from(Schema schema, Object... values) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create key prefix");
		}
		
		List<Field<?>> fields = schema.getFields();
		
		if (values.length > fields.size()) {
			throw new DataStoreException("The number of values exceeds the number of fields in the schema");
		}
		
		Key key = new Key();
		
		for (int i = 0; i < values.length; i++) {
			Field<?> field = fields.get(i);
			key.set(field.getName(), field.cast(values[i]));
		}
		
		return key;
	}
	
	public static Key prefix(Schema schema, TypedProperties properties, Field<?> field) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create key prefix");
		}
		
		Key key = new Key();
		
		for (int i = 0; i < schema.size(); i++) {
			Field<?> currentField = schema.get(i);
			key.set(currentField.getName(), currentField.valueOf(properties.getString(currentField.getName())));
			
			if (currentField.equals(field)) {
				break;
			}
		}
		
		return key;
	}
	
	public static Key prefix(Schema schema, TypedProperties properties, String fieldName) {
		return prefix(schema, properties, schema.get(fieldName));
	}
	
	public static Key prefix(Schema schema, TypedProperties properties, int depth) {
		if (depth <= 0) {
			return Key.of();
		} else {
			return prefix(schema, properties, schema.get(depth - 1));
		}
	}

}