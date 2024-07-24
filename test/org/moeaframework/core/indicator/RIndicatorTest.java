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
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;

/**
 * The raw values used here were produced using the PISA r-ind.exe program.
 */
public class RIndicatorTest {
	
	@Test
	public void testChebyshev() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.ChebyshevUtility(), 0.782451);
	}
	
	@Test
	public void testLinearWeights() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.LinearWeightedSumUtility(), 0.750497);
	}
	
	public void testUtility(String resource, RIndicator.UtilityFunction utilityFunction, double expectedUtility)
			throws IOException {
		Problem problem = new MockRealProblem(2);
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet(resource);
		Normalizer normalizer = new Normalizer(problem, referenceSet);
		
		RIndicator indicator = new RIndicator(problem, 500, referenceSet, normalizer, utilityFunction) {

			@Override
			public double evaluate(NondominatedPopulation approximationSet) {
				throw new UnsupportedOperationException();
			}
			
		};
		
		Assert.assertEquals(expectedUtility, indicator.expectedUtility(referenceSet), 0.000001);
	}

}
