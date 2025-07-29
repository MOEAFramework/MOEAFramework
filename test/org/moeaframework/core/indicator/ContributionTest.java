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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class ContributionTest extends AbstractIndicatorTest<Contribution> {
	
	@Override
	public Contribution createInstance(Problem problem, NondominatedPopulation referenceSet) {
		return new Contribution(referenceSet, 0.25);
	}
	
	@Override
	public double getWorstValue() {
		return 0.0;
	}
	
	@Test
	public void test() {
		Problem problem = new MockRealProblem(2);
		NondominatedPopulation referenceSet = getDefaultReferenceSet();
		Indicator indicator = createInstance(problem, referenceSet);
		
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		approximationSet.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		Assert.assertEquals(0.5, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(1.0, 1.0));
		Assert.assertEquals(0.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(0.1, 0.9));
		approximationSet.add(MockSolution.of(problem).withObjectives(0.0, 1.1));
		Assert.assertEquals(0.5, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);

		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(1.1, 0.0));
		approximationSet.add(MockSolution.of(problem).withObjectives(0.0, 1.1));
		Assert.assertEquals(1.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
	}

}
