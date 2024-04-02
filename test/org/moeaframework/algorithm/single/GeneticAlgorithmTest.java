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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class GeneticAlgorithmTest extends AbstractSingleObjectiveAlgorithmTest<GeneticAlgorithm> {
	
	public GeneticAlgorithm createInstance(Problem problem) {
		return new GeneticAlgorithm(problem);
	}
	
	@Test
	public void testDefaultComparator() {
		GeneticAlgorithm algorithm = new GeneticAlgorithm(new MockRealProblem());
		
		Assert.assertNotNull(algorithm.getComparator());
		Assert.assertEquals(algorithm.getComparator(), algorithm.getSelection().getComparator());
	}
	
	@Test
	public void testSetComparatorUpdatesSelection() {
		GeneticAlgorithm algorithm = new GeneticAlgorithm(new MockRealProblem());
		AggregateObjectiveComparator oldComparator = algorithm.getComparator();
		
		AggregateObjectiveComparator newComparator = new MinMaxDominanceComparator();
		algorithm.setComparator(newComparator);
		
		Assert.assertNotEquals(oldComparator, algorithm.getComparator());
		Assert.assertEquals(newComparator, algorithm.getComparator());
		Assert.assertEquals(newComparator, algorithm.getSelection().getComparator());
	}
	
	@Test
	public void testApplyConfigurationUpdatesSelection() {
		GeneticAlgorithm algorithm = createInstance(new MockRealProblem());
		TypedProperties properties = algorithm.getConfiguration();
		
		properties.setString("method", "min-max");
		algorithm.applyConfiguration(properties);
		
		Assert.assertEquals(algorithm.getComparator(), algorithm.getSelection().getComparator());
	}

}
