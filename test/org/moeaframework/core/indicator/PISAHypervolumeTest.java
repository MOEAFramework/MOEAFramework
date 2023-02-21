/* Copyright 2009-2023 David Hadka
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
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link PISAHypervolume} class. Due to performance, these tests only go up to 4 dimensions.
 */
public class PISAHypervolumeTest extends IndicatorTest {
	
	/**
	 * Tests if an exception is thrown when using an empty reference set.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyReferenceSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		NondominatedPopulation approximationSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");

		PISAHypervolume hypervolume = new PISAHypervolume(problem, referenceSet);
		hypervolume.evaluate(approximationSet);
	}
	
	/**
	 * Tests if an empty approximation set returns a hypervolume of zero.
	 */
	@Test
	public void testEmptyApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		PISAHypervolume hypervolume = new PISAHypervolume(problem, referenceSet);
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
	}
	
	/**
	 * Tests if infeasible solutions are properly ignored.
	 */
	@Test
	public void testInfeasibleApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("CF1");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("CF1");
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Solution solution = problem.newSolution();
		solution.setObjectives(new double[] { 0.5, 0.5 });
		solution.setConstraints(new double[] { 10.0 });
		approximationSet.add(solution);

		PISAHypervolume hypervolume = new PISAHypervolume(problem, referenceSet);
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
	}
	
	public void test(PISAHypervolume hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(1.0, 1.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(-0.5, -0.5));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.5, 0.0));
		approximationSet.add(TestUtils.newSolution(0.0, 0.5));
		Assert.assertEquals(0.75, hypervolume.evaluate(approximationSet), Settings.EPS);
	}

	/**
	 * Runs through some simple cases to ensure the hypervolume is computed
	 * correctly.
	 */
	@Test
	public void testSimple() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		
		PISAHypervolume hypervolume = new PISAHypervolume(problem, referenceSet);
		
		test(hypervolume);
	}
	
	@Test
	public void testExplicitBounds() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		PISAHypervolume hypervolume = new PISAHypervolume(problem,
				new double[] { 0.0, 0.0 },
				new double[] { 1.0, 1.0 });
		
		test(hypervolume);
	}
	
	public void test2(PISAHypervolume hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		// target value is 1.5^2 / 2^2
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(0.5625, hypervolume.evaluate(approximationSet),Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(1.0, 1.0));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), Settings.EPS);
	}
	
	@Test
	public void testExplicitBounds2_RefOnly() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		
		PISAHypervolume hypervolume = new PISAHypervolume(problem, referenceSet,
				new double[] { 2.0, 2.0 });
		
		test2(hypervolume);
	}
	
	@Test
	public void testExplicitBounds2_Both() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		PISAHypervolume hypervolume = new PISAHypervolume(problem,
				new double[] { 0.0, 0.0 },
				new double[] { 2.0, 2.0 });
		
		test2(hypervolume);
	}
	
	@Test
	public void testExplicitBounds2_Properties() {
		Settings.PROPERTIES.setDouble("org.moeaframework.core.indicator.hypervolume_idealpt.DTLZ2", 0.0);
		Settings.PROPERTIES.setDouble("org.moeaframework.core.indicator.hypervolume_refpt.DTLZ2", 2.0);
		
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		PISAHypervolume hypervolume = new PISAHypervolume(problem, new NondominatedPopulation());
		
		test2(hypervolume);
		
		Settings.PROPERTIES.remove("org.moeaframework.core.indicator.hypervolume_idealpt.DTLZ2");
		Settings.PROPERTIES.remove("org.moeaframework.core.indicator.hypervolume_refpt.DTLZ2");
	}

}
