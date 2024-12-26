/* Copyright 2009-2025 David Hadka
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.util.Iterators;

/**
 * A partition storing the intermediate, materialized content of the stream.
 * 
 * @param <K> the type of each key
 * @param <V> the type of each value
 */
public class ImmutablePartition<K, V> implements Partition<K, V> {
	
	private final List<Pair<K, V>> content;
	
	/**
	 * Constructs a new, empty partition.
	 */
	public ImmutablePartition() {
		this(new ArrayList<>());
	}
	
	/**
	 * Constructs a new partition with the given content.
	 * 
	 * @param list the list of key-value pairs
	 */
	public ImmutablePartition(List<Pair<K, V>> list) {
		super();
		this.content = list;
	}
	
	/**
	 * Constructs a new partition with the given content.
	 * 
	 * @param stream the stream of key-value pairs
	 */
	public ImmutablePartition(Stream<Pair<K, V>> stream) {
		this(stream.toList());
	}
	
	/**
	 * Constructs a new partition with the given content.
	 * 
	 * @param iterable the iterable of key-value pairs
	 */
	public ImmutablePartition(Iterable<Pair<K, V>> iterable) {
		this(Iterators.materialize(iterable));
	}
	
	@Override
	public int size() {
		return content.size();
	}
	
	@Override
	public Stream<Pair<K, V>> stream() {
		return content.stream();
	}
	
}