package org.moeaframework.parallel.island.topology;

import java.util.ArrayList;
import java.util.List;
import org.moeaframework.parallel.island.Island;

/**
 * A ring topology where each island is only connected to two adjacent
 * islands, thus forming the shape of a ring.
 */
public class RingTopology implements Topology {

	/**
	 * Constructs a ring topology instance.
	 */
	public RingTopology() {
		super();
	}
	
	@Override
	public List<Island> getNeighbors(Island current, List<Island> allIslands) {
		List<Island> result = new ArrayList<Island>();
		
		for (int i = 0; i < allIslands.size(); i++) {
			if (allIslands.get(i) == current) {
				result.add(allIslands.get((i+1) % allIslands.size()));
			}
		}
		
		return result;
	}

}
