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

import java.util.ArrayList;
import java.util.List;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.parallel.island.Island;

/**
 * Migration strategy that sends migrants to exactly one neighboring
 * island each iteration.
 */
public class SingleNeighborMigration implements Migration {
	
	/**
	 * The number of solutions migrated each iteration.
	 */
	private final int size;
	
	/**
	 * The process for selecting emigrants.
	 */
	private final Selection selection;
	
	/**
	 * Creates a new one-way migration strategy.
	 * 
	 * @param size the number of solutions migrated each iteration
	 * @param selection the process for selecting emigrants
	 */
	public SingleNeighborMigration(int size, Selection selection) {
		super();
		this.size = size;
		this.selection = selection;
	}

	@Override
	public void migrate(Island currentIsland, List<Island> neighbors) {
		Population current = currentIsland.getPopulation();
		
		//pick one neighboring island for migration
		Island targetIsland = PRNG.nextItem(neighbors);
		
		//send solutions to the immigration queue of neighboring islands
		Solution[] emigrants = selection.select(size, current);
		targetIsland.getImmigrationQueue().addAll(emigrants);
		
		//receive any migrants in the immigration queue, possibly replacing
		//current population members
		int originalSize = current.size();
		List<Solution> immigrants = currentIsland.getImmigrationQueue().popAll();
		
		current.addAll(immigrants);
		
		if (current.size() > originalSize) {
			List<Solution> dominated = findDominated(current, immigrants);
				
			while (!dominated.isEmpty() && (current.size() > originalSize)) {
				current.remove(dominated.remove(dominated.size()-1));
			}
			
			while (current.size() > originalSize) {
				current.remove(PRNG.nextInt(current.size()));
			}
		}
	}
	
	/**
	 * Returns all solutions in the population that are dominated by one or more
	 * emigrants.
	 * 
	 * @param population the current population
	 * @param emigrants the emigrating solutions
	 * @return all solutions in the population that are dominated
	 */
	private List<Solution> findDominated(Population population, List<Solution> emigrants) {
		List<Solution> result = new ArrayList<Solution>();
		ParetoDominanceComparator comparator = new ParetoDominanceComparator();
		
		for (Solution solution : population) {
			for (Solution emigrant : emigrants) {
				if (comparator.compare(emigrant, solution) < 0) {
					result.add(solution);
					break;
				}
			}
		}
		
		return result;
	}

}
