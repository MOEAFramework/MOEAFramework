/* Copyright 2009-2019 David Hadka
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

import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Tests the {@link AdaptiveMultimethodVariation} class.
 */
public class AdaptiveMultimethodVariationTest {
	
	/**
	 * The shared population used for testing.
	 */
	private Population population;
	
	/**
	 * The shared adaptive multhmethod variation operator used for testing.
	 */
	private AdaptiveMultimethodVariation variation;
	
	/**
	 * A dummy variation operator that counts the number of invocations of
	 * {@code evolve}.
	 */
	private static class DummyVariation implements Variation {

		/**
		 * The arity of this operator.
		 */
		private final int arity;
		
		/**
		 * The number of invocations of the {@code evolve} method.
		 */
		private int count = 0;
		
		/**
		 * Constructs a dummy variation operator with the specified arity.
		 * 
		 * @param arity the arity of this operator
		 */
		public DummyVariation(int arity) {
			super();
			this.arity = arity;
		}
		
		@Override
		public int getArity() {
			return arity;
		}

		@Override
		public Solution[] evolve(Solution[] parents) {
			count++;
			Assert.assertEquals(arity, parents.length);
			return new Solution[0];
		}
		
	}
	
	/**
	 * Creates the shared objects used by these tests.
	 */
	@Before
	public void setUp() {
		population = new Population();
		
		Solution s1 = new Solution(0, 0);
		Solution s2 = new Solution(0, 0);
		Solution s3 = new Solution(0, 0);
		
		s1.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 0);
		s2.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 1);
		s3.setAttribute(AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE, 0);
		
		population.add(s1);
		population.add(s2);
		population.add(s3);
		
		variation = new AdaptiveMultimethodVariation(population);
	}
	
	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		population = null;
		variation = null;
	}
	
	/**
	 * Tests if an exception is thrown when calling the {@code getArity()}
	 * method with no operators.
	 */
	@Test(expected = Exception.class)
	public void testGetArityNoOperators() {
		variation.getArity();
	}

	/**
	 * Tests if an exception is thrown when calling the {@code evolve()} method
	 * with no operators.
	 */
	@Test(expected = Exception.class)
	public void testEvolveNoOperators() {
		variation.evolve(null);
	}
	
	/**
	 * Tests if the probabilities used are correct.
	 */
	@Test
	public void testProbabilities() {
		variation.addOperator(new DummyVariation(1));
		variation.addOperator(new DummyVariation(3));
		
		Assert.assertEquals(3, variation.getArity());
		
		Assert.assertEquals(3.0/5.0, variation.getOperatorProbability(0), 
				Settings.EPS);
		Assert.assertEquals(2.0/5.0, variation.getOperatorProbability(1),
				Settings.EPS);
		
		assertActualSelectionProbabilities(variation, 3.0/5.0, 2.0/5.0);
	}
	
	/**
	 * Tests if the probabilities used are correct for an initial population.
	 */
	@Test
	public void testProbabilitiesInitialPopulation() {
		variation.addOperator(new DummyVariation(2));
		variation.addOperator(new DummyVariation(2));
		
		Assert.assertEquals(2, variation.getArity());
		
		for (Solution solution : population) {
			solution.removeAttribute(
					AdaptiveMultimethodVariation.OPERATOR_ATTRIBUTE);
		}
		
		Assert.assertEquals(0.5, variation.getOperatorProbability(0), 
				Settings.EPS);
		Assert.assertEquals(0.5, variation.getOperatorProbability(1),
				Settings.EPS);
		
		assertActualSelectionProbabilities(variation, 0.5, 0.5);
	}
	
	/**
	 * Tests if the actual selection probabilities are equal to the expected
	 * selection probabilities.
	 * 
	 * @param variation the variation operator
	 * @param probabilities the expected selection probabilities
	 */
	private void assertActualSelectionProbabilities(
			AdaptiveMultimethodVariation variation, double... probabilities) {
		UniformSelection selection = new UniformSelection();
		
		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			variation.evolve(selection.select(variation.getArity(), 
					population));
		}
		
		Assert.assertEquals(variation.getNumberOfOperators(), 
				probabilities.length);
		
		for (int i=0; i<variation.getNumberOfOperators(); i++) {
			int count = ((DummyVariation)variation.getOperator(i)).count;
			
			Assert.assertEquals(probabilities[i], 
					count / (double)TestThresholds.SAMPLES,
					TestThresholds.STATISTICS_EPS);
		}
	}
	
	/**
	 * Extends {@link AdaptiveMultimethodVariation} to count the number of
	 * invocations to {@link #getOperatorProbabilities()}.
	 */
	private class AdaptiveMultimethodVariationCounter extends
	AdaptiveMultimethodVariation {
		
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
	 * Tests if the number of invocations between probability updates matches
	 * the UPDATE_WINDOW.
	 */
	@Test
	public void testProbabilityUpdateInvocationCount() {
		AdaptiveMultimethodVariationCounter variation = 
				new AdaptiveMultimethodVariationCounter(population);
		variation.addOperator(new DummyVariation(2));
		variation.addOperator(new DummyVariation(2));
		
		UniformSelection selection = new UniformSelection();
		
		//ensure sufficient number of samples to trigger off-by-one error
		int numberOfSamples = ArithmeticUtils.pow(variation.getUpdateWindow(),
				3);
		
		for (int i=0; i<numberOfSamples; i++) {
			variation.evolve(selection.select(variation.getArity(), 
					population));
		}
		
		Assert.assertEquals(numberOfSamples / variation.getUpdateWindow(), 
				variation.getCount());
	}
	
}
