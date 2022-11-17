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
