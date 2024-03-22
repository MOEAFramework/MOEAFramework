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
package org.moeaframework.parallel.island.topology;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.problem.mock.MockRealProblem;

public abstract class AbstractTopologyTest<T extends Topology> {
	
	public abstract T createInstance();
	
	@Test
	public void testSingleIsland() {
		Topology topology = createInstance();
		List<Island> islands = createIslands(1);
		
		List<Island> neighbors = topology.getNeighbors(islands.get(0), islands);		
		Assert.assertEquals(0, neighbors.size());
	}
	
	@Test
	public void testTwoIslands() {
		Topology topology = createInstance();
		List<Island> islands = createIslands(2);
		
		List<Island> neighbors = topology.getNeighbors(islands.get(0), islands);
		Assert.assertEquals(1, neighbors.size());
		Assert.assertSame(islands.get(1), neighbors.get(0));
		
		neighbors = topology.getNeighbors(islands.get(1), islands);
		Assert.assertEquals(1, neighbors.size());
		Assert.assertSame(islands.get(0), neighbors.get(0));
	}
	
	protected List<Island> createIslands(int count) {
		List<Island> result = new ArrayList<Island>();
		
		for (int i = 0; i < count; i++) {
			result.add(createIsland());
		}
		
		return result;
	}
	
	protected Island createIsland() {
		NSGAII algorithm = new NSGAII(new MockRealProblem());
		return new Island(algorithm, algorithm.getPopulation());
	}

}
