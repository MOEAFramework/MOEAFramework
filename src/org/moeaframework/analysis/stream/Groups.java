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

import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Groups items together using a "grouping key".  This grouping key can be the same as, or different from, the key
 * found in the {@link Partition}.
 * 
 * @param <G> the type of the grouping key
 * @param <K> the original key type
 * @param <V> the type of each value
 */
public class Groups<G, K, V> extends ImmutablePartition<G, Partition<K, V>> {
	
	public Groups(List<Pair<G, Partition<K, V>>> content) {
		super(content);
	}

	public Groups(Stream<Pair<G, Partition<K, V>>> stream) {
		this(stream.toList());
	}
	
	public Groups(DataStream<Pair<G, Partition<K, V>>> dataStream) {
		this(dataStream.stream());
	}
	
	public Partition<K, V> get(G key) {
		return stream().filter(x -> x.getKey().equals(key)).findAny().get().getValue();
	}
	
	@Override
	public Groups<G, K, V> sorted() {
		return new Groups<G, K, V>(stream().sorted());
	}
	
	@Override
	public Groups<G, K, V> sorted(Comparator<G> comparator) {
		return new Groups<G, K, V>(stream().sorted((x, y) ->
			comparator.compare(x.getKey(), y.getKey())));
	}

	/**
	 * Equivalent to calling {@link Partition#map(Function)} on each group, keeping the grouping intact.
	 * 
	 * @param <R> the result type
	 * @param map the map function
	 * @return the groups after applying the map function
	 */
	public <R> Groups<G, K, R> mapEach(Function<V, R> map) {
		return new Groups<G, K, R>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().map(map))));
	}
	
	/**
	 * Equivalent to calling {@link Partition#measure(Function)} on each group, keeping the grouping intact.
	 * 
	 * @param <R> the result type
	 * @param measure the measurement function
	 * @return the groups after applying the measurement function
	 */
	public <R> Partition<G, R> measureEach(Function<Stream<V>, R> measure) {
		return new ImmutablePartition<G, R>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().measure(measure))));
	}
	
	/**
	 * Equivalent to calling {@link Partition#reduce(BinaryOperator)} on each group, keeping the grouping intact.
	 * 
	 * @param op the reduction operator
	 * @return the groups after applying the reduction operator
	 */
	public Partition<G, V> reduceEach(BinaryOperator<V> op) {
		return new ImmutablePartition<G, V>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().reduce(op))));
	}
	
	/**
	 * Equivalent to calling {@link Partition#reduce(Object, BinaryOperator)} on each group, keeping the grouping
	 * intact.
	 * 
	 * @param op the reduction operator
	 * @return the groups after applying the reduction operator
	 */
	public Partition<G, V> reduceEach(V identity, BinaryOperator<V> op) {
		return new ImmutablePartition<G, V>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().reduce(identity, op))));
	}
	
	/**
	 * Equivalent to calling {@link Partition#groupBy(Function)} on each group, keeping the grouping intact.
	 * 
	 * @param grouping the grouping function
	 * @return the groups after applying the grouping function
	 */
	public <R> Groups<G, R, Partition<K, V>> groupEachBy(Function<K, R> grouping) {
		return new Groups<G, R, Partition<K, V>>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().groupBy(grouping))));
	}
	
}