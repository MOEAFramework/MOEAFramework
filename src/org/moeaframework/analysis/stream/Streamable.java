package org.moeaframework.analysis.stream;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Main entry point for using data streams.
 * <p>
 * Data streams are built upon Java's {@link Stream} API, providing a layer for manipulating and analyzing data in
 * a succinct manner.  Streams come in two forms, either a {@link DataStream} that contains only values or a
 * {@link Partition} that contains keyed values.
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
