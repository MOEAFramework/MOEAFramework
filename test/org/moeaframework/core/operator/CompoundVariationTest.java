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
package org.moeaframework.core.operator;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.mock.MockVariation;

public class CompoundVariationTest {

	@Test
	public void testMutationOnly() {
		CallCounter<Variation> v1 = CallCounter.of(new MockVariation(1, 1));
		CompoundVariation variation = new CompoundVariation(v1.getProxy());
		
		Assert.assertEquals("mock", variation.getName());
		Assert.assertEquals(1, variation.getArity());
		
		Solution[] parents = MockSolution.of().buildArray(1);
		Assert.assertEquals(1, variation.evolve(parents).length);
		Assert.assertEquals(1, v1.getTotalCallCount("evolve"));
	}
	
	@Test
	public void testGA() {
		CallCounter<Variation> v1 = CallCounter.of(new MockVariation(2, 2));
		CallCounter<Variation> v2 = CallCounter.of(new MockVariation(1, 1));
		CompoundVariation variation = new CompoundVariation(v1.getProxy(), v2.getProxy());
		
		Assert.assertEquals("mock+mock", variation.getName());
		Assert.assertEquals(2, variation.getArity());
		
		Solution[] parents = MockSolution.of().buildArray(2);
		Assert.assertEquals(2, variation.evolve(parents).length);
		
		Assert.assertEquals(1, v1.getTotalCallCount("evolve"));
		Assert.assertEquals(2, v2.getTotalCallCount("evolve"));
	}
	
	@Test
	public void testMultipleCrossover() {
		CallCounter<Variation> v1 = CallCounter.of(new MockVariation(3, 2));
		CallCounter<Variation> v2 = CallCounter.of(new MockVariation(2, 2));
		CallCounter<Variation> v3 = CallCounter.of(new MockVariation(1, 1));
		CompoundVariation variation = new CompoundVariation(v1.getProxy(), v2.getProxy(), v3.getProxy());
		variation.setName("complex");
		
		Assert.assertEquals("complex", variation.getName());
		Assert.assertEquals(3, variation.getArity());
		
		Solution[] parents = MockSolution.of().buildArray(3);
		Assert.assertEquals(2, variation.evolve(parents).length);
		
		Assert.assertEquals(1, v1.getTotalCallCount("evolve"));
		Assert.assertEquals(1, v2.getTotalCallCount("evolve"));
		Assert.assertEquals(2, v3.getTotalCallCount("evolve"));
	}
	
	@Test
	public void testMultipleOfParents() {
		CallCounter<Variation> v1 = CallCounter.of(new MockVariation(4, 4));
		CallCounter<Variation> v2 = CallCounter.of(new MockVariation(2, 2));
		CallCounter<Variation> v3 = CallCounter.of(new MockVariation(1, 1));
		CompoundVariation variation = new CompoundVariation(v1.getProxy(), v2.getProxy(), v3.getProxy());
		
		Assert.assertEquals(4, variation.getArity());
		
		Solution[] parents = MockSolution.of().buildArray(4);
		variation.evolve(parents);
		
		Assert.assertEquals(1, v1.getTotalCallCount("evolve"));
		Assert.assertEquals(2, v2.getTotalCallCount("evolve"));
		Assert.assertEquals(4, v3.getTotalCallCount("evolve"));
	}
	
	@Test(expected = FrameworkException.class)
	public void testException() {
		MockVariation vs1 = new MockVariation(3, 2);
		MockVariation vs2 = new MockVariation(3, 2);
		MockVariation vs3 = new MockVariation(1, 1);
		CompoundVariation variation = new CompoundVariation(vs1, vs2, vs3);
		
		Assert.assertEquals(3, variation.getArity());
		
		Solution[] parents = MockSolution.of().buildArray(3);
		variation.evolve(parents);
	}

}
