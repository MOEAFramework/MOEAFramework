package org.moeaframework.parallel.island;

import java.util.List;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.topology.Topology;

/**
 * A complete island model, including the islands, their topology,
 * the migration schedule, etc.
 */
public interface IslandModel {

	/**
	 * Returns the migration frequency, in function evaluations, used by this island
	 * model.
	 * 
	 * @return the migration frequency
	 */
	public int getMigrationFrequency();

	/**
	 * Returns the migration strategy used by this island model.
	 * 
	 * @return the migration strategy
	 */
	public Migration getMigration();

	/**
	 * Returns the island topology, describing neighboring islands for the purpose
	 * of migrations.
	 * 
	 * @return the island topology
	 */
	public Topology getTopology();
	
	/**
	 * Returns the number of function evaluations summed across all islands.
	 * 
	 * @return the total number of function evaluations
	 */
	public int getNumberOfEvaluations();

	/**
	 * Adds a new island to this island model.
	 * 
	 * @param island the island to add
	 */
	public void addIsland(Island island);
	
	/**
	 * Returns all islands contained within this island model.
	 * 
	 * @return the list of islands
	 */
	public List<Island> getIslands();
	
	/**
	 * Executes this island model for the given number of function evaluations.
	 * 
	 * @param maxEvaluations the maximum number of evaluations across all islands
	 * @return the resulting non-dominated population aggregated across all islands
	 */
	public NondominatedPopulation run(int maxEvaluations);

}