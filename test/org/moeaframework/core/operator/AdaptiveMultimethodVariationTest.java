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

import java.util.List;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;
import org.moeaframework.Counter;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.OperatorIndex;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.mock.MockVariation;

public class AdaptiveMultimethodVariationTest {
	
	private Population population;
	private AdaptiveMultimethodVariation variation;
	private List<CallCounter<Variation>> counters;
	
	@Before
	public void setUp() {
		Solution s1 = MockSolution.of();
		Solution s2 = MockSolution.of();
		Solution s3 = MockSolution.of();
		
		OperatorIndex.setAttribute(s1, 0);
		OperatorIndex.setAttribute(s2, 1);
		OperatorIndex.setAttribute(s3, 0);
		
		population = new Population(List.of(s1, s2, s3));
		
		counters = List.of(
				CallCounter.of(new MockVariation(1)),
				CallCounter.of(new MockVariation(3)));
		
		variation = new AdaptiveMultimethodVariation(population);
		
		for (CallCounter<Variation> counter : counters) {
			variation.addOperator(counter.getProxy());
		}
	}
	
	@After
	public void tearDown() {
		population = null;
		variation = null;
	}
	
	@Test(expected = IllegalStateException.class)
	public void testGetArityNoOperators() {
		variation = new AdaptiveMultimethodVariation(population);
		variation.getArity();
	}

	@Test(expected = IllegalStateException.class)
	public void testEvolveNoOperators() {
		variation = new AdaptiveMultimethodVariation(population);
		variation.evolve(null);
	}
	
	@Test
	public void testArity() {
		Assert.assertEquals(3, variation.getArity());
	}
	
	@Test
	public void testProbabilities() {
		Assert.assertEquals(3.0/5.0, variation.getOperatorProbability(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.0/5.0, variation.getOperatorProbability(1), TestThresholds.HIGH_PRECISION);
		
		assertActualSelectionProbabilities(variation, 3.0/5.0, 2.0/5.0);
	}
	
	@Test
	public void testProbabilitiesInitialPopulation() {
		for (Solution solution : population) {
			OperatorIndex.removeAttribute(solution);
		}
		
		Assert.assertEquals(0.5, variation.getOperatorProbability(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.5, variation.getOperatorProbability(1), TestThresholds.HIGH_PRECISION);
		
		assertActualSelectionProbabilities(variation, 0.5, 0.5);
	}
	
	/**
	 * Tests if the actual selection probabilities are equal to the expected selection probabilities.
	 * 
	 * @param variation the variation operator
	 * @param probabilities the expected selection probabilities
	 */
	private void assertActualSelectionProbabilities(AdaptiveMultimethodVariation variation, double... probabilities) {
		UniformSelection selection = new UniformSelection();
		
		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			variation.evolve(selection.select(variation.getArity(), population));
		}
		
		Assert.assertEquals(variation.getNumberOfOperators(), probabilities.length);
		
		for (int i=0; i<variation.getNumberOfOperators(); i++) {
			int count = counters.get(i).getTotalCallCount("evolve");
			
			Assert.assertEquals(probabilities[i], count / (double)TestThresholds.SAMPLES,
					TestThresholds.LOW_PRECISION);
		}
	}
	
	@Test
	public void testProbabilityUpdateInvocationCount() {
		Counter<AdaptiveMultimethodVariation> counter = new Counter<>();
		
		AdaptiveMultimethodVariation variation = new AdaptiveMultimethodVariation(population) {
			
			@Override
			protected double[] getOperatorProbabilities() {
				counter.incrementAndGet(this);
				return super.getOperatorProbabilities();
			}
			
		};
		
		variation.addOperator(new MockVariation(2));
		variation.addOperator(new MockVariation(2));
		
		UniformSelection selection = new UniformSelection();
		
		//ensure sufficient number of samples to trigger off-by-one error
		int numberOfSamples = ArithmeticUtils.pow(variation.getUpdateWindow(), 3);
		
		for (int i=0; i<numberOfSamples; i++) {
			variation.evolve(selection.select(variation.getArity(), population));
		}
		
		Assert.assertEquals(numberOfSamples / variation.getUpdateWindow(), counter.get(variation));
	}
	
}
