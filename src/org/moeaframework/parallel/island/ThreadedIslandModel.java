package org.moeaframework.parallel.island;

import java.util.ArrayList;
import java.util.List;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.topology.Topology;

/**
 * An island model designed to execute each island on a separate thread.
 */
public class ThreadedIslandModel implements IslandModel {
	
	/**
	 * The migration frequency.
	 */
	protected final int migrationFrequency;
	
	/**
	 * The migration strategy.
	 */
	protected final Migration migration;
	
	/**
	 * The island topology.
	 */
	protected final Topology topology;
	
	/**
	 * The list of islands.
	 */
	protected final List<Island> islands;
	
	/**
	 * Creates a new threaded island model.
	 * 
	 * @param migrationFrequency the migration frequency
	 * @param migration the migration strategy
	 * @param topology the island topology
	 */
	public ThreadedIslandModel(int migrationFrequency, Migration migration, Topology topology) {
		super();
		this.migrationFrequency = migrationFrequency;
		this.migration = migration;
		this.topology = topology;
		
		islands = new ArrayList<Island>();
	}
	
	@Override
	public int getMigrationFrequency() {
		return migrationFrequency;
	}

	@Override
	public Migration getMigration() {
		return migration;
	}

	@Override
	public Topology getTopology() {
		return topology;
	}

	@Override
	public List<Island> getIslands() {
		return islands;
	}

	@Override
	public void addIsland(Island island) {
		synchronized (islands) {
			islands.add(island);
		}
	}
	
	@Override
	public NondominatedPopulation run(final int maxEvaluations) {
		final int startingEvaluations = getNumberOfEvaluations();
		List<IslandMigrationAction> migrationActions = new ArrayList<IslandMigrationAction>();
		
		//initialize the migration actions
		synchronized (islands) {
			for (Island island : islands) {
				migrationActions.add(new IslandMigrationAction(island, this));
			}
		}
			
		//create the threads
		List<Thread> threads = new ArrayList<Thread>();
			
		for (final IslandMigrationAction action : migrationActions) {
			threads.add(new Thread() {

				public void run() {
					while (getNumberOfEvaluations() - startingEvaluations < maxEvaluations) {
						action.step();
					}
				}

			});
		}

		//start the threads
		for (Thread thread : threads) {
			thread.start();
		}

		//wait for threads to finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new FrameworkException("thread was interrupted", e);
			}
		}

		//aggregate the result
		NondominatedPopulation result = new NondominatedPopulation();

		for (IslandMigrationAction action : migrationActions) {
			result.addAll(action.getResult());
		}

		return result;
	}
	
	@Override
	public int getNumberOfEvaluations() {
		synchronized (islands) {
			int total = 0;
			
			for (Island island : islands) {
				total += island.getAlgorithm().getNumberOfEvaluations();
			}
			
			return total;
		}
	}

}
