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
	 * @param neighbors the neighboring islands
	 */
	public void migrate(Island current, List<Island> neighbors);

}
