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

import java.util.Arrays;

/**
 * Adaptive grid archive. Divides objective space into a number of grid cells,
 * maintaining a count of the number of solutions within each grid cell. When
 * the size of the archive exceeds a specified capacity, a solution from the
 * most crowded grid cell is selected and removed from the archive.
 * <p>
 * References:
 * <ol>
 * <li>Knowles, J.D. and Corne, D.W., "Approximating the Nondominated Front
 * using the Pareto Archived Evolution Strategy," Evolutionary Computation, vol.
 * 8, no. 2, pp. 149-172, 2000.
 * <li>Knowles, J.D. and Corne, D.W., "Properties of an Adaptive Archiving for
 * Storing Nondominated Vectors," IEEE Transactions on Evolutionary Computation,
 * vol. 7, no. 2, pp. 100-116, 2003.
 * </ol>
 */
public class AdaptiveGridArchive extends NondominatedPopulation {

	/**
	 * The maximum capacity of this archive.
	 */
	protected final int capacity;

	/**
	 * The problem for which this archive is used.
	 */
	protected final Problem problem;

	/**
	 * The number of divisions this archive uses to split each objective.
	 */
	protected final int numberOfDivisions;

	/**
	 * The minimum objective value for each dimension.
	 */
	protected double[] minimum;

	/**
	 * The maximum objective value for each dimension.
	 */
	protected double[] maximum;

	/**
	 * The number of solutions in each grid cell.
	 */
	protected int[] density;

	/**
	 * Constructs an adaptive grid archive with the specified capacity with the
	 * specified number of divisions along each objective.
	 * 
	 * @param capacity the capacity of this archive
	 * @param problem the problem for which this archive is used
	 * @param numberOfDivisions the number of divisions this archive uses to
	 *        split each objective
	 */
	public AdaptiveGridArchive(int capacity, Problem problem,
			int numberOfDivisions) {
		this.capacity = capacity;
		this.problem = problem;
		this.numberOfDivisions = numberOfDivisions;

		minimum = new double[problem.getNumberOfObjectives()];
		maximum = new double[problem.getNumberOfObjectives()];
		density = new int[(int)Math.pow(numberOfDivisions, problem
				.getNumberOfObjectives())];

		adaptGrid();
	}

	/**
	 * Returns the maximum number of solutions stored in this archive.
	 * 
	 * @return the maximum number of solutions stored in this archive
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns the number of divisions this archive uses to split each
	 * objective.
	 * 
	 * @return the number of divisions this archive uses to split each objective
	 */
	public int getNumberOfDivisions() {
		return numberOfDivisions;
	}

	/**
	 * Returns the problem for which this archive is used.
	 * 
	 * @return the problem for which this archive is used
	 */
	public Problem getProblem() {
		return problem;
	}

	@Override
	public boolean add(Solution solution) {
		boolean added = super.add(solution);

		if (!added) {
			return false;
		}

		int index = findIndex(solution);

		if (index < 0) {
			adaptGrid();
		} else {
			density[index]++;
		}

		if (size() > capacity) {
			remove(findDensestIndex());
		}

		return true;
	}

	@Override
	public void remove(int index) {
		int gridIndex = findIndex(get(index));

		super.remove(index);

		density[gridIndex]--;
	}

	@Override
	public boolean remove(Solution solution) {
		boolean removed = super.remove(solution);

		if (removed) {
			density[findIndex(solution)]--;
		}

		return removed;
	}

	@Override
	public void clear() {
		super.clear();
		adaptGrid();
	}

	/**
	 * Returns the index of the solution residing in the densest grid cell. If
	 * there are more than one such solutions, the first value is returned.
	 * 
	 * @return the index of the solution residing in the densest grid cell
	 */
	protected int findDensestIndex() {
		int index = -1;
		int value = -1;

		for (int i = 0; i < size(); i++) {
			int tempValue = density[findIndex(get(i))];

			if (tempValue > value) {
				index = i;
				value = tempValue;
			}
		}

		return index;
	}

	/**
	 * Computes new lower and upper bounds and recalculates the densities of
	 * each grid cell.
	 */
	protected void adaptGrid() {
		Arrays.fill(minimum, Double.POSITIVE_INFINITY);
		Arrays.fill(maximum, Double.NEGATIVE_INFINITY);
		Arrays.fill(density, 0);

		for (Solution solution : this) {
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				minimum[i] = Math.min(minimum[i], solution.getObjective(i));
				maximum[i] = Math.max(maximum[i], solution.getObjective(i));
			}
		}

		for (Solution solution : this) {
			density[findIndex(solution)]++;
		}
	}

	/**
	 * Returns the index of the specified solution in this adaptive grid
	 * archive, or {@code -1} if the solution is not within the current lower
	 * and upper bounds.
	 * 
	 * @param solution the specified solution
	 * @return the index of the specified solution in this adaptive grid
	 *         archive, or {@code -1} if the solution is not within the current
	 *         lower and upper bounds
	 */
	protected int findIndex(Solution solution) {
		int index = 0;

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			double value = solution.getObjective(i);

			if ((value < minimum[i]) || (value > maximum[i])) {
				return -1;
			} else {
				int tempIndex = (int)(numberOfDivisions * 
						((value - minimum[i]) / (maximum[i] - minimum[i])));

				// handle special case where value = maximum[i]
				if (tempIndex == numberOfDivisions) {
					tempIndex--;
				}

				index += tempIndex * (int)Math.pow(numberOfDivisions, i);
			}
		}

		return index;
	}

}
