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
package org.moeaframework.mock;

import java.util.stream.IntStream;

import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.Variation;

public class MockVariation implements Variation {

	private final int arity;
	
	private final int offspring;
	
	public MockVariation(int arity) {
		this(arity, 1);
	}
		
	public MockVariation(int arity, int offspring) {
		super();
		this.arity = arity;
		this.offspring = offspring;
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
		Assert.assertEquals(arity, parents.length);
		return IntStream.range(0, offspring).mapToObj(i -> parents[0].copy()).toArray(Solution[]::new);
	}
	
}