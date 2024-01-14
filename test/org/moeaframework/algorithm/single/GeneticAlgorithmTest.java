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
package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class GeneticAlgorithmTest {
	
	@Test
	public void testDefaultComparator() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		
		Assert.assertNotNull(ga.getComparator());
		Assert.assertEquals(ga.getComparator(), ga.getSelection().getComparator());
	}
	
	@Test
	public void testComparator() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		AggregateObjectiveComparator oldComparator = ga.getComparator();
		
		AggregateObjectiveComparator newComparator = new MinMaxDominanceComparator();
		ga.setComparator(newComparator);
		
		Assert.assertNotEquals(oldComparator, ga.getComparator());
		Assert.assertEquals(newComparator, ga.getComparator());
		Assert.assertEquals(newComparator, ga.getSelection().getComparator());
	}
	
	@Test
	public void testApplyConfiguration() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		AggregateObjectiveComparator oldComparator = ga.getComparator();
		
		ga.applyConfiguration(TypedProperties.withProperty("method", "min-max"));
		
		Assert.assertNotEquals(oldComparator, ga.getComparator());
		Assert.assertTrue(ga.getComparator() instanceof MinMaxDominanceComparator);
		Assert.assertEquals(ga.getComparator(), ga.getSelection().getComparator());
	}

}
