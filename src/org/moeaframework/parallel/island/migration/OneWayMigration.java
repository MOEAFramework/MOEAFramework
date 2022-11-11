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
 * A one-way migration where some number of emigrants are copied
 * from the target island to the current island.  Solutions that are
 * dominated are replaced by the emigrants, otherwise random solutions
 * are replaced.
 */
public class OneWayMigration implements Migration {
	
	/**
	 * The number of solutions moved between the islands.
	 */
	private final int numberOfEmigrants;
	
	/**
	 * The process for selecting emigrants.
	 */
	private final Selection selection;
	
	/**
	 * Creates a new one-way migration strategy.
	 * 
	 * @param numberOfEmigrants the number of solutions moved between the islands
	 * @param selection the process for selecting emigrants
	 */
	public OneWayMigration(int numberOfEmigrants, Selection selection) {
		super();
		this.numberOfEmigrants = numberOfEmigrants;
		this.selection = selection;
	}

	@Override
	public void migrate(Island currentIsland, Island targetIsland) {
		Population current = currentIsland.getPopulation();
		Population target = targetIsland.getPopulation();
		
		synchronized (target) {
			synchronized (current) {
				int originalSize = current.size();
				Solution[] emigrants = selection.select(numberOfEmigrants, target);
				
				for (int i = 0; i < emigrants.length; i++) {
					current.add(emigrants[i].copy());
				}
				
				if (current.size() > originalSize) {
					List<Solution> dominated = findDominated(current, emigrants);
					
					while (!dominated.isEmpty() && (current.size() > originalSize)) {
						current.remove(dominated.remove(dominated.size()-1));
					}
					
					while (current.size() > originalSize) {
						current.remove(PRNG.nextInt(current.size()));
					}
				}
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
	private List<Solution> findDominated(Population population, Solution[] emigrants) {
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
