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
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link Spacing} class.
 */
public class SpacingTest {
	
	/**
	 * Tests if an empty approximation set returns a value of zero.
	 */
	@Test
	public void testEmptyApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		Spacing sp = new Spacing(problem);
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), 
				Settings.EPS);
	}
	
	/**
	 * Tests if infeasible solutions are properly ignored.
	 */
	@Test
	public void testInfeasibleApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("CF1");
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Solution solution = problem.newSolution();
		solution.setObjectives(new double[] { 0.5, 0.5 });
		solution.setConstraints(new double[] { 10.0 });
		approximationSet.add(solution);

		Spacing sp = new Spacing(problem);
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), 
				Settings.EPS);
	}
	
	/**
	 * Runs through some simple cases to ensure the spacing metric is
	 * computed correctly.
	 */
	@Test
	public void testSimple() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Spacing sp = new Spacing(problem);
		
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), Settings.EPS);

		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertEquals(0.0, sp.evaluate(approximationSet), Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(0.25, 0.75));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertTrue(sp.evaluate(approximationSet) > 0.0);
	}

}
