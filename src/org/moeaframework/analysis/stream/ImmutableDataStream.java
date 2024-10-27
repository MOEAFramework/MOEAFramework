package org.moeaframework.analysis.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ImmutableDataStream<V> implements DataStream<V> {
	
	protected final List<V> content;
	
	public ImmutableDataStream() {
		this(new ArrayList<>());
	}
	
	public ImmutableDataStream(List<V> content) {
		super();
		this.content = content;
	}
	
	public ImmutableDataStream(Stream<V> stream) {
		this(stream.toList());
	}
	
	@Override
	public int size() {
		return content.size();
	}
	
	@Override
	public V get(int index) {
		return content.get(index);
	}
	
	@Override
	public List<V> values() {
		return stream().toList();
	}
	
	@Override
	public Stream<V> stream() {
		return content.stream();
	}

}
