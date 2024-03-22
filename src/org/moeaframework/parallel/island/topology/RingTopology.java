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
import org.moeaframework.parallel.island.Island;

/**
 * A ring topology where each island is only connected to one (unidirectional) or two adjacent islands (bidirectional),
 * thus forming the shape of a ring.
 */
public class RingTopology implements Topology {
	
	/**
	 * {@code true} if the ring connects to both neighbors; {@code false} if only one.
	 */
	private final boolean bidirectional;
	
	/**
	 * Constructs a bidirectional ring topology instance.
	 */
	public RingTopology() {
		this(true);
	}

	/**
	 * Constructs a ring topology instance.
	 * 
	 * @param bidirectional {@code true} if the ring connects to both neighbors; {@code false} if only one
	 */
	public RingTopology(boolean bidirectional) {
		super();
		this.bidirectional = bidirectional;
	}
	
	@Override
	public List<Island> getNeighbors(Island current, List<Island> allIslands) {
		List<Island> result = new ArrayList<Island>();
		int index = allIslands.indexOf(current);
		
		if (allIslands.size() > 1) {
			result.add(allIslands.get(Math.floorMod(index+1, allIslands.size())));
		}
		
		if (bidirectional && allIslands.size() > 2) {
			result.add(allIslands.get(Math.floorMod(index-1, allIslands.size())));
		}
		
		return result;
	}

}
