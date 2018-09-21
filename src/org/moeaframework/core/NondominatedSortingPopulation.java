/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.core;

import static org.moeaframework.core.NondominatedSorting.RANK_ATTRIBUTE;

import java.util.Comparator;
import java.util.Iterator;

import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.comparator.RankComparator;

/**
 * Population that maintains the {@code rank} and {@code crowdingDistance}
 * attributes for its solutions by invoking
 * {@link NondominatedSorting#evaluate(Population)}. This population tracks
 * modifications and performs fast non-dominated sorting only when required.
 * Only changes made to this population can be tracked; changes made directly
 * to the contained solutions will not be detected.  Therefore, it may be
 * necessary to invoke {@link #update()} manually.
 * <p>
 * The iterator() method returned by {@link Population} must use the
 * {@code size()}, {@code get(int)} and {@code remove(int)} methods to ensure
 * proper functionality.
 */
public class NondominatedSortingPopulation extends Population {

	/**
	 * {@code true} if the population has been modified but fast non-dominated
	 * sorting has not yet been invoked; {@code false} otherwise.
	 */
	private boolean modified;

	/**
	 * The fast non-dominated sorting implementation.
	 */
	private final NondominatedSorting nondominatedSorting;

	/**
	 * Constructs an empty population that maintains the {@code rank} and
	 * {@code crowdingDistance} attributes for its solutions.
	 */
	public NondominatedSortingPopulation() {
		this(new ParetoDominanceComparator());
	}

	/**
	 * Constructs an empty population that maintains the {@code rank} and
	 * {@code crowdingDistance} attributes for its solutions.
	 * 
	 * @param comparator the dominance comparator
	 */
	public NondominatedSortingPopulation(DominanceComparator comparator) {
		super();
		modified = false;
		
		if (Settings.useFastNondominatedSorting()) {
			nondominatedSorting = new FastNondominatedSorting(comparator);
		} else {
			nondominatedSorting = new NondominatedSorting(comparator);
		}
	}

	/**
	 * Constructs a population initialized with the specified solutions that 
	 * maintains the {@code rank} and {@code crowdingDistance} attributes for 
	 * its solutions.
	 * 
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public NondominatedSortingPopulation(DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		this(comparator);
		addAll(iterable);
	}

	/**
	 * Constructs a population initialized with the specified solutions that 
	 * maintains the {@code rank} and {@code crowdingDistance} attributes for 
	 * its solutions.
	 * 
	 * @param iterable the solutions used to initialize this population
	 */
	public NondominatedSortingPopulation(Iterable<? extends Solution> iterable) {
		this(new ParetoDominanceComparator(), iterable);
	}

	@Override
	public boolean add(Solution solution) {
		modified = true;
		return super.add(solution);
	}

	@Override
	public void replace(int index, Solution solution) {
		modified = true;
		super.replace(index, solution);
	}

	@Override
	public Solution get(int index) {
		if (modified) {
			update();
		}

		return super.get(index);
	}

	@Override
	public void remove(int index) {
		modified = true;
		super.remove(index);
	}

	@Override
	public boolean remove(Solution solution) {
		modified = true;
		return super.remove(solution);
	}

	@Override
	public void clear() {
		modified = true;
		super.clear();
	}

	@Override
	public Iterator<Solution> iterator() {
		if (modified) {
			update();
		}

		return super.iterator();
	}

	@Override
	public void sort(Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.sort(comparator);
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.truncate(size, comparator);
	}

	/**
	 * Equivalent to calling {@code truncate(size, 
	 * new NondominatedSortingComparator())}.
	 * 
	 * @param size the target population size after truncation
	 */
	public void truncate(int size) {
		truncate(size, new NondominatedSortingComparator());
	}
	
	/**
	 * Prunes the population to the specified size.  This is similar to
	 * {@link #truncate(int)}, except the crowding distance is recalculated
	 * each time a solution is removed.
	 * 
	 * @param size the target population size after pruning
	 */
	public void prune(int size) {
		if (modified) {
			update();
		}

		sort(new RankComparator());

		//collect all solutions in the front which must be pruned
		//note the use of super to prevent repeatedly triggering update()
		int maxRank = (Integer)super.get(size-1).getAttribute(RANK_ATTRIBUTE);
		Population front = new Population();

		for (int i=size()-1; i>=0; i--) {
			Solution solution = super.get(i);
			int rank = (Integer)solution.getAttribute(RANK_ATTRIBUTE);
			
			if (rank >= maxRank) {
				super.remove(i);
			
				if (rank == maxRank) {
					front.add(solution);
				}
			}
		}
		
		//prune front until correct size
		while (size() + front.size() > size) {
			nondominatedSorting.updateCrowdingDistance(front);
			front.truncate(front.size()-1, new CrowdingComparator());
		}
		
		addAll(front);
	}

	/**
	 * Updates the rank and crowding distance of all solutions in this
	 * population.  This method will in general be called automatically when
	 * the population is modified.  However, only changes made to this
	 * population can be tracked; changes made directly to the contained
	 * solutions will not be detected.  Therefore, it may be necessary to
	 * invoke {@link #update()} manually.
	 */
	public void update() {
		modified = false;
		nondominatedSorting.evaluate(this);
	}

}
