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
 * Interface for classes that support data streams.  Classes can implement one of the data structures, such as
 * {@link DataStream} or {@link Partition} directly, or this interface as an entry point for using data streams.
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
