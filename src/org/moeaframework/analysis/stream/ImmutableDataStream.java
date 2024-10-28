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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.moeaframework.util.Iterators;

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
	
	public ImmutableDataStream(Iterable<V> iterable) {
		this(Iterators.materialize(iterable));
	}
	
	@Override
	public Stream<V> stream() {
		return content.stream();
	}

}
