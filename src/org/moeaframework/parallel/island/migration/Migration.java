package org.moeaframework.parallel.island.migration;

import org.moeaframework.parallel.island.Island;

/**
 * A migration strategy for an island model.  Since each island can execute in
 * separate threads or processes, some best practices are:
 * <ol>
 *   <li>The migration executes in the same thread as the current island.
 *       Therefore, you can modify the current population without synchronization.
 *   <li>Use the immigration queue on each island to send or receive solutions.
 *       Do not read or modify other islands directly.  Use their immigration queue!
 * </ol>
 */
public interface Migration {
	
	/**
	 * Performs a single migration operation between two islands.
	 * 
	 * @param current the current island
	 * @param target the target island
	 */
	public void migrate(Island current, Island target);

}
