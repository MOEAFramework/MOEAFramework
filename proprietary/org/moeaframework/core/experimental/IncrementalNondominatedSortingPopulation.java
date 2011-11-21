/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.core.experimental;

import static org.moeaframework.core.FastNondominatedSorting.CROWDING_ATTRIBUTE;
import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.LexicographicalComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

public class IncrementalNondominatedSortingPopulation extends Population {

	private int numberOfObjectives;

	private List<NonDominatedFront> fronts;

	private DominanceComparator comparator;

	public static final String DOMINATES_ATTRIBUTE = "dominates";

	public IncrementalNondominatedSortingPopulation(int numberOfObjectives) {
		this.numberOfObjectives = numberOfObjectives;

		fronts = new ArrayList<NonDominatedFront>();
		comparator = new ParetoDominanceComparator();
	}

	public boolean add(Solution solution) {
		int rank = 0;

		// find the front where this solution is added
		while (rank < fronts.size()) {
			boolean isDominated = false;
			NonDominatedFront front = fronts.get(rank);

			for (int i = 0; i < front.size(); i++) {
				Solution existingSolution = front.get(i);
				int flag = comparator.compare(solution, existingSolution);

				if (flag > 0) {
					isDominated = true;
				}
			}

			if (!isDominated) {
				break;
			}

			rank++;
		}

		if (rank < fronts.size()) {
			// start the push back procedure with the solution
			List<Solution> temp = new ArrayList<Solution>();
			temp.add(solution);
			pushBack(temp, rank);
		} else {
			// create new front for the solution
			NonDominatedFront newFront = new NonDominatedFront(
					numberOfObjectives);
			newFront.add(solution);
			fronts.add(newFront);
		}

		return true;
	}

	private void pushBack(List<Solution> solutions, int rank) {
		List<Solution> dominatedSolutions = new ArrayList<Solution>();
		NonDominatedFront front = fronts.get(rank);

		// find all solutions in current front that are dominated
		for (int i = 0; i < front.size(); i++) {
			Solution currentSolution = front.getWithoutCrowding(i);

			for (Solution solution : solutions) {
				int flag = comparator.compare(solution, currentSolution);

				if (flag < 0) {
					dominatedSolutions.add(currentSolution);
					break;
				}
			}
		}

		// remove dominated solutions
		for (Solution solution : dominatedSolutions) {
			front.remove(solution);
		}

		// add the solutions being pushed back
		for (Solution solution : solutions) {
			front.add(solution);
		}

		// recursive call to handle next front, or create new front if we are
		// already at the last
		if (!dominatedSolutions.isEmpty()) {
			if (rank == fronts.size() - 1) {
				NonDominatedFront newFront = new NonDominatedFront(
						numberOfObjectives);
				for (Solution solution : dominatedSolutions) {
					newFront.add(solution);
				}
				fronts.add(newFront);
			} else {
				pushBack(dominatedSolutions, rank + 1);
			}
		}
	}

	public void clear() {
		fronts.clear();
	}

	public boolean contains(Solution solution) {
		return indexOf(solution) >= 0;
	}

	public Solution get(int index) {
		int rank = 0;

		for (NonDominatedFront front : fronts) {
			if (front.size() - 1 < index) {
				index -= front.size();
			} else {
				Solution solution = front.get(index);
				solution.setAttribute(RANK_ATTRIBUTE, rank);
				return solution;
			}

			rank++;
		}

		throw new IndexOutOfBoundsException();
	}

	public int indexOf(Solution solution) {
		int index = 0;

		for (NonDominatedFront front : fronts) {
			int tempIndex = front.indexOf(solution);

			if (tempIndex >= 0) {
				return index + tempIndex;
			} else {
				index += front.size();
			}
		}

		return -1;
	}

	public boolean isEmpty() {
		return fronts.isEmpty();
	}

	public void remove(int index) {
		for (NonDominatedFront front : fronts) {
			if (front.size() < index) {
				index -= front.size();
			} else {
				front.remove(index);
			}
		}

		throw new IndexOutOfBoundsException();
	}

	public boolean remove(Solution solution) {
		for (NonDominatedFront front : fronts) {
			if (front.remove(solution)) {
				return true;
			}
		}

		return false;
	}

	public int size() {
		int size = 0;

		for (NonDominatedFront front : fronts) {
			size += front.size();
		}

		return size;
	}

