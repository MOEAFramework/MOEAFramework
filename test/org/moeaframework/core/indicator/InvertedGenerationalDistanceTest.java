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
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class InvertedGenerationalDistanceTest extends AbstractIndicatorTest<InvertedGenerationalDistance> {
	
	@Override
	public InvertedGenerationalDistance createInstance(Problem problem, NondominatedPopulation referenceSet) {
		return new InvertedGenerationalDistance(problem, referenceSet);
	}
	
	@Override
	public double getWorstValue() {
		return Double.POSITIVE_INFINITY;
	}
	
	@Test
	public void test() {
		Problem problem = new MockRealProblem(2);
		NondominatedPopulation referenceSet = getDefaultReferenceSet();
		Indicator indicator = createInstance(problem, referenceSet);
		
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		approximationSet.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		Assert.assertEquals(Math.sqrt(2.0)/2.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(1.0, 1.0));
		Assert.assertEquals(2.0/2.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
		
		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(2.0, 2.0));
		Assert.assertEquals(2.0*Math.sqrt(5.0)/2.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);

		approximationSet.clear();
		approximationSet.add(MockSolution.of(problem).withObjectives(1.0, 0.0));
		approximationSet.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		Assert.assertEquals(0.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testExactMatch() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		NondominatedPopulation approximationSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		
		Indicator indicator = createInstance(problem, referenceSet);
		Assert.assertEquals(0.0, indicator.evaluate(approximationSet), TestEnvironment.HIGH_PRECISION);
	}
	
	@Test
	public void testIshibuchi() {
		Assert.assertEquals(5.242, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example1RefSet,
				IshibuchiTestInstances.Example1SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(6.191, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example1RefSet,
				IshibuchiTestInstances.Example1SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(5.242, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example2RefSet,
				IshibuchiTestInstances.Example2SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(7.171, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example2RefSet,
				IshibuchiTestInstances.Example2SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(5.317, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example1RefSet,
				IshibuchiTestInstances.Example3SetD), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(3.707, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example4RefSet,
				IshibuchiTestInstances.Example4SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(2.591, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example4RefSet,
				IshibuchiTestInstances.Example4SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(5.242, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example5RefSet,
				IshibuchiTestInstances.Example5SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(4.854, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example5RefSet,
				IshibuchiTestInstances.Example5SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(3.162, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example6RefSet,
				IshibuchiTestInstances.Example6SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(2.236, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example6RefSet,
				IshibuchiTestInstances.Example6SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(2.828, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example8RefSet,
				IshibuchiTestInstances.Example8SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(5.0, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example8RefSet,
				IshibuchiTestInstances.Example8SetB), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(2.828, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example9RefSet,
				IshibuchiTestInstances.Example9SetA), TestEnvironment.LOW_PRECISION);
		
		Assert.assertEquals(2.828, IshibuchiTestInstances.computeIGD(IshibuchiTestInstances.Example9RefSet,
				IshibuchiTestInstances.Example9SetB), TestEnvironment.LOW_PRECISION);
	}

}
