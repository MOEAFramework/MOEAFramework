package org.moeaframework.parallel.island.migration;

import java.util.List;

import org.moeaframework.parallel.island.Island;

/**
 * A migration strategy for an island model.  Since each island can execute in
 * separate threads or processes, be mindful of thread safety.
 * <p>
 * Migrations execute in the same thread as the current island.  Therefore, you can
 * freely read and write to the current population without synchronization.
 * <p>
 * Each island has a thread-safe immigration queue.  This stores the migrants that
 * are arriving to the current island.  Therefore, avoid reading or writing to any
 * neighboring islands directly, use their immigration queue!
 * <p>
 * The migration code should also create copies of migrating solutions.  By convention,
 * this should be done when adding the solution to an immigration queue.  This is
 * especially important when running in a shared memory environment (threaded).
 */
public interface Migration {
	
	/**
	 * Performs a single migration operation between the current island and its
	 * neighbors.
	 * 
	 * @param current the current island
	 * @param target the neighboring islands
	 */
	public void migrate(Island current, List<Island> neighbors);

}
