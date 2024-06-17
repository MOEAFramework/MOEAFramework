package org.moeaframework.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

public class Samples implements Iterable<TypedProperties> {
	
	private final Schema schema;

	private final List<TypedProperties> samples;
	
	public Samples(Schema schema) {
		super();
		this.schema = schema;
		this.samples = new ArrayList<>();
	}
	
	public Samples(Schema schema, Collection<TypedProperties> samples) {
		this(schema);
		addAll(samples);
	}
	
	public int size() {
		return samples.size();
	}
	
	public boolean isEmpty() {
		return samples.isEmpty();
	}
	
	public void add(TypedProperties sample) {
		this.samples.add(sample);
	}
	
	public void addAll(Collection<TypedProperties> samples) {
		this.samples.addAll(samples);
	}

	@Override
	public Iterator<TypedProperties> iterator() {
		return samples.iterator();
	}
	
	public Samples copy() {
		return new Samples(schema, samples);
	}
	
	public void save(DataStore dataStore) throws IOException {
		try (TransactionalOutputStream out = dataStore.writer(Key.of(), DataType.SAMPLES).asBinary();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeInt(size());
			
			for (TypedProperties sample : samples) {
				try (StringWriter writer = new StringWriter()) {
					sample.store(writer);
					oos.writeUTF(writer.toString());
				}
			}
			
			out.commit();
		}
	}
	
	public void load(DataStore dataStore) throws IOException {
		try (InputStream out = dataStore.reader(Key.of(), DataType.SAMPLES).asBinary();
				ObjectInputStream ois = new ObjectInputStream(out)) {
			int size = ois.readInt();
			
			for (int i = 0; i < size; i++) {			
				try (StringReader reader = new StringReader(ois.readUTF())) {
					TypedProperties sample = new TypedProperties();
					sample.load(reader);
					samples.add(sample);
				}
			}
		}
	}
	
	public Collection<?> distinctValues(String fieldName) {
		return distinctValues(schema.get(fieldName));
	}
	
	public <T extends Comparable<T> & Serializable> Collection<T> distinctValues(Field<T> field) {
		return samples.stream().map(x -> field.valueOf(x.getString(field.getName()))).distinct().toList();
	}
	
	public Collection<Key> getKeys() {
		Set<Key> keys = new HashSet<Key>();
		
		for (TypedProperties sample : samples) {
			keys.add(Key.from(schema, sample));
		}
		
		return keys;
	}
	
	public TypedProperties get(int index) {
		return samples.get(index);
	}
	
	public TypedProperties get(Key key) {
		Samples samples = filter(key);
		
		if (samples.size() == 0) {
			throw new IllegalArgumentException("No samples match the key " + key);
		} else if (samples.size() > 1) {
			throw new IllegalArgumentException("Multiple samples match the key " + key + ", use filter instead");
		} else {
			return samples.get(0);
		}
	}
	
	public Map<Key, Samples> partition(int depth) {
		if (depth <= 0) {
			Map<Key, Samples> result = new HashMap<>();
			result.put(Key.of(), copy());
			return result;
		} else {
			return partition(schema.get(depth - 1));
		}
	}
	
	public Map<Key, Samples> partition(String fieldName) {
		return partition(schema.get(fieldName));
	}
	
	public Map<Key, Samples> partition(Field<?> field) {
		Map<Key, Samples> result = new HashMap<>();
		
		for (TypedProperties sample : samples) {
			Key key = Key.prefix(schema, sample, field);
			Samples samples = result.get(key);
			
			if (samples == null) {
				samples = new Samples(schema);
				result.put(key, samples);
			}
			
			samples.add(sample);
		}

		return result;
	}
	
	public Map<?, Samples> groupBy(String fieldName) {
		return groupBy(schema.get(fieldName));
	}
	
	public <T extends Comparable<T> & Serializable> Map<T, Samples> groupBy(Field<T> field) {
		Map<T, Samples> result = new HashMap<>();
		
		for (TypedProperties sample : samples) {
			T value = field.valueOf(sample.getString(field.getName()));
			Samples samples = result.get(value);
			
			if (samples == null) {
				samples = new Samples(schema);
				result.put(value, samples);
			}
			
			samples.add(sample);
		}

		return result;
	}
	
	public Samples filter(Predicate<TypedProperties> predicate) {
		Samples result = new Samples(schema);
		
		for (TypedProperties sample : samples) {
			if (predicate.test(sample)) {
				result.add(sample);
			}
		}
		
		return result;
	}
	
	public Samples filter(Key key) {
		return filter(sample -> {
			for (String fieldName : key.getDefinedFields()) {
				if (!sample.contains(fieldName) ||
						!sample.getString(fieldName).equals(key.get(fieldName).toString())) {
					return false;
				}
			}
			
			return true;
		});
	}
	
	public Samples filter(String fieldName, Object value) {
		Field<?> field = schema.get(fieldName);
		
		return filter(sample -> sample.contains(fieldName) &&
				field.valueOf(sample.getString(fieldName)).equals(value));
	}
	
	public <T extends Comparable<T> & Serializable> Samples filter(Field<T> field, T value) {
		return filter(sample -> sample.contains(field.getName()) &&
				field.valueOf(sample.getString(field.getName())).equals(value));
	}

}
