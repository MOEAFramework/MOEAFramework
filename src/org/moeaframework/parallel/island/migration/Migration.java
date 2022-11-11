package org.moeaframework.parallel.island.migration;

import org.moeaframework.parallel.island.Island;

/**
 * A migration strategy for an island model.  A strategy typically
 * specifies:
 * <ol>
 *   <li>The number of solutions that emigrate
 *   <li>Whether solutions are moved or copied
 *   <li>The direction of the migration (one-way, bi-directional)
 *   <li>The replacement strategy - do emigrants replace current members
 *       of the population?
 * </ol>
 */
public interface Migration {
	
	/**
	 * Performs a single migration operation between two islands.  While
	 * not mandatory, it is convention to migrate solutions from the
	 * {@code target} to the {@code current} island.
	 * 
	 * @param current the current island
	 * @param target the target island
	 */
	public void migrate(Island current, Island target);

}
