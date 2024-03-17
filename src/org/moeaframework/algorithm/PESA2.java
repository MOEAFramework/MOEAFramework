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
package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.moeaframework.core.AdaptiveGridArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of the Pareto Envelope-based Selection Algorithm (PESA2).
 * <p>
 * References:
 * <ol>
 *   <li>Corne, D. W., N. R. Jerram, J. D. Knowles, and M. J. Oates (2001).  PESA-II: Region-based Selection in
 *       Evolutionary Multiobjective Optimization.  Proceedings of the Genetic and Evolutionary Computation Conference
 *       (GECCO 2001), pp. 283-290.
 *   <li>Corne, D. W., J. D. Knowles, and M. J. Oates (2000).  The Pareto Envelope-based Selection Algorithm for
 *       Multiobjective Optimization.  Parallel Problem Solving from Nature PPSN VI, pp. 839-848.
 * </ol>
 */
public class PESA2 extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	final Selection selection;
	
	/**
	 * A mapping from grid index to the solutions occupying that grid index.  This enables PESA2's region-based
	 * selection.
	 */
	Map<Integer, List<Solution>> gridMap;
	
	/**
	 * Constructs a new PESA2 instance with default settings.
	 * 
	 * @param problem the problem
	 */
	public PESA2(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem),
				8,
				100);
	}

	/**
	 * Constructs a new PESA2 instance.
	 * 
	 * @param problem the problem
	 * @param initialPopulationSize the initial population size
	 * @param variation the mutation operator
	 * @param initialization the initialization operator
	 * @param bisections the number of bisections in the adaptive grid archive
	 * @param archiveSize the capacity of the adaptive grid archive
	 */
	public PESA2(Problem problem, int initialPopulationSize, Variation variation, Initialization initialization,
			int bisections, int archiveSize) {
		super(problem,
				initialPopulationSize,
				new Population(),
				new AdaptiveGridArchive(archiveSize, problem, ArithmeticUtils.pow(2, bisections)),
				initialization,
				variation);
		
		selection = new RegionBasedSelection();
	}
	
	@Override
	public AdaptiveGridArchive getArchive() {
		return (AdaptiveGridArchive)super.getArchive();
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}
	
	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}

	@Override
	protected void iterate() {
		Population population = getPopulation();
		AdaptiveGridArchive archive = getArchive();
		Variation variation = getVariation();
		int populationSize = population.size();
		
		// clear the population; selection draws from archive only
		population.clear();
		
		// generate the mapping between grid indices and solutions
		gridMap = createGridMap();

		while (population.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(), archive);
			Solution[] children = variation.evolve(parents);

			population.addAll(children);
		}
		
		evaluateAll(population);
		archive.addAll(population);
	}
	
	/**
	 * Returns a mapping from grid index to the solutions occupying that grid index.  The key is the grid index, and
	 * the value is the list of solutions occupying that index.
	 * 
	 * @return a mapping from grid index to the solutions occupying that grid index
	 */
	protected Map<Integer, List<Solution>> createGridMap() {
		AdaptiveGridArchive archive = getArchive();
		Map<Integer, List<Solution>> result = new HashMap<Integer, List<Solution>>();
		
		for (Solution solution : archive) {
			int index = archive.findIndex(solution);
			List<Solution> solutions = result.get(index);
			
			if (solutions == null) {
				solutions = new ArrayList<Solution>();
				result.put(index, solutions);
			}
			
			solutions.add(solution);
		}
		
		return result;
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("archiveSize") || properties.contains("bisections")) {
			int archiveSize = properties.getInt("archiveSize", getArchive().getCapacity());
			int bisections = properties.getInt("bisections", getArchive().getBisections());
			setArchive(new AdaptiveGridArchive(archiveSize, problem, ArithmeticUtils.pow(2, bisections)));
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		properties.setInt("archiveSize", getArchive().getCapacity());
		properties.setInt("bisections", getArchive().getBisections());
		
		return properties;
	}
	
	/**
	 * Region-based selection.  Instead of selecting individual solutions, PESA2 first selects hyperboxes using binary
	 * tournament selection to favor hyperboxes with lower density.  Then, one solution from the selected hyperbox is
	 * returned.
	 */
	public class RegionBasedSelection implements Selection {
		
		/**
		 * Constructs a new region-based selection instance.
		 */
		public RegionBasedSelection() {
			super();
		}

		@Override
		public Solution[] select(int arity, Population population) {
			Solution[] result = new Solution[arity];
			
			for (int i = 0; i < arity; i++) {
				result[i] = select();
			}
			
			return result;
		}
		
		/**
		 * Draws a random entry from the map.
		 * 
		 * @return the randomly selected map entry
		 */
		protected Entry<Integer, List<Solution>> draw() {
			int index = PRNG.nextInt(gridMap.size());
			Iterator<Entry<Integer, List<Solution>>> iterator = gridMap.entrySet().iterator();
			
			while (iterator.hasNext()) {
				Entry<Integer, List<Solution>> entry = iterator.next();
				
				if (index == 0) {
					return entry;
				} else {
					index--;
				}
			}
			
			throw new NoSuchElementException();
		}
		
		/**
		 * Selects one solution using PESA2's region-based selection scheme.
		 * 
		 * @return the selected solution
		 */
		protected Solution select() {
			AdaptiveGridArchive archive = getArchive();
			Entry<Integer, List<Solution>> entry1 = draw();
			Entry<Integer, List<Solution>> entry2 = draw();
			Entry<Integer, List<Solution>> selection = entry1;
			
			// pick the grid index with smaller density
			if (entry1 != entry2) {
				if ((archive.getDensity(entry2.getKey()) < archive.getDensity(entry1.getKey())) ||
						(archive.getDensity(entry2.getKey()) == archive.getDensity(entry1.getKey()) && PRNG.nextBoolean())) {	
					selection = entry2;
				}
			}
			
			// randomly pick a solution from the selected grid index
			return PRNG.nextItem(selection.getValue());
		}
		
	}

}
