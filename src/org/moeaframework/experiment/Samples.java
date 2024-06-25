package org.moeaframework.experiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataStoreException;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;

public class Samples implements Iterable<Sample> {
	
	private static final String HEADER_TEXT = "Samples: ";
	
	private final Schema schema;

	private final List<Sample> samples;
	
	public Samples(Schema schema) {
		super();
		this.schema = schema;
		this.samples = Collections.synchronizedList(new ArrayList<>());
	}
	
	public Samples(Schema schema, Collection<Sample> samples) {
		this(schema);
		addAll(samples);
	}
	
	public Samples(Schema schema, Iterable<Sample> samples) {
		this(schema);
		addAll(samples);
	}
	
	public Schema getSchema() {
		return schema;
	}
	
	public int size() {
		return samples.size();
	}
	
	public boolean isEmpty() {
		return samples.isEmpty();
	}
	
	void add(Sample sample) {
		this.samples.add(sample);
	}

	void addAll(Collection<Sample> samples) {
		this.samples.addAll(samples);
	}
	
	void addAll(Iterable<Sample> samples) {
		for (Sample sample : samples) {
			add(sample);
		}
	}

	@Override
	public Iterator<Sample> iterator() {
		return Collections.unmodifiableList(samples).iterator();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(schema)
				.append(samples)
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

		Samples rhs = (Samples)obj;
		return new EqualsBuilder()
				.append(schema, rhs.schema)
				.append(samples, rhs.samples)
				.isEquals();
	}
	
	public void save(DataStore dataStore) throws IOException {
		try (TransactionalWriter out = dataStore.writer(Key.of(), DataType.SAMPLES).asText()) {
			out.write(HEADER_TEXT);
			out.write(Integer.toString(samples.size()));
			out.write(System.lineSeparator());
			out.write("#");
			out.write(System.lineSeparator());
			
			for (Sample sample : samples) {
				sample.store(out);
				out.write("#");
				out.write(System.lineSeparator());
			}
			
			out.commit();
		}
	}
	
	public void load(DataStore dataStore) throws IOException {
		samples.clear();
		
		try (BufferedReader in = new BufferedReader(dataStore.reader(Key.of(), DataType.SAMPLES).asText())) {
			StringBuilder content = new StringBuilder();
			String line = null;
			
			if (!(line = in.readLine()).startsWith(HEADER_TEXT)) {
				throw new DataStoreException("Missing header, not a samples file");
			}
			
			int expectedSize = Integer.parseInt(line.substring(HEADER_TEXT.length()));
			
			if (!(line = in.readLine()).startsWith("#")) {
				throw new DataStoreException("Missing # indicating end of header");
			}
			
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					try (StringReader reader = new StringReader(content.toString())) {
						Sample sample = new Sample();
						sample.load(reader);
						samples.add(sample);
					}
					
					content = new StringBuilder();
				} else {
					content.append(line);
					content.append(System.lineSeparator());
				}
			}
			
			if (samples.size() != expectedSize) {
				throw new DataStoreException("Incorrect number of samples, expected " + expectedSize + " but read " +
						samples.size());
			}
		}
	}
	
	public Collection<?> distinctValues(String fieldName) {
		return distinctValues(schema.get(fieldName));
	}
	
	public <T extends Comparable<? super T> & Serializable> Collection<T> distinctValues(Field<T> field) {
		return samples.stream().map(x -> field.valueOf(x)).distinct().toList();
	}
	
	public Set<Key> keySet() {
		Set<Key> keys = new HashSet<Key>();
		
		for (Sample sample : samples) {
			keys.add(Key.from(schema, sample));
		}
		
		return keys;
	}
	
	public Sample get(int index) {
		return samples.get(index);
	}
	
	public Sample get(Key key) {
		Samples samples = filter(key);
		
		if (samples.size() == 0) {
			throw new IllegalArgumentException("No samples match the key " + key);
		} else if (samples.size() > 1) {
			throw new IllegalArgumentException("Multiple samples match the key " + key + ", use filter instead");
		} else {
			return samples.get(0);
		}
	}
	
	public Partition<Key, PartitionedSamples> partition(int depth) {
		if (depth <= 0) {
			PartitionedSamples partitionedSamples = new PartitionedSamples(schema, Key.of());
			partitionedSamples.addAll(this);
			
			Partition<Key, PartitionedSamples> partition = new Partition<>();
			partition.put(partitionedSamples.getPartitionKey(), partitionedSamples);
			return partition;
		} else {
			return partition(schema.get(depth - 1));
		}
	}
	
	public Partition<Key, PartitionedSamples> partition(String fieldName) {
		return partition(schema.get(fieldName));
	}
	
	public Partition<Key, PartitionedSamples> partition(Field<?> field) {
		Partition<Key, PartitionedSamples> result = new Partition<>();
		
		for (Sample sample : samples) {
			Key key = Key.prefix(schema, sample, field);
			PartitionedSamples samples = result.get(key);
			
			if (samples == null) {
				samples = new PartitionedSamples(schema, key);
				result.put(key, samples);
			}
			
			samples.add(sample);
		}

		return result;
	}
	
	public Samples filter(Predicate<Sample> predicate) {
		Samples result = new Samples(schema);
		
		for (Sample sample : samples) {
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
				field.valueOf(sample).equals(value));
	}
	
	public <T extends Comparable<T> & Serializable> Samples filter(Field<T> field, T value) {
		return filter(sample -> sample.contains(field.getName()) &&
				field.valueOf(sample).equals(value));
	}
	
	public boolean matchesStoredSamples(DataStore dataStore) throws IOException {
		if (!dataStore.contains(Key.of(), DataType.SAMPLES)) {
			return false;
		}
		
		Samples storedSamples = new Samples(schema);
		storedSamples.load(dataStore);
		
		return equals(storedSamples);
	}

}
