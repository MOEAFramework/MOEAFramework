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
package org.moeaframework;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Counter<T> {
	
	private final Map<T, Integer> counters;
	
	public Counter() {
		super();
		this.counters = new HashMap<>();
	}
	
	public Set<T> values() {
		return counters.keySet();
	}
	
	public <S extends T> void incrementAll(S[] values) {
		for (S value : values) {
			incrementAndGet(value);
		}
	}
	
	public <S extends T> void incrementAll(Iterable<S> iterable) {
		for (S value : iterable) {
			incrementAndGet(value);
		}
	}
	
	public int incrementAndGet(T value) {
		Integer count = counters.get(value);
		count = count == null ? 1 : count + 1;
		counters.put(value, count);
		return count;
	}
	
	public int get(T value) {
		Integer count = counters.get(value);
		return count == null ? 0 : count;
	}

}
