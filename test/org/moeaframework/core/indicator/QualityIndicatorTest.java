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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link QualityIndicator} class.
 */
public class QualityIndicatorTest {
	
	private QualityIndicator qualityIndicator;
	
	@Before
	public void setUp() {
		String problemName = "DTLZ2_2";
		ProblemFactory problemFactory = ProblemFactory.getInstance();
		Problem problem = problemFactory.getProblem(problemName);
		NondominatedPopulation referenceSet = problemFactory.getReferenceSet(
				problemName);
		
		qualityIndicator = new QualityIndicator(problem, referenceSet);
	}
	
	@After
	public void tearDown() {
		qualityIndicator = null;
	}
	
	@Test(expected = IllegalStateException.class)
	public void testHypervolumeIllegalState() {
		qualityIndicator.getHypervolume();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testAdditiveEpsilonIndicatorIllegalState() {
		qualityIndicator.getAdditiveEpsilonIndicator();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testGenerationalDistanceIllegalState() {
		qualityIndicator.getGenerationalDistance();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testInvertedGenerationalDistanceIllegalState() {
		qualityIndicator.getInvertedGenerationalDistance();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testMaximumParetoFrontErrorIllegalState() {
		qualityIndicator.getMaximumParetoFrontError();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSpacingIllegalState() {
		qualityIndicator.getSpacing();
	}
	
	@Test
	public void testValidState() {
		qualityIndicator.calculate(qualityIndicator.getReferenceSet());
		qualityIndicator.getHypervolume();
		qualityIndicator.getAdditiveEpsilonIndicator();
		qualityIndicator.getGenerationalDistance();
		qualityIndicator.getInvertedGenerationalDistance();
		qualityIndicator.getMaximumParetoFrontError();
		qualityIndicator.getSpacing();
	}

}
