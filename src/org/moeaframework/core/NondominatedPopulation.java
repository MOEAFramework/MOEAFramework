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
	 * Specifies how duplicate solutions are handled.  Duplicate solutions are
	 * those whose Euclidean distance in objective space are less than
	 * {@value Settings#EPS}.
	 */
	public static enum DuplicateMode {
		
		/**
		 * Do not allow any duplicate solutions into this population.
		 */
		NO_DUPLICATE_OBJECTIVES,
		
		/**
		 * Allow duplicate solutions only if they have different decision
		 * variables.
		 */
		ALLOW_DUPLICATE_OBJECTIVES,
		
		/**
		 * Allow all duplicate solutions, even if they have identical decision
		 * variables and objectives.
		 */
		ALLOW_DUPLICATES
		
	}

	/**
	 * The dominance comparator used by this non-dominated population.
	 */
	protected final DominanceComparator comparator;
	
	/**
	 * Specifies how duplicate solutions are handled. 
	 */
	protected final DuplicateMode duplicateMode;

	/**
	 * Constructs an empty non-dominated population using the Pareto dominance
	 * relation.
	 */
	public NondominatedPopulation() {
		this(new ParetoDominanceComparator());
	}
	
	/**
	 * Constructs an empty non-dominated population using the Pareto dominance
	 * relation.
	 * 
	 * @param duplicateMode specifies how duplicate solutions are handled
	 */
	public NondominatedPopulation(DuplicateMode duplicateMode) {
		this(new ParetoDominanceComparator(), duplicateMode);
	}

	/**
	 * Constructs an empty non-dominated population using the specified 
	 * dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 */
	public NondominatedPopulation(DominanceComparator comparator) {
		this(comparator, Settings.getDuplicateMode());
	}
	
	/**
	 * Constructs an empty non-dominated population using the specified 
	 * dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 * @param allowDuplicates allow duplicate solutions into the non-dominated
	 *        population
	 * @deprecated Use {@link #NondominatedPopulation(DominanceComparator,
	 * 		  DuplicateMode)} instead.
	 */
	@Deprecated
	public NondominatedPopulation(DominanceComparator comparator,
			boolean allowDuplicates) {
		this(comparator, allowDuplicates ?
				DuplicateMode.ALLOW_DUPLICATES :
				DuplicateMode.NO_DUPLICATE_OBJECTIVES);
	}
	
	/**
	 * Constructs an empty non-dominated population using the specified 
	 * dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 * @param duplicateMode specifies how duplicate solutions are handled
	 */
	public NondominatedPopulation(DominanceComparator comparator,
			DuplicateMode duplicateMode) {
		super();
		this.comparator = comparator;
		this.duplicateMode = duplicateMode;
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
			} else if (isDuplicate(newSolution, oldSolution)) {
				return false;
			}
		}

		return super.add(newSolution);
	}

	/**
	 * Replace the solution at the given index with the new solution, but only
	 * if the new solution is non-dominated.  To maintain non-dominance within
	 * this population, any solutions dominated by the new solution will also
	 * be replaced.
	 */
	@Override
	public void replace(int index, Solution newSolution) {
		Iterator<Solution> iterator = iterator();

		while (iterator.hasNext()) {
			Solution oldSolution = iterator.next();
			int flag = comparator.compare(newSolution, oldSolution);

			if (flag < 0) {
				iterator.remove();
			} else if (flag > 0) {
				return;
			} else if (isDuplicate(newSolution, oldSolution)) {
				return;
			}
		}

		super.replace(index, newSolution);
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
	protected static double distance(Solution s1, Solution s2) {
		double distance = 0.0;

		for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
			distance += Math.pow(s1.getObjective(i) - s2.getObjective(i), 2.0);
		}

		return Math.sqrt(distance);
	}
	
	/**
	 * Returns {@code true} if the two solutions are duplicates and one should
	 * be ignored based on the duplicate mode.  This default implementation
	 * depends on the {@link #equals(Object)} method of the {@link Variable}
	 * class to check for equality of the decision variables.
	 * 
	 * @param s1 the first solution
	 * @param s2 the second solution
	 * @return {@code true} if the solutions are duplicates; {@code false}
	 *         otherwise
	 */
	protected boolean isDuplicate(Solution s1, Solution s2) {
		switch (duplicateMode) {
		case NO_DUPLICATE_OBJECTIVES:
			return distance(s1, s2) < Settings.EPS;
		case ALLOW_DUPLICATE_OBJECTIVES:
			if (s1.getNumberOfVariables() != s2.getNumberOfVariables()) {
				return false;
			}
			
			for (int i = 0; i < s1.getNumberOfVariables(); i++) {
				if (!s1.getVariable(i).equals(s2.getVariable(i))) {
					return false;
				}
			}
			
			return true;
		default:
			return false;
		}
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
