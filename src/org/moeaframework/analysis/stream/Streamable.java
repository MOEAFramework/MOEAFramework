/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.analysis.stream;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface for classes that support data streams.
 * <p>
 * Classes are not necessarily required to implement this interface.  They can alternatively extend one of the existing
 * data structures, such as {@link DataStream} or {@link Partition} directly, or use one of the static {@code of(...)}
 * methods in the aforementioned classes to convert a collection into a data stream.
 * 
 * @param <V> the type of each value in the stream
 */
public interface Streamable<V>  {
	
	/**
	 * Returns a stream of values represented by this object.
	 * 
	 * @return the stream of values
	 */
	public Stream<V> stream();
	
	/**
	 * Returns a {@link DataStream} with the values represented by this object.
	 * 
	 * @return the data stream
	 */
	public default DataStream<V> asDataStream() {
		return new ImmutableDataStream<V>(stream());
	}
	
	/**
	 * Returns a {@link Partition} with the values represented by this object.
	 * 
	 * @param <K> the type of the key
	 * @param key a function returning the key for each value
	 * @return the partition
	 */
	public default <K> Partition<K, V> asPartition(Function<V, K> key) {
		return new ImmutablePartition<K, V>(stream().map(x -> Pair.of(key.apply(x), x)));
	}

}
