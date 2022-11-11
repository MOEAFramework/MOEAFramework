package org.moeaframework.parallel.island;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

/**
 * Represents an island in an island model for parallelization.
 * Each island is essentially a separate instance of an
 * optimization algorithm.  Migration events can periodically
 * copy or move solutions between islands.
 */
public class Island {
	
	/**
	 * The current optimization algorithm used by this island.
	 */
	private final Algorithm algorithm;
	
	/**
	 * The current population of this island.
	 */
	private final Population population;
	
	/**
	 * Synchronized queue of incoming solutions migrating from other islands.
	 */
	private final Queue<Solution> immigrationQueue;

	/**
	 * Creates a new island with the given algorithm and population.
	 * 
	 * @param algorithm the algorithm assigned to this island
	 * @param population the initial population for this island
	 */
	public Island(Algorithm algorithm, Population population) {
		super();
		this.algorithm = algorithm;
		this.population = population;
		
		immigrationQueue = new ConcurrentLinkedQueue<Solution>();
	}

	/**
	 * Returns the current optimization algorithm used by this island.
	 * 
	 * @return the algorithm
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Returns the current population of this island.
	 * 
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}
	
	/**
	 * Returns the immigration queue for this island.  Neighboring islands should
	 * add solutions into the immigration queue, which will then get injected into the
	 * population at an opportune time.
	 * 
	 * @return the immigration queue
	 */
	public Queue<Solution> getImmigrationQueue() {
		return immigrationQueue;
	}

}
