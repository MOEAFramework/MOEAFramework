/* Copyright 2009-2022 David Hadka
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
 * A fully-connected or "star" topology where each island is connected
 * to every other island.
 */
public class FullyConnectedTopology implements Topology {
	
	/**
	 * Constructs a fully-connected topology instance.
	 */
	public FullyConnectedTopology() {
		super();
	}

	@Override
	public List<Island> getNeighbors(Island current, List<Island> allIslands) {
		List<Island> result = new ArrayList<Island>();

		for (Island otherIsland : allIslands) {
			if (otherIsland != current) {
				result.add(otherIsland);
			}
		}
		
		return result;
	}

}
