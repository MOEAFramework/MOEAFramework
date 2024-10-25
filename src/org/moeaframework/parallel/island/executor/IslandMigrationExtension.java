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
package org.moeaframework.parallel.island.executor;

import java.util.List;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.FrequencyType;
import org.moeaframework.algorithm.extension.PeriodicExtension;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;

/**
 * Extension that triggers island migrations.
 */
public class IslandMigrationExtension extends PeriodicExtension {

	/**
	 * The island associated with this migration action.
	 */
	private final Island island;

	/**
	 * The island model.
	 */
	private final IslandModel model;

	/**
	 * Constructs a new periodic action for triggering island migrations.
	 * 
	 * @param island the island associated with this migration action
	 * @param model the island model
	 */
	public IslandMigrationExtension(Island island, IslandModel model) {
		super(model.getMigrationFrequency(), FrequencyType.EVALUATIONS);
		this.island = island;
		this.model = model;
	}

	@Override
	public void doAction(Algorithm algorithm) {
		List<Island> islands = model.getIslands();

		//no migrations if there is only one island
		if (islands.size() <= 1) {
			return;
		}

		//perform the migration
		List<Island> neighbors = model.getTopology().getNeighbors(island, islands);
		model.getMigration().migrate(island, neighbors);
	}

}
