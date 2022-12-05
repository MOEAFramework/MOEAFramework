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
package org.moeaframework.parallel.island;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Population;
import org.moeaframework.parallel.util.ImmigrationQueue;

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
	private final ImmigrationQueue immigrationQueue;

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
		
		immigrationQueue = new ImmigrationQueue();
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
	public ImmigrationQueue getImmigrationQueue() {
		return immigrationQueue;
	}

}
