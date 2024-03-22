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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.parallel.island.Island;

public class FullyConnectedTopologyTest extends AbstractTopologyTest<FullyConnectedTopology> {
	
	@Override
	public FullyConnectedTopology createInstance() {
		return new FullyConnectedTopology();
	}
	
	@Test
	public void testFourIslands() {
		Topology topology = createInstance();
		List<Island> islands = createIslands(4);
		
		List<Island> neighbors = topology.getNeighbors(islands.get(0), islands);
		Assert.assertEquals(3, neighbors.size());
		Assert.assertTrue(neighbors.contains(islands.get(1)));
		Assert.assertTrue(neighbors.contains(islands.get(2)));
		Assert.assertTrue(neighbors.contains(islands.get(3)));
		
		neighbors = topology.getNeighbors(islands.get(1), islands);
		Assert.assertEquals(3, neighbors.size());
		Assert.assertTrue(neighbors.contains(islands.get(0)));
		Assert.assertTrue(neighbors.contains(islands.get(2)));
		Assert.assertTrue(neighbors.contains(islands.get(3)));
		
		neighbors = topology.getNeighbors(islands.get(2), islands);
		Assert.assertEquals(3, neighbors.size());
		Assert.assertTrue(neighbors.contains(islands.get(0)));
		Assert.assertTrue(neighbors.contains(islands.get(1)));
		Assert.assertTrue(neighbors.contains(islands.get(3)));
		
		neighbors = topology.getNeighbors(islands.get(3), islands);
		Assert.assertEquals(3, neighbors.size());
		Assert.assertTrue(neighbors.contains(islands.get(0)));
		Assert.assertTrue(neighbors.contains(islands.get(1)));
		Assert.assertTrue(neighbors.contains(islands.get(2)));
	}

}
