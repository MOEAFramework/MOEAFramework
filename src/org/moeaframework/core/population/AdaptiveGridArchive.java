/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.population;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.SerializationUtils;
import org.moeaframework.util.Vector;

/**
 * Adaptive grid archive.  Divides objective space into a number of grid cells, maintaining a count of the number of
 * solutions within each grid cell.  When the size of the archive exceeds a specified capacity, a solution from the
 * most crowded grid cell is selected and removed from the archive.
 * <p>
 * This implementation currently stores the density of each grid cell in an array.  As such,
 * {@code pow(numberOfDivisions, numberOfObjectives)} can not exceed the storage capacity of an array, or
 * {@code pow(2, 32)}.  We may consider at some point using sparse arrays to remove this limitation.
 * <p>
 * References:
 * <ol>
 *   <li>Knowles, J.D. and Corne, D.W., "Approximating the Nondominated Front using the Pareto Archived Evolution
 *       Strategy," Evolutionary Computation, vol. 8, no. 2, pp. 149-172, 2000.
 *   <li>Knowles, J.D. and Corne, D.W., "Properties of an Adaptive Archiving for Storing Nondominated Vectors,"
 *       IEEE Transactions on Evolutionary Computation, vol. 7, no. 2, pp. 100-116, 2003.
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
	protected Map<Integer, Integer> density;

	/**
	 * Constructs an adaptive grid archive with the specified capacity with the specified number of divisions along
	 * each objective.
	 * 
	 * @param capacity the capacity of this archive
	 * @param problem the problem for which this archive is used
	 * @param numberOfDivisions the number of divisions this archive uses to split each objective
	 * @throws FrameworkException if {@code pow(numberOfDivisions, numberOfObjectives)} exceeds the storage capacity
	 *         of an array
	 */
	public AdaptiveGridArchive(int capacity, Problem problem, int numberOfDivisions) {
		super(new ParetoDominanceComparator(), DuplicateMode.ALLOW_DUPLICATES);
		this.capacity = capacity;
		this.problem = problem;
		this.numberOfDivisions = numberOfDivisions;
		this.density = new HashMap<>();
		
		try {
			ArithmeticUtils.pow(numberOfDivisions, problem.getNumberOfObjectives());
		} catch (MathArithmeticException e) {
			throw new FrameworkException("Number of divisions (bisections) too large for adaptive grid archive", e);
		}

		adaptGrid();
	}
	
	/**
	 * Calculates the largest possible value for the {@code bisections} parameter.  This is limited by the available
	 * Java heap size and the maximum integer value (2^31-1), whichever is smaller.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @return the maximum number of bisections
	 */
	public static int getMaximumBisections(int numberOfObjectives) {
		return (int)Math.round(Math.log(Math.pow(Integer.MAX_VALUE, 1.0 / numberOfObjectives)) / Math.log(2.0));
	}
	
	/**
	 * Returns the equivalent number of bisections to produce the number of divisions.
	 * 
	 * @return the number of bisections
	 */
	public int getBisections() {
		return (int)Math.round(Math.log(numberOfDivisions) / Math.log(2));
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
	 * Returns the number of divisions this archive uses to split each objective.
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
		// check if the candidate dominates or is dominated by any member in
		// the archive
		Iterator<Solution> iterator = iterator();

		while (iterator.hasNext()) {
			Solution oldSolution = iterator.next();
			int flag = comparator.compare(solution, oldSolution);
			
			if (flag < 0) {
				// candidate dominates a member of the archive
				iterator.remove();
			} else if (flag > 0) {
				// candidate is dominated by a member of the archive
				return false;
			}
		}
		
		// if archive is empty, add the candidate
		if (isEmpty()) {
			super.forceAddWithoutCheck(solution);
			adaptGrid();
			return true;
		}
		
		// temporarily add the candidate solution
		super.forceAddWithoutCheck(solution);
		int index = findIndex(solution);
		
		if (index < 0) {
			adaptGrid();
			index = findIndex(solution);
		} else {
			incrementDensity(index);
		}
		
		if (size() <= capacity) {
			// if archive is not exceeding capacity, keep the candidate
			return true;
		} else if (getDensity(index) == getDensity(findDensestCell())) {
			// if the candidate is in the most dense cell, reject the candidate
			remove(solution);
			return false;
		} else {
			// otherwise keep the candidate and remove a solution from the most
			// dense cell
			remove(pickSolutionFromDensestCell());
			return true;
		}
	}

	@Override
	public void remove(int index) {
		int gridIndex = findIndex(get(index));

		super.remove(index);

		if (decrementDensity(gridIndex) == 0) {
			adaptGrid();
		}
	}

	@Override
	public boolean remove(Solution solution) {
		boolean removed = super.remove(solution);

		if (removed) {
			int gridIndex = findIndex(solution);
			
			if (decrementDensity(gridIndex) == 0) {
				adaptGrid();
			}
		}

		return removed;
	}

	@Override
	public void clear() {
		super.clear();
		adaptGrid();
	}
	
	/**
	 * Returns the index of the grid cell with the largest density.
	 * 
	 * @return the index of the grid cell with the largest density
	 */
	protected int findDensestCell() {
		int index = -1;
		int value = -1;
		
		for (int i = 0; i < size(); i++) {
			int tempIndex = findIndex(get(i));
			int tempValue = getDensity(tempIndex);
			
			if (tempValue > value) {
				value = tempValue;
				index = tempIndex;
			}
		}
		
		return index;
	}

	/**
	 * Returns a solution residing in the densest grid cell.  If there are more than one such solution or multiple
	 * cells with the same density, the first solution encountered is returned.
	 * 
	 * @return a solution residing in the densest grid cell
	 */
	protected Solution pickSolutionFromDensestCell() {
		Solution solution = null;
		int value = -1;

		for (int i = 0; i < size(); i++) {
			int tempValue = getDensity(findIndex(get(i)));

			if (tempValue > value) {
				solution = get(i);
				value = tempValue;
			}
		}

		return solution;
	}

	/**
	 * Computes new lower and upper bounds and recalculates the densities of each grid cell.
	 */
	protected void adaptGrid() {
		if (isEmpty()) {
			// these values make findIndex return -1
			minimum = Vector.of(problem.getNumberOfObjectives(), Double.POSITIVE_INFINITY);
			maximum = Vector.of(problem.getNumberOfObjectives(), Double.NEGATIVE_INFINITY);
		} else {
			minimum = getLowerBounds();
			maximum = getUpperBounds();
		}

		density.clear();

		for (Solution solution : this) {
			incrementDensity(findIndex(solution));
		}
	}

	/**
	 * Returns the index of the specified solution in this adaptive grid archive, or {@code -1} if the solution is
	 * not within the current lower and upper bounds.
	 * 
	 * @param solution the specified solution
	 * @return the index of the specified solution in this adaptive grid archive, or {@code -1} if the solution is
	 *         not within the current lower and upper bounds
	 */
	public int findIndex(Solution solution) {
		int index = 0;

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			double value = solution.getObjectiveValue(i);

			if ((value < minimum[i]) || (value > maximum[i])) {
				return -1;
			} else {
				int tempIndex = (int)(numberOfDivisions * ((value - minimum[i]) / (maximum[i] - minimum[i])));

				// handle special case where value = maximum[i]
				if (tempIndex == numberOfDivisions) {
					tempIndex--;
				}

				index += tempIndex * ArithmeticUtils.pow(numberOfDivisions, i);
			}
		}

		return index;
	}
	
	/**
	 * Returns the density of the solution at the given index.
	 * 
	 * @param index the solution index
	 * @return the density of the solution at the given index
	 */
	public int getDensity(int index) {
		return density.getOrDefault(index, 0);
	}
	
	/**
	 * Increments the density at the specified index.
	 * 
	 * @param index the solution index
	 * @return the new density
	 */
	private int incrementDensity(int index) {
		int value = density.getOrDefault(index, 0) + 1;
		density.put(index, value);
		return value;
	}
	
	/**
	 * Decrements the density at the specified index.  The density will not go below {@code 0}.
	 * 
	 * @param index the solution index
	 * @return the new density
	 */
	private int decrementDensity(int index) {
		int value = density.getOrDefault(index, 0) - 1;
		density.put(index, value < 0 ? 0 : value);
		return value;
	}
	
	@Override
	public AdaptiveGridArchive copy() {
		AdaptiveGridArchive result = new AdaptiveGridArchive(capacity, problem, numberOfDivisions);
		
		for (Solution solution : this) {
			result.forceAddWithoutCheck(solution.copy());
		}
		
		result.adaptGrid();
		return result;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeObject(minimum);
		stream.writeObject(maximum);
		SerializationUtils.writeMap(density, stream);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		minimum = (double[])stream.readObject();
		maximum = (double[])stream.readObject();
		density = SerializationUtils.readMap(Integer.class, Integer.class, HashMap::new, stream);
	}

}
