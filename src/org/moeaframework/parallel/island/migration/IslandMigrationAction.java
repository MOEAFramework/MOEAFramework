package org.moeaframework.parallel.island.migration;

import java.util.List;
import org.moeaframework.algorithm.PeriodicAction;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;

/**
 * A periodic action that triggers island migrations.
 */
public class IslandMigrationAction extends PeriodicAction {

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
	public IslandMigrationAction(Island island, IslandModel model) {
		super(island.getAlgorithm(), model.getMigrationFrequency(), FrequencyType.EVALUATIONS);
		this.island = island;
		this.model = model;
	}

	@Override
	public void doAction() {
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
