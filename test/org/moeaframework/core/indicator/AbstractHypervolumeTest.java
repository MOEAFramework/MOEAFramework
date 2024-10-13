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
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Settings;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.PropertyScope;

public abstract class AbstractHypervolumeTest<T extends Indicator> extends AbstractIndicatorTest<T> {
	
	public abstract T createInstance(Problem problem, Normalizer normalizer);
		
	/**
	 * Returns {@code true} if the measured hypervolume includes points that exceed / are better than the ideal point.
	 */
	public boolean includesBetterSolutions() {
		return false;
	}
	
	@Override
	public double getWorstValue() {
		return 0.0;
	}

	@Test
	public void testDefaultBounds_ReferenceSet() {
		testDefaultBounds(createInstance(new MockRealProblem(2), getDefaultReferenceSet()));
	}
	
	@Test
	public void testDefaultBounds_IdealAndReferencePoint() {
		Problem problem = new MockRealProblem(2);
		
		testDefaultBounds(createInstance(problem,
				new Normalizer(new double[] { 0.0, 0.0 }, new double[] { 1.0, 1.0 })));
	}
	
	@Test
	public void testExpandedBounds_ReferencePointOnly() {
		Problem problem = new MockRealProblem(2);
		
		testExpandedBounds(createInstance(problem,
				new Normalizer(getDefaultReferenceSet(), new double[] { 2.0, 2.0 })));
	}
	
	@Test
	public void testExpandedBounds_IdealAndReferencePoint() {
		Problem problem = new MockRealProblem(2);
		
		testExpandedBounds(createInstance(problem,
				new Normalizer(new double[] { 0.0, 0.0 }, new double[] { 2.0, 2.0 })));
	}
	
	@Test
	public void testExpandedBounds_FromIdealRefPointProperties() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "core", "indicator", "hypervolume", "idealpt", "MockRealProblem"), 0.0)
				.with(Settings.createKey("org", "moeaframework", "core", "indicator", "hypervolume", "refpt", "MockRealProblem"), 2.0)) {
			testExpandedBounds(createInstance(new MockRealProblem(2), new NondominatedPopulation()));
		}
	}
	
	@Test
	public void testExpandedBounds_FromMinMaxProperties() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", "MockRealProblem", "normalization", "minimum"), 0.0)
				.with(Settings.createKey("org", "moeaframework", "problem", "MockRealProblem", "normalization", "maximum"), 2.0)) {
			testExpandedBounds(createInstance(new MockRealProblem(2), new NondominatedPopulation()));
		}
	}
	
	@Test
	public void testWellKnownSets() {
		testWellKnownSet("DTLZ2_2", 0.2106707611);
		testWellKnownSet("DTLZ2_3", 0.4204013912);
		testWellKnownSet("DTLZ2_4", 0.5809397339);
		testWellKnownSet("DTLZ2_6", 0.6938294108);
	}
	
	/**
	 * Tests hypervolume when bounded between (0, 0, ..., 0) and (1, 1, ..., 1).
	 */
	private void testDefaultBounds(Indicator hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.add(MockSolution.of().withObjectives(0.5, 0.5));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(1.0, 1.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(-0.5, -0.5));
		Assert.assertEquals(includesBetterSolutions() ? 2.25 : 1.0, hypervolume.evaluate(approximationSet),
				TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(0.5, 0.0));
		approximationSet.add(MockSolution.of().withObjectives(0.0, 0.5));
		Assert.assertEquals(0.75, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
	}
	
	/**
	 * Tests hypervolume when bounded between (0, 0, ..., 0) and (2, 2, ..., 2).
	 */
	private void testExpandedBounds(Indicator hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		// target value is 1.5^2 / 2^2
		approximationSet.add(MockSolution.of().withObjectives(0.5, 0.5));
		Assert.assertEquals(0.5625, hypervolume.evaluate(approximationSet),TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(1.0, 1.0));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of().withObjectives(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), TestThresholds.HIGH_PRECISION);
	}
	
	private void testWellKnownSet(String problemName, double expectedHypervolume) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
		
		Indicator hypervolume = createInstance(problem, referenceSet);
		
		Assert.assertEquals(expectedHypervolume, hypervolume.evaluate(referenceSet), 0.0001);
	}

}
