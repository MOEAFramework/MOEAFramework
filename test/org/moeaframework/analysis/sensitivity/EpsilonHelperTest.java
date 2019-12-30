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
package org.moeaframework.analysis.sensitivity;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link EpsilonHelper} class.
 */
public class EpsilonHelperTest {
	
	@Test
	public void testConvertNoEpsilons() {
		NondominatedPopulation population = 
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		EpsilonBoxDominanceArchive expected = 
				new EpsilonBoxDominanceArchive(0.1, population);
		EpsilonBoxDominanceArchive actual = 
				EpsilonHelper.convert(population, new double[] { 0.1 });
		
		TestUtils.assertEquals(expected, actual);
		TestUtils.assertEquals(0.1, actual.getComparator().getEpsilon(0));
	}
	
	@Test
	public void testConvertDifferentEpsilons() {
		NondominatedPopulation population = new EpsilonBoxDominanceArchive(0.1,
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2"));
		EpsilonBoxDominanceArchive actual = 
				EpsilonHelper.convert(population, new double[] { 0.25 });
		
		Assert.assertNotSame(actual, population);
		TestUtils.assertEquals(0.25, actual.getComparator().getEpsilon(0));
	}
	
	@Test
	public void testConvertSameEpsilons() {
		NondominatedPopulation population = new EpsilonBoxDominanceArchive(0.1,
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2"));
		EpsilonBoxDominanceArchive actual = 
				EpsilonHelper.convert(population, new double[] { 0.1 });
		
		Assert.assertSame(actual, population);
		TestUtils.assertEquals(0.1, actual.getComparator().getEpsilon(0));
	}
	
}
