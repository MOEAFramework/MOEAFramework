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

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;

/**
 * The raw values used here were produced using the PISA r-ind.exe program.
 */
public class R3IndicatorTest extends AbstractIndicatorTest<R3Indicator> {
	
	@Override
	public R3Indicator createInstance(Problem problem, NondominatedPopulation referenceSet) {
		return new R3Indicator(problem, 500, referenceSet);
	}
	
	@Override
	public double getWorstValue() {
		return Double.POSITIVE_INFINITY;
	}
	
	@Test
	public void testSame() throws IOException {
		NondominatedPopulation referenceSet = getDefaultReferenceSet();
		Indicator indicator = createInstance(new MockRealProblem(2), referenceSet);
		Assert.assertEquals(0.0, indicator.evaluate(referenceSet), 0.000001);
	}
	
	@Test
	public void testDominance() {
		NondominatedPopulation referenceSet = getDefaultReferenceSet();
		Indicator indicator = createInstance(new MockRealProblem(2), referenceSet);
		
		NondominatedPopulation population1 = new NondominatedPopulation();
		population1.add(MockSolution.of().withObjectives(0.75, 0.25));
		population1.add(MockSolution.of().withObjectives(0.25, 0.75));
		
		NondominatedPopulation population2 = new NondominatedPopulation();
		population2.add(MockSolution.of().withObjectives(0.5, 0.5));
		
		Assert.assertLessThan(indicator.evaluate(population1), indicator.evaluate(population2));
	}

	@Test
	public void testCase() throws IOException {
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("./pf/DTLZ2.2D.pf");
		
		Indicator indicator = createInstance(new MockRealProblem(2), referenceSet);
		
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.75, 0.25));
		population.add(MockSolution.of().withObjectives(0.25, 0.75));
		
		Assert.assertEquals(2.878560650e-002, indicator.evaluate(population), 0.000001);
	}
	
}
