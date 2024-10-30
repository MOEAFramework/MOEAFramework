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
package org.moeaframework.mock.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.Assert;

public class MockBiConsumer<T, U> implements BiConsumer<T, U> {
	
	private List<Pair<T, U>> calls;
	
	public MockBiConsumer() {
		super();
		this.calls = new ArrayList<>();
	}

	@Override
	public void accept(T t, U u) {
		calls.add(Pair.of(t, u));
	}
	
	public void assertCallCount(int count) {
		Assert.assertEquals(count, calls.size());
	}
	
	@SafeVarargs
	public final void assertCalls(Pair<T, U>... calls) {
		Assert.assertEquals(List.of(calls), this.calls);
	}
	
	public static <T, U> MockBiConsumer<T, U> of() {
		return new MockBiConsumer<>();
	}

}
