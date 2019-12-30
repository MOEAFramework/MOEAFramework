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
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link RIndicator} class.  The raw values used here were produced
 * using the PISA r-ind.exe program.
 */
public class RIndicatorTest {
	
	@Test
	public void testChebychev() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.ChebychevUtility(), 0.782451);
	}
	
	@Test
	public void testLinearWeights() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.LinearWeightedSumUtility(), 0.750497);
	}
	
	public void testUtility(String file, RIndicator.UtilityFunction utilityFunction, double expectedUtility) throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File(file)));
		
		RIndicator indicator = new RIndicator(new MockRealProblem(), 500, referenceSet, utilityFunction) {

			@Override
			public double evaluate(NondominatedPopulation approximationSet) {
				throw new UnsupportedOperationException();
			}
			
		};
		
		Assert.assertEquals(expectedUtility, indicator.expectedUtility(referenceSet), 0.000001);
	}

}
