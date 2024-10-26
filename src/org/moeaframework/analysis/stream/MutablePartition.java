package org.moeaframework.analysis.stream;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public class MutablePartition<K, V> extends ImmutablePartition<K, V> {
	
	public MutablePartition() {
		super();
	}
	
	public MutablePartition(List<Pair<K, V>> content) {
		super(content);
	}
	
	public MutablePartition(Stream<Pair<K, V>> stream) {
		super(stream);
	}

	public void add(K key, V value) {
		content.add(Pair.of(key, value));
	}
	
}