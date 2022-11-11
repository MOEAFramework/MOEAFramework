package org.moeaframework.parallel.island.topology;

import java.util.List;
import org.moeaframework.parallel.island.Island;

/**
 * Defines the topology for an island model.
 */
public interface Topology {

	/**
	 * Returns the neighboring islands for the current island.
	 * 
	 * @param current the current island
	 * @param allIslands the list of all islands
	 * @return the neighboring islands
	 */
	public List<Island> getNeighbors(Island current, List<Island> allIslands);
	
}
