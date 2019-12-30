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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link R1Indicator} class.  The raw values used here were produced
 * using the PISA r-ind.exe program.
 */
public class R1IndicatorTest {

	@Test
	public void testZero() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		R1Indicator indicator = new R1Indicator(new MockRealProblem(), 500, referenceSet);
		Assert.assertEquals(0.5, indicator.evaluate(referenceSet), 0.000001);
	}
	
	@Test
	public void testSet() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.75, 0.25));
		population.add(TestUtils.newSolution(0.25, 0.75));
		
		R1Indicator indicator = new R1Indicator(new MockRealProblem(), 500, referenceSet);
		
		Assert.assertEquals(5.269461078e-001, indicator.evaluate(population), 0.000001);
	}
	
}
