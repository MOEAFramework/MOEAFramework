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

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.PropertyScope;
import org.moeaframework.core.Settings;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

public class HypervolumeTest {
	
	private Problem problem;
	private NondominatedPopulation referenceSet;
	private double[] idealPt;
	private double[] refPt;
	
	@Before
	public void setUp() {
		problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		idealPt = new double[] { 0.0, 0.0 };
		refPt = new double[] { 2.0, 2.0 };
	}
	
	@Test
	public void testDefault() {
		assertInstance(null, WFGNormalizedHypervolume.class);
	}
	
	@Test
	public void testWFG() {
		assertInstance("WFG", WFGNormalizedHypervolume.class);
	}
	
	@Test
	public void testPISA() {
		assertInstance("PISA", PISAHypervolume.class);
	}
	
	@Test
	public void testNative() {
		assertInstance("./wfg.exe {0}", NativeHypervolume.class);
	}
	
	private <T extends Indicator> void assertInstance(String propertyValue, Class<T> expectedType) {
		try (PropertyScope scope = Settings.createScope()) {
			if (propertyValue == null) {
				scope.without(Settings.KEY_HYPERVOLUME);
			} else {
				scope.with(Settings.KEY_HYPERVOLUME, propertyValue);
			}
			
			Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
			Assert.assertInstanceOf(expectedType, hypervolume.instance);
			
			hypervolume = new Hypervolume(problem, referenceSet, refPt);
			Assert.assertInstanceOf(expectedType, hypervolume.instance);
			
			hypervolume = new Hypervolume(problem, idealPt, refPt);
			Assert.assertInstanceOf(expectedType, hypervolume.instance);
		}
	}

}
