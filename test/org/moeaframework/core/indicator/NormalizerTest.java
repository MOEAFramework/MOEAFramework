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
package org.moeaframework.core.indicator;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.ProblemStub;

public class NormalizerTest {
	
	@Test
	public void testNormalizeDoesNotAffectOriginal() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		NondominatedPopulation result = normalizer.normalize(population);
		
		Assert.assertNotSame(population, result);
		Assert.assertNotContains(population, result.get(0));
		Assert.assertNotContains(population, result.get(1));
	}
	
	@Test
	public void testNoChangeIfAlreadyNormalized() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		
		Assert.assertEquals(population, normalizer.normalize(population));
	}
	
	@Test
	public void testBoundsFromPopulation() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 0.1));
		population.add(MockSolution.of().withObjectives(10.0, -0.1));
		population.add(MockSolution.of().withObjectives(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population);
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(MockSolution.of().withObjectives(0.0, 1.0));
		expected.add(MockSolution.of().withObjectives(1.0, 0.0));
		expected.add(MockSolution.of().withObjectives(0.5, 0.5));
		
		Assert.assertEquals(expected, normalizer.normalize(population));
	}
	
	@Test
	public void testBoundsFromReferencePoint() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 0.1));
		population.add(MockSolution.of().withObjectives(10.0, -0.1));
		population.add(MockSolution.of().withObjectives(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), population, new double[] { 20.0, 0.2 });
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(MockSolution.of().withObjectives(0.0, 0.6666666));
		expected.add(MockSolution.of().withObjectives(0.5, 0.0));
		expected.add(MockSolution.of().withObjectives(0.25, 0.3333333));
		
		Assert.assertEquals(expected, normalizer.normalize(population));
	}
	
	@Test
	public void testBoundsFromIdealAndReferencePoint() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 0.1));
		population.add(MockSolution.of().withObjectives(10.0, -0.1));
		population.add(MockSolution.of().withObjectives(5.0, 0.0));
		
		Normalizer normalizer = new Normalizer(new ProblemStub(2), new double[] { 0.0, -0.2 }, new double[] { 20.0, 0.2 });
		
		NondominatedPopulation expected = new NondominatedPopulation();
		expected.add(MockSolution.of().withObjectives(0.0, 0.75));
		expected.add(MockSolution.of().withObjectives(0.5, 0.25));
		expected.add(MockSolution.of().withObjectives(0.25, 0.5));
		
		Assert.assertEquals(expected, normalizer.normalize(population));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorEmptyReferenceSet() {
		new Normalizer(new ProblemStub(2), new NondominatedPopulation());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorSingleSolutionInReferenceSet() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		new Normalizer(new ProblemStub(2), population);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorDegenerateReferenceSet() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(1.0, 1.0));
		population.add(MockSolution.of().withObjectives(0.0, 1.0 + Settings.EPS/2.0));
		
		new Normalizer(new ProblemStub(2), population);
	}

}
