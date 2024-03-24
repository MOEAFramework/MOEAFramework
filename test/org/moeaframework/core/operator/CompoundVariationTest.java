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
package org.moeaframework.core.operator;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.mock.MockSolution;

public class CompoundVariationTest {
	
	private static class VariationStub implements Variation {
		
		private final int arity;
		
		private final int numberOfOffspring;
		
		private int count;
		
		public VariationStub(int arity, int numberOfOffspring) {
			super();
			this.arity = arity;
			this.numberOfOffspring = numberOfOffspring;
		}
		
		@Override
		public String getName() {
			return "stub";
		}

		@Override
		public int getArity() {
			return arity;
		}

		@Override
		public Solution[] evolve(Solution[] parents) {
			count++;
			Assert.assertEquals(arity, parents.length);
			return Arrays.copyOf(parents, numberOfOffspring);
		}
		
	}
	
	@Test
	public void testMutationOnly() {
		VariationStub vs1 = new VariationStub(1, 1);
		CompoundVariation variation = new CompoundVariation(vs1);
		
		Assert.assertEquals("stub", variation.getName());
		Assert.assertEquals(1, variation.getArity());
		
		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution[] parents = new Solution[] { MockSolution.of() };
			Assert.assertEquals(1, variation.evolve(parents).length);
		}
		
		Assert.assertEquals(TestThresholds.SAMPLES, vs1.count);
	}
	
	@Test
	public void testGA() {
		VariationStub vs1 = new VariationStub(2, 2);
		VariationStub vs2 = new VariationStub(1, 1);
		CompoundVariation variation = new CompoundVariation(vs1, vs2);
		
		Assert.assertEquals("stub+stub", variation.getName());
		Assert.assertEquals(2, variation.getArity());
		
		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution[] parents = new Solution[] { MockSolution.of(), MockSolution.of() };
			Assert.assertEquals(2, variation.evolve(parents).length);
		}
		
		Assert.assertEquals(TestThresholds.SAMPLES, vs1.count);
		Assert.assertEquals(2*TestThresholds.SAMPLES, vs2.count);
	}
	
	@Test
	public void testComplex() {
		VariationStub vs1 = new VariationStub(3, 2);
		VariationStub vs2 = new VariationStub(2, 2);
		VariationStub vs3 = new VariationStub(1, 1);
		CompoundVariation variation = new CompoundVariation(vs1, vs2, vs3);
		variation.setName("complex");
		
		Assert.assertEquals("complex", variation.getName());
		Assert.assertEquals(3, variation.getArity());
		
		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution[] parents = new Solution[] { MockSolution.of(), MockSolution.of(), MockSolution.of() };
			Assert.assertEquals(2, variation.evolve(parents).length);
		}
		
		Assert.assertEquals(TestThresholds.SAMPLES, vs1.count);
		Assert.assertEquals(TestThresholds.SAMPLES, vs2.count);
		Assert.assertEquals(2*TestThresholds.SAMPLES, vs3.count);
	}
	
	@Test(expected = FrameworkException.class)
	public void testException() {
		VariationStub vs1 = new VariationStub(3, 2);
		VariationStub vs2 = new VariationStub(3, 2);
		VariationStub vs3 = new VariationStub(1, 1);
		CompoundVariation variation = new CompoundVariation(vs1, vs2, vs3);
		
		Assert.assertEquals(3, variation.getArity());
		
		Solution[] parents = new Solution[] { MockSolution.of(), MockSolution.of(), MockSolution.of() };
		variation.evolve(parents);
	}

}
