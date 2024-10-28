package org.moeaframework.analysis.stream;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface for classes that support data streams.
 * <p>
 * Data streams are built upon Java's {@link Stream} API, providing a layer for manipulating and analyzing data in
 * a succinct manner.  Any class that represents a collection of values can enable data streams by implementing this
 * interface, or extending from one of the data stream types, such as {@link DataStream} or {@link Partition}.
 * 
 * @param <V> the type of each value in the stream
 */
public interface Streamable<V>  {
	
	public Stream<V> stream();
	
	public default DataStream<V> asDataStream() {
		return new ImmutableDataStream<V>(stream());
	}
	
	public default <K> Partition<K, V> asPartition(Function<V, K> key) {
		return new ImmutablePartition<K, V>(stream().map(x -> Pair.of(key.apply(x), x)));
	}

}
