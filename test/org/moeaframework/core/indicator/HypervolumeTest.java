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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.PropertyScope;

public class HypervolumeTest extends IndicatorTest {
	
	private Problem problem;
	private NondominatedPopulation referenceSet;
	
	@Before
	public void setUp() {
		problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
	}
	
	@Test
	public void testDefault() {
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		Assert.assertTrue(hypervolume.instance instanceof WFGNormalizedHypervolume);
	}
	
	@Test
	public void testWFG() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_HYPERVOLUME, "WFG")) {
			Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
			Assert.assertTrue(hypervolume.instance instanceof WFGNormalizedHypervolume);
		}
	}
	
	@Test
	public void testPISA() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_HYPERVOLUME, "PISA")) {
			Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
			Assert.assertTrue(hypervolume.instance instanceof PISAHypervolume);
		}
	}
	
	@Test
	public void testNative() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_HYPERVOLUME, "./wfg.exe {0}")) {
			Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
			Assert.assertTrue(hypervolume.instance instanceof NativeHypervolume);
		}
	}

}
