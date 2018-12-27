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
package org.moeaframework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Utility methods for solutions.
 */
public class SolutionUtils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private SolutionUtils() {
		super();
	}
	
	/**
	 * Converts the population to a list of solutions.
	 * 
	 * @param population the population
	 * @return the list of solutions in the population
	 */
	public static List<Solution> toList(Population population) {
		List<Solution> result = new ArrayList<Solution>(population.size());
		
		for (Solution solution : population) {
			result.add(solution.copy());
		}
		
		return result;
	}
	
	/**
	 * Converts an array of solutions to a list of solutions.
	 * 
	 * @param solutions the array of solutions
	 * @return the list of solutions
	 */
	public static List<Solution> toList(Solution[] solutions) {
		return Arrays.asList(solutions);
	}
	
	/**
	 * Converts a population to a list of solutions, creating copies of each solution.
	 * 
	 * @param population the population
	 * @return the copied solutions in the population
	 */
	public static List<Solution> copyToList(Population population) {
		List<Solution> result = new ArrayList<Solution>(population.size());
		
		for (Solution solution : population) {
			result.add(solution.copy());
		}
		
		return result;
	}
	
	/**
	 * Converts an array of solutions to a list of solutions, creating copies of each solution.
	 * 
	 * @param solutions the array of solutions
	 * @return the list of copied solutions
	 */
	public static List<Solution> copyToList(Solution[] solutions) {
		List<Solution> result = new ArrayList<Solution>(solutions.length);
		
		for (Solution solution : solutions) {
			result.add(solution.copy());
		}
		
		return result;
	}

}
