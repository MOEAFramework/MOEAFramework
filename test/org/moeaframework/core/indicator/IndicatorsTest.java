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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Settings.Scope;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.spi.ProblemFactory;

public class IndicatorsTest {
	
	private Indicators indicators;
	
	private NondominatedPopulation testApproximationSet;
	
	@Before
	public void setUp() {
		String problemName = "DTLZ2_2";
		ProblemFactory problemFactory = ProblemFactory.getInstance();
		Problem problem = problemFactory.getProblem(problemName);
		NondominatedPopulation referenceSet = problemFactory.getReferenceSet(problemName);
		
		indicators = Indicators.from(problem, referenceSet);
		testApproximationSet = referenceSet;
	}
	
	@After
	public void tearDown() {
		indicators = null;
	}

	@Test
	public void testNoneEnabled() {
		IndicatorValues values = indicators.apply(testApproximationSet);
		
		Assert.assertTrue(Double.isNaN(values.getHypervolume()));
		Assert.assertTrue(Double.isNaN(values.getGenerationalDistance()));
		Assert.assertTrue(Double.isNaN(values.getInvertedGenerationalDistance()));
		Assert.assertTrue(Double.isNaN(values.getAdditiveEpsilonIndicator()));
		Assert.assertTrue(Double.isNaN(values.getSpacing()));
		Assert.assertTrue(Double.isNaN(values.getMaximumParetoFrontError()));
		Assert.assertTrue(Double.isNaN(values.getContribution()));
		Assert.assertTrue(Double.isNaN(values.getR1()));
		Assert.assertTrue(Double.isNaN(values.getR2()));
		Assert.assertTrue(Double.isNaN(values.getR3()));
		
		Assert.assertEquals(0, values.asProperties().size());
	}
	
	@Test
	public void testStandardMetrics() {
		indicators.includeStandardMetrics();
		
		IndicatorValues values = indicators.apply(testApproximationSet);
		
		Assert.assertFalse(Double.isNaN(values.getHypervolume()));
		Assert.assertFalse(Double.isNaN(values.getGenerationalDistance()));
		Assert.assertFalse(Double.isNaN(values.getInvertedGenerationalDistance()));
		Assert.assertFalse(Double.isNaN(values.getAdditiveEpsilonIndicator()));
		Assert.assertFalse(Double.isNaN(values.getSpacing()));
		Assert.assertFalse(Double.isNaN(values.getMaximumParetoFrontError()));
		Assert.assertFalse(Double.isNaN(values.getContribution()));
		Assert.assertTrue(Double.isNaN(values.getR1()));
		Assert.assertTrue(Double.isNaN(values.getR2()));
		Assert.assertTrue(Double.isNaN(values.getR3()));
		
		Assert.assertEquals(7, values.asProperties().size());
	}
	
	@Test
	public void testAllMetrics() {
		indicators.includeAllMetrics();
		
		IndicatorValues values = indicators.apply(testApproximationSet);
		
		Assert.assertFalse(Double.isNaN(values.getHypervolume()));
		Assert.assertFalse(Double.isNaN(values.getGenerationalDistance()));
		Assert.assertFalse(Double.isNaN(values.getInvertedGenerationalDistance()));
		Assert.assertFalse(Double.isNaN(values.getAdditiveEpsilonIndicator()));
		Assert.assertFalse(Double.isNaN(values.getSpacing()));
		Assert.assertFalse(Double.isNaN(values.getMaximumParetoFrontError()));
		Assert.assertFalse(Double.isNaN(values.getContribution()));
		Assert.assertFalse(Double.isNaN(values.getR1()));
		Assert.assertFalse(Double.isNaN(values.getR2()));
		Assert.assertFalse(Double.isNaN(values.getR3()));
		
		Assert.assertEquals(10, values.asProperties().size());
	}
	
	@Test
	public void testUpdates() {
		IndicatorValues values = indicators.apply(testApproximationSet);
		Assert.assertTrue(Double.isNaN(values.getHypervolume()));
		Assert.assertEquals(0, values.asProperties().size());
		
		indicators.includeHypervolume();
		values = indicators.apply(testApproximationSet);
		Assert.assertFalse(Double.isNaN(values.getHypervolume()));
		Assert.assertEquals(1, values.asProperties().size());
	}
	
	@Test
	public void testDisabledHypervolume() {
		try (Scope scope = Settings.createScope().with(Settings.KEY_HYPERVOLUME_ENABLED, false)) {
			indicators.includeStandardMetrics();
			IndicatorValues values = indicators.apply(testApproximationSet);
			Assert.assertTrue(Double.isNaN(values.getHypervolume()));
			
			indicators.includeAllMetrics();
			values = indicators.apply(testApproximationSet);
			Assert.assertTrue(Double.isNaN(values.getHypervolume()));
			
			// including hypervolume directly ignores the setting
			indicators.includeHypervolume();
			values = indicators.apply(testApproximationSet);
			Assert.assertFalse(Double.isNaN(values.getHypervolume()));
		}
	}

}
