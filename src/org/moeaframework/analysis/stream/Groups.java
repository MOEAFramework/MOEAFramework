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

public class Groups<T, K, V> extends ImmutablePartition<T, Partition<K, V>> {
	
	public Groups(List<Pair<T, Partition<K, V>>> content) {
		super(content);
	}

	public Groups(Stream<Pair<T, Partition<K, V>>> stream) {
		this(stream.toList());
	}
	
	public Groups(DataStream<Pair<T, Partition<K, V>>> dataStream) {
		this(dataStream.stream());
	}
	
	public Partition<K, V> get(T key) {
		return stream().filter(x -> x.getKey().equals(key)).findAny().get().getValue();
	}
	
	@Override
	public Groups<T, K, V> sorted() {
		return new Groups<T, K, V>(stream().sorted());
	}
	
	public Groups<T, K, V> sorted(Comparator<T> comparator) {
		return new Groups<T, K, V>(stream().sorted((x, y) ->
			comparator.compare(x.getKey(), y.getKey())));
	}

	public <R> Groups<T, K, R> mapEach(Function<V, R> op) {
		return new Groups<T, K, R>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().map(op))));
	}
	
	public <R> Partition<T, R> measureEach(Function<Stream<V>, R> measure) {
		return new ImmutablePartition<T, R>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().measure(measure))));
	}
	
	public Partition<T, V> reduceEach(BinaryOperator<V> op) {
		return new ImmutablePartition<T, V>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().reduce(op))));
	}
	
	public Partition<T, V> reduceEach(V identity, BinaryOperator<V> op) {
		return new ImmutablePartition<T, V>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().reduce(identity, op))));
	}
	
	public <R> Groups<T, R, Partition<K, V>> groupEachBy(Function<K, R> grouping) {
		return new Groups<T, R, Partition<K, V>>(stream().map(x ->
			Pair.of(x.getKey(), x.getValue().groupBy(grouping))));
	}
	
}