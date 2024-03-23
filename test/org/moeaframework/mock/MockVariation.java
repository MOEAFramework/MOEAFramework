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
package org.moeaframework.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class MockVariation implements Variation {

	private final int arity;
	
	private final AtomicInteger count;
	
	public MockVariation(int arity) {
		super();
		this.arity = arity;
		
		count = new AtomicInteger();
	}
	
	@Override
	public String getName() {
		return "mock";
	}
	
	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		count.incrementAndGet();
		Assert.assertEquals(arity, parents.length);
		return new Solution[] { parents[0].copy() };
	}
	
	public int getCallCount() {
		return count.get();
	}
	
}