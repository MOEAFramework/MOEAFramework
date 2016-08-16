/* Copyright 2009-2016 David Hadka
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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Non-dominated sorting algorithm for dominance depth ranking. Assigns the
 * {@code rank} and {@code crowdingDistance} attributes to solutions. Solutions
 * of rank 0 belong to the Pareto non-dominated front.
 * <p>
 * Despite its name, this naive non-dominated sort implementation tends to be
 * faster than the "fast non-dominated sort" implementation from [1].  This
 * is primarily due to the fact that for the average case, the "fast" version
 * always requires O(MN^2) comparisons while this naive implementations requires
 * only (K-1)/2 * M * (N-1)*N/2, assuming there are K equally sized fronts.
 * <p>
 * References:
 * <ol>
 * <li>Deb et al (2002). "A Fast and Elitist Multiobjective Genetic Algorithm:
 * NSGA-II." IEEE Transactions on Evolutionary Computation. 6(2):182-197.
 * </ol>
 */
public class NondominatedSorting {

	/**
	 * Attribute key for the rank of a solution.
	 */
	public static final String RANK_ATTRIBUTE = "rank";

	/**
	 * Attribute key for the crowding distance of a solution.
	 */
	public static final String CROWDING_ATTRIBUTE = "crowdingDistance";

	/**
	 * The dominance comparator.
	 */
	protected final DominanceComparator comparator;

	/**
	 * Constructs a fast non-dominated sorting operator using Pareto dominance.
	 */
	public NondominatedSorting() {
		this(new ParetoDominanceComparator());
	}

	/**
	 * Constructs a non-dominated sorting operator using the specified
	 * dominance comparator.
	 * 
	 * @param comparator the dominance comparator
	 */
	public NondominatedSorting(DominanceComparator comparator) {
		super();
		this.comparator = comparator;
	}
	
	/**
	 * Returns the dominance comparator used by this non-dominated sorting
	 * routine.
	 * 
	 * @return the dominance comparator used by this non-dominated sorting
	 *         routine
	 */
	public DominanceComparator getComparator() {
		return comparator;
	}

	/**
	 * Performs non-dominated sorting on the specified population,
	 * assigning the {@code rank} and {@code crowdingDistance} attributes to
	 * solutions.
	 * 
	 * @param population the population whose solutions are to be evaluated
	 */
	public void evaluate(Population population) {
		List<Solution> remaining = new ArrayList<Solution>();

		for (Solution solution : population) {
			remaining.add(solution);
		}

		int rank = 0;

		while (!remaining.isEmpty()) {
			NondominatedPopulation front = new NondominatedPopulation(
					comparator, DuplicateMode.ALLOW_DUPLICATES);

			for (Solution solution : remaining) {
				front.add(solution);
			}

			for (Solution solution : front) {
				remaining.remove(solution);
				solution.setAttribute(RANK_ATTRIBUTE, rank);
			}

			updateCrowdingDistance(front);

			rank++;
		}
	}

	/**
	 * Computes and assigns the {@code crowdingDistance} attribute to solutions.
	 * The specified population should consist of solutions within the same
	 * front/rank.
	 * 
	 * @param front the population whose solutions are to be evaluated
	 */
	public void updateCrowdingDistance(Population front) {
		// initially assign all crowding distances of 0.0
		for (Solution solution : front) {
			solution.setAttribute(CROWDING_ATTRIBUTE, 0.0);
		}
		
		// remove any duplicate solutions, the duplicate solutions will retain
		// the crowding distance of 0.0
		Population uniqueFront = new Population();
		
		for (Solution s1 : front) {
			boolean isDuplicate = false;
			
			for (Solution s2 : uniqueFront) {
				if (NondominatedPopulation.distance(s1, s2) < Settings.EPS) {
					isDuplicate = true;
					break;
				}
			}
			
			if (!isDuplicate) {
				uniqueFront.add(s1);
			}
		}
		
		front = uniqueFront;

		// then compute the crowding distance for the unique solutions
		int n = front.size();
		
		if (n < 3) {
			for (Solution solution : front) {
				solution.setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);
			}
		} else {
			int numberOfObjectives = front.get(0).getNumberOfObjectives();

			for (int i = 0; i < numberOfObjectives; i++) {
				front.sort(new ObjectiveComparator(i));

				double minObjective = front.get(0).getObjective(i);
				double maxObjective = front.get(n - 1).getObjective(i);

				front.get(0).setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);
				front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE,
						Double.POSITIVE_INFINITY);

				for (int j = 1; j < n - 1; j++) {
					double distance = (Double)front.get(j).getAttribute(
							CROWDING_ATTRIBUTE);
					distance += (front.get(j + 1).getObjective(i) - 
							front.get(j - 1).getObjective(i))
							/ (maxObjective - minObjective);
					front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
				}
			}
		}
	}

}
