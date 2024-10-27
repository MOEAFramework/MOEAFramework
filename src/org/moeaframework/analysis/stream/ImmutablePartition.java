package org.moeaframework.analysis.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public class ImmutablePartition<K, V> implements Partition<K, V> {
	
	protected final List<Pair<K, V>> content;
	
	public ImmutablePartition() {
		this(new ArrayList<>());
	}
	
	public ImmutablePartition(List<Pair<K, V>> content) {
		super();
		this.content = content;
	}
	
	public ImmutablePartition(Stream<Pair<K, V>> stream) {
		this(stream.toList());
	}
	
	public int size() {
		return content.size();
	}
	
	public Pair<K, V> get(int index) {
		return content.get(index);
	}
	
	@Override
	public Stream<Pair<K, V>> stream() {
		return content.stream();
	}
	
	
}