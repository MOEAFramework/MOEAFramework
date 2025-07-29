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
package org.moeaframework.core.indicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.PropertyScope;
import org.moeaframework.core.Settings;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class DefaultNormalizerTest {
	
	private Problem problem;
	
	private NondominatedPopulation referenceSet;
	
	private Population population;
	
	@Before
	public void setUp() {
		problem = new MockRealProblem(2);
		
		referenceSet = new NondominatedPopulation();
		referenceSet.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		referenceSet.add(MockSolution.of(problem).withObjectives(1.0, 0.0));
		
		population = new Population();
		population.add(MockSolution.of(problem).withObjectives(0.5, 0.5));
	}
	
	@After
	public void tearDown() {
		problem = null;
		referenceSet = null;
		DefaultNormalizer.getInstance().clearOverrides();
	}
	
	@Test
	public void testDefault() {
		Normalizer normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
		population = normalizer.normalize(population);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetNullInstance() {
		DefaultNormalizer.setInstance(null);
	}
	
	@Test
	public void testCodeOverride() {
		DefaultNormalizer.getInstance().override(problem, new double[] { 0.0, 0.0 }, new double[] { 2.0, 2.0 });
		
		Normalizer normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
		population = normalizer.normalize(population);
		
		Assert.assertArrayEquals(new double[] { 0.25, 0.25 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testPropertiesOverride() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", problem.getName(), "normalization", "minimum"), "0")
				.with(Settings.createKey("org", "moeaframework", "problem", problem.getName(), "normalization", "maximum"), "2")) {
			Normalizer normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
			population = normalizer.normalize(population);
			
			Assert.assertArrayEquals(new double[] { 0.25, 0.25 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
		}
	}
	
	@Test
	public void testHypervolumeBackwardsCompatibility() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "core", "indicator", "hypervolume", "idealpt", problem.getName()), "0")
				.with(Settings.createKey("org", "moeaframework", "core", "indicator", "hypervolume", "refpt", problem.getName()), "2")) {
			Normalizer normalizer = DefaultNormalizer.getInstance().getHypervolumeNormalizer(problem, referenceSet);
			population = normalizer.normalize(population);
			
			Assert.assertArrayEquals(new double[] { 0.25, 0.25 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
		}
	}
	
	@Test
	public void testHypervolumeDelta() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", problem.getName(), "normalization", "delta"), "1.0")) {
			Normalizer normalizer = DefaultNormalizer.getInstance().getHypervolumeNormalizer(problem, referenceSet);
			population = normalizer.normalize(population);
			
			Assert.assertArrayEquals(new double[] { 0.25, 0.25 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
		}
	}
	
	@Test
	public void testDisableNormalization() {
		DefaultNormalizer.getInstance().disableNormalization(problem);
		
		referenceSet = new NondominatedPopulation();
		referenceSet.add(MockSolution.of(problem).withObjectives(100.0, 100.0));
		
		Normalizer normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
		population = normalizer.normalize(population);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testDisableNormalizationWithProperty() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", problem.getName(), "normalization", "disabled"), true)) {
			referenceSet = new NondominatedPopulation();
			referenceSet.add(MockSolution.of(problem).withObjectives(100.0, 100.0));
			
			Normalizer normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
			population = normalizer.normalize(population);
			
			Assert.assertArrayEquals(new double[] { 0.5, 0.5 }, population.get(0).getObjectiveValues(), TestEnvironment.HIGH_PRECISION);
		}
	}
	
	@Test
	public void testNamesWithSpecialCharacters() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", "foo.bar", "normalization", "disabled"), true)) {
			Assert.assertTrue(Settings.isNormalizationDisabled("foo.bar"));
		}
	}

}
