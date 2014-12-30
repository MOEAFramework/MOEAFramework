/* Copyright 2009-2015 David Hadka
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

import java.util.Iterator;

import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * A population that maintains the property of pair-wise non-dominance between
 * all solutions. When the {@code add} method is invoked with a new solution,
 * all solutions currently in the population that are dominated by the new
 * solution are removed. If the new solution is dominated by any member of the
 * population, the new solution is not added.
 */
public class NondominatedPopulation extends Population {

	/**
	 * The dominance comparator used by this non-dominated population.
	 */
	private final DominanceComparator comparator;

	/**
	 * Constructs an empty non-dominated population using the Pareto dominance
	 * relation.
	 */
	public NondominatedPopulation() {
		this(new ParetoDominanceComparator());
	}

	/**
	 * Constructs an empty non-dominated population using the specified 
	 * dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 */
	public NondominatedPopulation(DominanceComparator comparator) {
		super();
		this.comparator = comparator;
	}

	/**
	 * Constructs a non-dominated population using the Pareto dominance relation
	 * and initialized with the specified solutions.
	 * 
	 * @param iterable the solutions used to initialize this non-dominated
	 *        population
	 */
	public NondominatedPopulation(Iterable<? extends Solution> iterable) {
		this();
		addAll(iterable);
	}

	/**
	 * Constructs a non-dominated population using the specified dominance
	 * comparator and initialized with the specified solutions.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 * @param iterable the solutions used to initialize this non-dominated
	 *        population
	 */
	public NondominatedPopulation(DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		this(comparator);
		addAll(iterable);
	}

	/**
	 * If {@code newSolution} is dominates any solution or is non-dominated with
	 * all solutions in this population, the dominated solutions are removed and
	 * {@code newSolution} is added to this population. Otherwise,
	 * {@code newSolution} is dominated and is not added to this population.
	 */
	@Override
	public boolean add(Solution newSolution) {
		Iterator<Solution> iterator = iterator();

		while (iterator.hasNext()) {
			Solution oldSolution = iterator.next();
			int flag = comparator.compare(newSolution, oldSolution);

			if (flag < 0) {
				iterator.remove();
			} else if (flag > 0) {
				return false;
			} else if (distance(newSolution, oldSolution) < Settings.EPS) {
				return false;
			}
		}

		return super.add(newSolution);
	}

	/**
	 * Adds the specified solution to the population, bypassing the
	 * non-domination check. This method should only be used when a
	 * non-domination check has been performed elsewhere, such as in a subclass.
	 * <p>
	 * <b>This method should only be used internally, and should never be made
	 * public by any subclasses.</b>
	 * 
	 * @param newSolution the solution to be added
	 * @return true if the population was modified as a result of this operation
	 */
	protected boolean forceAddWithoutCheck(Solution newSolution) {
		return super.add(newSolution);
	}

	/**
	 * Returns the Euclidean distance between two solutions in objective space.
	 * 
	 * @param s1 the first solution
	 * @param s2 the second solution
	 * @return the distance between the two solutions in objective space
	 */
	protected double distance(Solution s1, Solution s2) {
		double distance = 0.0;

		for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
			distance += Math.pow(s1.getObjective(i) - s2.getObjective(i), 2.0);
		}

		return Math.sqrt(distance);
	}

	/**
	 * Returns the dominance comparator used by this non-dominated population.
	 * 
	 * @return the dominance comparator used by this non-dominated population
	 */
	public DominanceComparator getComparator() {
		return comparator;
	}

}
