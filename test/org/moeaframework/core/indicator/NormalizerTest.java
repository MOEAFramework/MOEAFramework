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
package org.moeaframework.core.indicator;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.sensitivity.ProblemStub;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link Normalizer} class.
 */
public class NormalizerTest {
	
	/**
	 * Tests if the returned set is independent from the original, ensuring
	 * normalization does not affect the original population.
	 */
	@Test
	public void testNewObject() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(1.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		NondominatedPopulation result = normalizer.normalize(population);
		
		Assert.assertTrue(population != result);
		Assert.assertFalse(population.contains(result.get(0)));
		Assert.assertFalse(population.contains(result.get(1)));
	}
	
	/**
	 * Tests if a normalized population remains unchanged.
	 */
	@Test
	public void testNoRescale() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(1.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		
		TestUtils.assertEquals(population, normalizer.normalize(population));
	}
	
	/**
	 * Tests normalization when the bounds are derived from the population.
	 */
	@Test
	public void testRescale() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 0.1));
		population.add(TestUtils.newSolution(10.0, -0.1));
		population.add(TestUtils.newSolution(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(TestUtils.newSolution(0.0, 1.0));
		expected.add(TestUtils.newSolution(1.0, 0.0));
		expected.add(TestUtils.newSolution(0.5, 0.5));
		
		TestUtils.assertEquals(expected, normalizer.normalize(population));
	}
	
	/**
	 * Tests normalization when a reference point is provided.
	 */
	@Test
	public void testRescaleReferencePoint() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 0.1));
		population.add(TestUtils.newSolution(10.0, -0.1));
		population.add(TestUtils.newSolution(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), 
				population, new double[] { 20.0, 0.2 });
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(TestUtils.newSolution(0.0, 0.6666666));
		expected.add(TestUtils.newSolution(0.5, 0.0));
		expected.add(TestUtils.newSolution(0.25, 0.3333333));
		
		TestUtils.assertEquals(expected, normalizer.normalize(population));
	}
	
	/**
	 * Tests normalization when the bounds are defined explicitly.
	 */
	@Test
	public void testRescaleExplicit() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 0.1));
		population.add(TestUtils.newSolution(10.0, -0.1));
		population.add(TestUtils.newSolution(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), 
				new double[] { 0.0, -0.2 }, new double[] { 20.0, 0.2 });
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(TestUtils.newSolution(0.0, 0.75));
		expected.add(TestUtils.newSolution(0.5, 0.25));
		expected.add(TestUtils.newSolution(0.25, 0.5));
		
		TestUtils.assertEquals(expected, normalizer.normalize(population));
	}
	
	/**
	 * Tests if an exception is thrown when an empty set is provided.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorException1() {
		new Normalizer(new ProblemStub(2), new NondominatedPopulation());
	}
	
	/**
	 * Tests if an exception is thrown on a degenerate set.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorException2() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		new Normalizer(new ProblemStub(2), population);
	}
	
	/**
	 * Tests if an exception is thrown on a degenerate set.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorException3() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(1.0, 1.0));
		population.add(TestUtils.newSolution(0.0, 1.0 + Settings.EPS/2.0));
		
		new Normalizer(new ProblemStub(2), population);
	}

}
