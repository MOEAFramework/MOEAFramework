package org.moeaframework.parallel.island;

import java.util.ArrayList;
import java.util.List;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.topology.Topology;

/**
 * Describes the design of an island model parallelization strategy conceptually,
 * including the islands, their topology, the migration schedule, etc.
 */
public class IslandModel {
	
	private final int migrationFrequency;
	
	private final Migration migration;
	
	private final Topology topology;
	
	private final List<Island> islands;
	
	public IslandModel(int migrationFrequency, Migration migration, Topology topology) {
		super();
		this.migrationFrequency = migrationFrequency;
		this.migration = migration;
		this.topology = topology;
		
		islands = new ArrayList<Island>();
	}

	/**
	 * Returns the migration frequency, in function evaluations, used by this island
	 * model.
	 * 
	 * @return the migration frequency
	 */
	public int getMigrationFrequency() {
		return migrationFrequency;
	}

	/**
	 * Returns the migration strategy used by this island model.
	 * 
	 * @return the migration strategy
	 */
	public Migration getMigration() {
		return migration;
	}

	/**
	 * Returns the island topology, describing neighboring islands for the purpose
	 * of migrations.
	 * 
	 * @return the island topology
	 */
	public Topology getTopology() {
		return topology;
	}

	/**
	 * Adds a new island to this island model.
	 * 
	 * @param island the island to add
	 */
	public void addIsland(Island island) {
		islands.add(island);
	}
	
	/**
	 * Returns all islands contained within this island model.
	 * 
	 * @return the list of islands
	 */
	public List<Island> getIslands() {
		return islands;
	}

}