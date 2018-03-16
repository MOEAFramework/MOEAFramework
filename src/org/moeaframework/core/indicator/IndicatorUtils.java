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
package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Collection of methods shared by indicators.
 */
public class IndicatorUtils {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private IndicatorUtils() {
		super();
	}

	/**
	 * Returns the Manhattan distance in objective space between the two
	 * solutions.
	 * 
	 * @param problem the problem
	 * @param a the first solution
	 * @param b the second solution
	 * @return the Manhattan distance in objective space between the two
	 *         solutions
	 */
	public static double manhattanDistance(Problem problem, Solution a,
			Solution b) {
		return distance(problem, a, b, 1.0);
	}

	/**
	 * Returns the Euclidean distance in objective space between the two
	 * solutions.
	 * 
	 * @param problem the problem
	 * @param a the first solution
	 * @param b the second solution
	 * @return the Euclidean distance in objective space between the two
	 *         solutions
	 */
	public static double euclideanDistance(Problem problem, Solution a,
			Solution b) {
		return distance(problem, a, b, 2.0);
	}

	/**
	 * Returns the distance in objective space between the two solutions.
	 * 
	 * @param problem the problem
	 * @param a the first solution
	 * @param b the second solution
	 * @param power the power ({@code 1.0} for Manhattan distance, {@code 2.0}
	 *        for Euclidean distance)
	 * @return the distance in objective space between the two solutions
	 */
	private static double distance(Problem problem, Solution a, Solution b,
			double power) {
		double distance = 0.0;

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			distance += Math.pow(Math.abs(a.getObjective(i) - 
					b.getObjective(i)), power);
		}

		return Math.pow(distance, 1.0 / power);
	}

	/**
	 * Returns the Euclidean distance in objective space between the specified
	 * solution and the nearest solution in the population.
	 * 
	 * @param problem the problem
	 * @param solution the solution
	 * @param population the population
	 * @return the Euclidean distance in objective space between the specified
	 *         solution and the nearest solution in the population
	 */
	public static double distanceToNearestSolution(Problem problem,
			Solution solution, NondominatedPopulation population) {
		double minimum = Double.POSITIVE_INFINITY;

		for (int i = 0; i < population.size(); i++) {
			minimum = Math.min(minimum, euclideanDistance(problem, solution,
					population.get(i)));
		}

		return minimum;
	}

}
