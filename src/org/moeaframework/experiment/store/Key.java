package org.moeaframework.experiment.store;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.PropertyNotFoundException;

public class Key {
	
	private final Map<String, Comparable<?>> entries;
	
	public Key() {
		super();
		entries = Collections.synchronizedMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
	}
	
	public int size() {
		return entries.size();
	}
	
	<T extends Comparable<? super T> & Serializable> void set(String fieldName, T value) {
		entries.put(fieldName, value);
	}
	
	<T extends Comparable<? super T> & Serializable> void set(Field<T> field, T value) {
		set(field.getName(), value);
	}
	
	public Comparable<?> get(String fieldName) {
		return entries.get(fieldName);
	}
	
	public <T extends Comparable<? super T> & Serializable> T get(Field<T> field) {
		return field.cast(get(field.getName()));
	}
	
	public boolean defines(String fieldName) {
		return entries.containsKey(fieldName);
	}
	
	public <T extends Comparable<? super T> & Serializable> boolean defines(Field<T> field) {
		return defines(field.getName());
	}
	
	public Set<String> getDefinedFields() {
		return Collections.unmodifiableSet(entries.keySet());
	}
	
	public String getDisplayName() {
		if (size() == 0) {
			return "<blank>";
		} else if (size() == 1) {
			return entries.values().iterator().next().toString();
		} else {
			return toString();
		}
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
	
	public Key parent(Schema schema) {
		Key result = new Key();
		result.entries.putAll(entries);
		
		if (result.size() > 0) {
			result.entries.remove(schema.get(result.size()-1).getName());
		}
		
		return result;
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
	
	public static <T extends Comparable<? super T> & Serializable> Key of(String fieldName, T value) {
		Key key = new Key();
		key.set(fieldName, value);
		return key;
	}
	
	public static <T extends Comparable<? super T> & Serializable> Key of(Field<T> field, T value) {
		return of(field.getName(), value);
	}
	
	public static Key join(Key... keys) {
		Key joinKey = new Key();
		
		for (Key key : keys) {
			joinKey.entries.putAll(key.entries);
		}
		
		return joinKey;
	}
	
	public static Key from(Schema schema, Sample sample) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create key prefix");
		}
		
		List<Field<?>> fields = schema.getFields();
		Key key = new Key();
		
		for (int i = 0; i < fields.size(); i++) {
			Field<?> field = fields.get(i);
			
			try {
				key.set(field.getName(), field.valueOf(sample));
			} catch (PropertyNotFoundException e) {
				throw new DataStoreException("Field '" + field.getName() + "' not found in sample");
			}
		}
		
		return key;
	}

	@SafeVarargs
	public static <T extends Comparable<? super T> & Serializable> Key from(Schema schema, T... values) {
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
	
	public static Key prefix(Schema schema, Sample sample, Field<?> field) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create key prefix");
		}
		
		Key key = new Key();
		
		for (int i = 0; i < schema.size(); i++) {
			Field<?> currentField = schema.get(i);
			key.set(currentField.getName(), currentField.valueOf(sample));
			
			if (currentField.equals(field)) {
				break;
			}
		}
		
		return key;
	}
	
	public static Key prefix(Schema schema, Sample sample, String fieldName) {
		return prefix(schema, sample, schema.get(fieldName));
	}
	
	public static Key prefix(Schema schema, Sample sample, int depth) {
		if (depth <= 0) {
			return Key.of();
		} else {
			return prefix(schema, sample, schema.get(depth - 1));
		}
	}
	
	public static <T extends Comparable<? super T> & Serializable> Comparator<Key> compare(String fieldName) {
		return new Comparator<Key>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(Key k1, Key k2) {
				Comparable v1 = k1.get(fieldName);
				Comparable v2 = k2.get(fieldName);
				
				if (v1 == null && v2 == null) {
					return 0;
				} else if (v1 == null) {
					return 1;
				} else if (v2 == null) {
					return -1;
				}
				
				if (!v1.getClass().isInstance(v2)) {
					throw new DataStoreException("Can not compare objects of different types, expected " +
							v1.getClass().getName() + " but given " + v2.getClass().getName());
				}
				
				return v1.compareTo(v2);
			}
			
		};
	}
	
	public static <T extends Comparable<? super T> & Serializable> Comparator<Key> compare(Field<T> field) {
		return new Comparator<Key>() {

			@Override
			public int compare(Key k1, Key k2) {
				T v1 = k1.get(field);
				T v2 = k2.get(field);
				
				if (v1 == null && v2 == null) {
					return 0;
				} else if (v1 == null) {
					return 1;
				} else if (v2 == null) {
					return -1;
				}
				
				return v1.compareTo(v2);
			}
			
		};
	}
	
	public static Comparator<Key> compare(Schema schema) {
		if (schema.isSchemaless()) {
			throw new DataStoreException("Must have a defined schema to create comparator");
		}
		
		Comparator<Key> comparator = Key.compare(schema.get(0));
		
		for (int i = 1; i < schema.size(); i++) {
			comparator = comparator.thenComparing(Key.compare(schema.get(i)));
		}
		
		return comparator;
	}

}