	public void removeMostCrowded() {
		NonDominatedFront front = fronts.get(fronts.size() - 1);
		Solution mostCrowded = front.get(0);

		for (int i = 1; i < front.size(); i++) {
			Solution solution = front.get(i);

			if ((Double)mostCrowded.getAttribute(CROWDING_ATTRIBUTE) > (Double)solution
					.getAttribute(CROWDING_ATTRIBUTE)) {
				mostCrowded = solution;
			}
		}

		front.remove(mostCrowded);

		if (front.isEmpty()) {
			fronts.remove(fronts.size() - 1);
		}
	}

	@Override
	public Iterator<Solution> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sort(Comparator<? super Solution> comparator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		throw new UnsupportedOperationException();
	}

	private static class NonDominatedFront {

		private int numberOfObjectives;

		/**
		 * The i-th entry in the outer list contains the solutions sorted by the
		 * i-th objective. The order of the elements in this front are specified
		 * by the order of elements in the first inner list.
		 */
		private List<ArrayList<Solution>> data;

		/**
		 * The i-th entry is the comparator used for sorting by the i-th
		 * objective.
		 */
		private List<MyObjectiveComparator> comparators;

		public NonDominatedFront(int numberOfObjectives) {
			this.numberOfObjectives = numberOfObjectives;

			data = new ArrayList<ArrayList<Solution>>(numberOfObjectives);
			comparators = new ArrayList<MyObjectiveComparator>(
					numberOfObjectives);

			for (int i = 0; i < numberOfObjectives; i++) {
				data.add(new ArrayList<Solution>());
				comparators.add(new MyObjectiveComparator(i));
			}
		}

		public void add(Solution solution) {
			for (int i = 0; i < numberOfObjectives; i++) {
				ArrayList<Solution> sortedSolutions = data.get(i);
				sortedSolutions.add(solution);
				Collections.sort(sortedSolutions, comparators.get(i));
			}
		}

		public boolean remove(Solution solution) {
			if (!contains(solution)) {
				return false;
			}

			for (int i = 0; i < numberOfObjectives; i++) {
				ArrayList<Solution> sortedSolutions = data.get(i);
				int index = Collections.binarySearch(sortedSolutions, solution,
						comparators.get(i));
				sortedSolutions.remove(index);
			}

			return true;
		}

		public void remove(int index) {
			remove(data.get(0).get(index));
		}

		public Solution get(int index) {
			Solution solution = data.get(0).get(index);

			double crowdingDistance = 0.0;

			for (int i = 0; i < numberOfObjectives; i++) {
				ArrayList<Solution> sortedSolutions = data.get(i);
				int j = Collections.binarySearch(sortedSolutions, solution,
						comparators.get(i));

				if ((j == 0) || (j == sortedSolutions.size() - 1)) {
					crowdingDistance = Double.POSITIVE_INFINITY;
				} else {
					crowdingDistance += (sortedSolutions.get(j + 1)
							.getObjective(i) - sortedSolutions.get(j - 1)
							.getObjective(i))
							/ (sortedSolutions.get(sortedSolutions.size() - 1)
									.getObjective(i) - sortedSolutions.get(0)
									.getObjective(i));
				}
			}

			solution.setAttribute(CROWDING_ATTRIBUTE, crowdingDistance);
			return solution;
		}

		Solution getWithoutCrowding(int index) {
			return data.get(0).get(index);
		}

		public int size() {
			return data.get(0).size();
		}

		public boolean contains(Solution solution) {
			return indexOf(solution) >= 0;
		}

		public int indexOf(Solution solution) {
			int index = Collections.binarySearch(data.get(0), solution,
					comparators.get(0));

			if (index < 0) {
				return -1;
			} else {
				return index;
			}
		}

		public boolean isEmpty() {
			return data.get(0).isEmpty();
		}

	}

	private static class MyObjectiveComparator implements Comparator<Solution>,
	Serializable {

		private static final long serialVersionUID = 4838611497587537654L;

		private final LexicographicalComparator lexicographicalComparator;

		/**
		 * The objective to be compared.
		 */
		private final int objective;

		/**
		 * Constructs the comparator for comparing solutions using the value of
		 * the specified objective.
		 * 
		 * @param objective
		 */
		public MyObjectiveComparator(int objective) {
			this.objective = objective;

			lexicographicalComparator = new LexicographicalComparator();
		}

		/**
		 * Compares the two solutions using the value of the specified
		 * objective.
		 */
		@Override
		public int compare(Solution solution1, Solution solution2) {
			double value1 = solution1.getObjective(objective);
			double value2 = solution2.getObjective(objective);

			int flag = Double.compare(value1, value2);

			if (flag == 0) {
				flag = lexicographicalComparator.compare(solution1, solution2);
			}

			return flag;
		}

	}

}
