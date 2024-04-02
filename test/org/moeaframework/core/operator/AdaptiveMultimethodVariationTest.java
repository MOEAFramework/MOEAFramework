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

import java.util.List;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.mock.MockVariation;

public class AdaptiveMultimethodVariationTest {
	
	private Population population;
	private AdaptiveMultimethodVariation variation;
	
	@Before
	public void setUp() {	
		Solution s1 = MockSolution.of();
		Solution s2 = MockSolution.of();
		Solution s3 = MockSolution.of();
		
		s1.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 0);
		s2.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 1);
		s3.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 0);
		
		population = new Population(List.of(s1, s2, s3));
		variation = new AdaptiveMultimethodVariation(population);
	}
	
	@After
	public void tearDown() {
		population = null;
		variation = null;
	}
	
	@Test(expected = IllegalStateException.class)
	public void testGetArityNoOperators() {
		variation.getArity();
	}

	@Test(expected = IllegalStateException.class)
	public void testEvolveNoOperators() {
		variation.evolve(null);
	}
	
	@Test
	public void testProbabilities() {
		variation.addOperator(new MockVariation(1));
		variation.addOperator(new MockVariation(3));
		
		Assert.assertEquals(3, variation.getArity());
		
		Assert.assertEquals(3.0/5.0, variation.getOperatorProbability(0), Settings.EPS);
		Assert.assertEquals(2.0/5.0, variation.getOperatorProbability(1), Settings.EPS);
		
		assertActualSelectionProbabilities(variation, 3.0/5.0, 2.0/5.0);
	}
	
	@Test
	public void testProbabilitiesInitialPopulation() {
		variation.addOperator(new MockVariation(2));
		variation.addOperator(new MockVariation(2));
		
		Assert.assertEquals(2, variation.getArity());
		
		for (Solution solution : population) {
			solution.removeAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE);
		}
		
		Assert.assertEquals(0.5, variation.getOperatorProbability(0), Settings.EPS);
		Assert.assertEquals(0.5, variation.getOperatorProbability(1), Settings.EPS);
		
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
			int count = ((MockVariation)variation.getOperator(i)).getCallCount();
			
			Assert.assertEquals(probabilities[i], count / (double)TestThresholds.SAMPLES,
					TestThresholds.STATISTICS_EPS);
		}
	}
	
	/**
	 * Extends {@link AdaptiveMultimethodVariation} to count the number of invocations to
	 * {@link #getOperatorProbabilities()}.
	 */
	private class AdaptiveMultimethodVariationCounter extends AdaptiveMultimethodVariation {
		
		private int count = 0;

		public AdaptiveMultimethodVariationCounter(Population archive) {
			super(archive);
		}

		@Override
		protected double[] getOperatorProbabilities() {
			count++;
			return super.getOperatorProbabilities();
		}
		
		public int getCount() {
			return count;
		}
		
	}
	
	/**
	 * Tests if the number of invocations between probability updates matches the UPDATE_WINDOW.
	 */
	@Test
	public void testProbabilityUpdateInvocationCount() {
		AdaptiveMultimethodVariationCounter variation = new AdaptiveMultimethodVariationCounter(population);
		variation.addOperator(new MockVariation(2));
		variation.addOperator(new MockVariation(2));
		
		UniformSelection selection = new UniformSelection();
		
		//ensure sufficient number of samples to trigger off-by-one error
		int numberOfSamples = ArithmeticUtils.pow(variation.getUpdateWindow(), 3);
		
		for (int i=0; i<numberOfSamples; i++) {
			variation.evolve(selection.select(variation.getArity(), population));
		}
		
		Assert.assertEquals(numberOfSamples / variation.getUpdateWindow(), variation.getCount());
	}
	
}
