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

import java.util.BitSet;
import java.util.Iterator;

import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

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
	 * those whose Euclidean distance, either in decision or objective space,
	 * is smaller than {@value Settings.EPSILON}.
	 */
	public static enum DuplicateMode {
		
		/**
		 * Do not allow any duplicate solutions into this population.
		 */
		NO_DUPLICATES,
		
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
	 * Constructs an empty non-dominated population using the specified 
	 * dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated
	 *        population
	 */
	public NondominatedPopulation(DominanceComparator comparator) {
		this(comparator, DuplicateMode.NO_DUPLICATES);
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
	public NondominatedPopulation(DominanceComparator comparator,
			boolean allowDuplicates) {
		this(comparator, allowDuplicates ? DuplicateMode.ALLOW_DUPLICATES :
			DuplicateMode.NO_DUPLICATES);
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
			} else {
				switch (duplicateMode) {
				case NO_DUPLICATES:
					if (objectiveDistance(newSolution, oldSolution) < Settings.EPS) {
						return false;
					}
					
					break;
				case ALLOW_DUPLICATE_OBJECTIVES:
					if (variableDistance(newSolution, oldSolution) < Settings.EPS) {
						return false;
					}
					
					break;
				case ALLOW_DUPLICATES:
					break;
				}
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
			} else {
				switch (duplicateMode) {
				case NO_DUPLICATES:
					if (objectiveDistance(newSolution, oldSolution) < Settings.EPS) {
						return;
					}
					
					break;
				case ALLOW_DUPLICATE_OBJECTIVES:
					if (variableDistance(newSolution, oldSolution) < Settings.EPS) {
						return;
					}
					
					break;
				case ALLOW_DUPLICATES:
					break;
				}
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
	protected double objectiveDistance(Solution s1, Solution s2) {
		double distance = 0.0;

		for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
			distance += Math.pow(s1.getObjective(i) - s2.getObjective(i), 2.0);
		}

		return Math.sqrt(distance);
	}
	
	protected double variableDistance(Solution s1, Solution s2) {
		double distance = 0.0;
		
		for (int i = 0; i < s1.getNumberOfVariables(); i++) {
			Variable v1 = s1.getVariable(i);
			Variable v2 = s2.getVariable(i);
			
			if ((v1 instanceof RealVariable) && (v2 instanceof RealVariable)) {
				distance += Math.pow(EncodingUtils.getReal(v1),
						EncodingUtils.getReal(v2));
			} else if ((v1 instanceof BinaryVariable) && (v2 instanceof BinaryVariable)) {
				BitSet bs1 = EncodingUtils.getBitSet(v1);
				BitSet bs2 = EncodingUtils.getBitSet(v2);
				
				bs1.xor(bs2);
				
				distance += bs1.cardinality();
			} else if ((v1 instanceof Permutation) && (v2 instanceof Permutation)) {
				int[] p1 = EncodingUtils.getPermutation(v1);
				int[] p2 = EncodingUtils.getPermutation(v2);
				
				for (int j = 0; j < p1.length; j++) {
					for (int k = 0; k < p2.length; k++) {
						if (p2[k] == p1[j]) {
							distance += Math.abs(k-j);
						}
					}
				}
			} else {
				// TODO: add better calculations for other types
				distance += 1.0;
			}
		}
		
		return distance;
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
