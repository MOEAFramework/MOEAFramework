package org.moeaframework.experiment;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Partition<K, V> implements Map<K, V>, Iterable<V> {
	
	private final Map<K, V> partitions;
	
	public Partition() {
		super();
		this.partitions = new HashMap<>();
	}
	
	public Partition(Comparator<? super K> comparator) {
		super();
		this.partitions = new TreeMap<>(comparator);
	}
	
	@Override
	public int size() {
		return partitions.size();
	}

	@Override
	public boolean isEmpty() {
		return partitions.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return partitions.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return partitions.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return partitions.get(key);
	}

	@Override
	public V put(K key, V value) {
		return partitions.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return partitions.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		partitions.putAll(m);
	}

	@Override
	public void clear() {
		partitions.clear();
	}

	@Override
	public Collection<V> values() {
		return partitions.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return partitions.entrySet();
	}
	
	public Set<K> keySet() {
		return partitions.keySet();
	}

	@Override
	public Iterator<V> iterator() {
		return partitions.values().iterator();
	}
	
	public Stream<V> stream() {
		return partitions.values().stream();
	}
	
	public Partition<K, V> sort(Comparator<? super K> comparator) {
		Partition<K, V> sorted = new Partition<K, V>(comparator);
		sorted.putAll(this);
		return sorted;
	}
	
}
