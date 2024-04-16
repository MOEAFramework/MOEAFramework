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
package org.moeaframework.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.util.io.Resources;

/**
 * A population that maintains the property of pair-wise non-dominance between all solutions. When the {@code add}
 * method is invoked with a new solution, all solutions currently in the population that are dominated by the new
 * solution are removed. If the new solution is dominated by any member of the population, the new solution is not
 * added.
 * <p>
 * <strong>Avoid modifying solutions contained in non-dominated populations.</strong>  Since the dominance checks are
 * only performed when adding solutions, modifying an existing solution can violating this contract.
 */
public class NondominatedPopulation extends Population {
	
	/**
	 * Specifies how duplicate solutions are handled.  Duplicate solutions are those whose Euclidean distance in
	 * objective space are less than {@value Settings#EPS}.
	 */
	public static enum DuplicateMode {
		
		/**
		 * Do not allow any duplicate solutions into this population.
		 */
		NO_DUPLICATE_OBJECTIVES,
		
		/**
		 * Allow duplicate solutions only if they have different decision variables.
		 */
		ALLOW_DUPLICATE_OBJECTIVES,
		
		/**
		 * Allow all duplicate solutions, even if they have identical decision variables and objectives.
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
	 * Constructs an empty non-dominated population using the Pareto dominance relation.
	 */
	public NondominatedPopulation() {
		this(new ParetoDominanceComparator());
	}
	
	/**
	 * Constructs an empty non-dominated population using the Pareto dominance relation.
	 * 
	 * @param duplicateMode specifies how duplicate solutions are handled
	 */
	public NondominatedPopulation(DuplicateMode duplicateMode) {
		this(new ParetoDominanceComparator(), duplicateMode);
	}

	/**
	 * Constructs an empty non-dominated population using the specified dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated population
	 */
	public NondominatedPopulation(DominanceComparator comparator) {
		this(comparator, Settings.getDuplicateMode());
	}
	
	/**
	 * Constructs an empty non-dominated population using the specified dominance relation.
	 * 
	 * @param comparator the dominance relation used by this non-dominated population
	 * @param duplicateMode specifies how duplicate solutions are handled
	 */
	public NondominatedPopulation(DominanceComparator comparator, DuplicateMode duplicateMode) {
		super();
		this.comparator = comparator;
		this.duplicateMode = duplicateMode;
	}

	/**
	 * Constructs a non-dominated population using the Pareto dominance relation and initialized with the specified
	 * solutions.
	 * 
	 * @param iterable the solutions used to initialize this non-dominated population
	 */
	public NondominatedPopulation(Iterable<? extends Solution> iterable) {
		this();
		addAll(iterable);
	}

	/**
	 * Constructs a non-dominated population using the specified dominance comparator and initialized with the
	 * specified solutions.
	 * 
	 * @param comparator the dominance relation used by this non-dominated population
	 * @param iterable the solutions used to initialize this non-dominated population
	 */
	public NondominatedPopulation(DominanceComparator comparator, Iterable<? extends Solution> iterable) {
		this(comparator);
		addAll(iterable);
	}

	/**
	 * If {@code newSolution} is dominates any solution or is non-dominated with all solutions in this population,
	 * the dominated solutions are removed and {@code newSolution} is added to this population. Otherwise,
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
	 * Replace is not supported with non-dominated populations.  Calling this method will throw an exception.
	 * 
	 * @throws UnsupportedOperationException if this method is called
	 */
	@Override
	public void replace(int index, Solution newSolution) {
		throw new UnsupportedOperationException("replace not supported with NondominatedPopulation");
	}

	/**
	 * Adds the specified solution to the population, bypassing the non-domination check. This method should only be
	 * used when a non-domination check has been performed elsewhere, such as in a subclass.
	 * <p>
	 * <b>This method should only be used internally, and should never be made public by any subclasses.</b>
	 * 
	 * @param newSolution the solution to be added
	 * @return true if the population was modified as a result of this operation
	 */
	protected boolean forceAddWithoutCheck(Solution newSolution) {
		return super.add(newSolution);
	}
	
	/**
	 * Returns {@code true} if the two solutions are duplicates and one should be ignored based on the duplicate mode.
	 * This default implementation depends on the {@link #equals(Object)} method of the {@link Variable}
	 * class to check for equality of the decision variables.
	 * 
	 * @param s1 the first solution
	 * @param s2 the second solution
	 * @return {@code true} if the solutions are duplicates; {@code false} otherwise
	 */
	protected boolean isDuplicate(Solution s1, Solution s2) {
		return switch (duplicateMode) {
			case NO_DUPLICATE_OBJECTIVES -> s1.euclideanDistance(s2) < Settings.EPS;
			case ALLOW_DUPLICATE_OBJECTIVES -> {
				if (s1.getNumberOfVariables() != s2.getNumberOfVariables()) {
					yield false;
				}
				
				for (int i = 0; i < s1.getNumberOfVariables(); i++) {
					if (!s1.getVariable(i).equals(s2.getVariable(i))) {
						yield false;
					}
				}
				
				yield true;
			}
			case ALLOW_DUPLICATES -> false;
		};
	}

	/**
	 * Returns the dominance comparator used by this non-dominated population.
	 * 
	 * @return the dominance comparator used by this non-dominated population
	 */
	public DominanceComparator getComparator() {
		return comparator;
	}
	
	@Override
	public NondominatedPopulation copy() {
		NondominatedPopulation result = new NondominatedPopulation(getComparator());
		
		for (Solution solution : this) {
			result.forceAddWithoutCheck(solution.copy());
		}
		
		return result;
	}
	
	/**
	 * Loads a reference set, which contains the objective values for a set of non-dominated solutions.  Any dominated
	 * solutions are discarded.
	 * 
	 * @param resource the path of the file or resource on the classpath
	 * @return the reference set, or {@code null} if the file or resource was not found
	 * @throws IOException if an I/O error occurred
	 */
	public static NondominatedPopulation loadReferenceSet(String resource) throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return loadReferenceSet(file);
		} else {
			try (Reader reader = Resources.asReader(NondominatedPopulation.class, "/" + resource)) {
				if (reader != null) {
					return new NondominatedPopulation(Population.loadObjectives(reader));
				}
			}
		}
		
		throw new FileNotFoundException(resource);
	}
	
	/**
	 * Loads a reference set file, which contains the objective values for a set of non-dominated solutions.  Any
	 * dominated solutions are discarded.
	 * 
	 * @param file the file containing the reference set
	 * @return the reference set
	 * @throws IOException if an I/O error occurred or the file was not found
	 */
	public static NondominatedPopulation loadReferenceSet(File file) throws IOException {
		return new NondominatedPopulation(Population.loadObjectives(file));
	}

}
