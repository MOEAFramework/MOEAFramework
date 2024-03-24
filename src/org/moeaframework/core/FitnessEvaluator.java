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

/**
 * Evaluates a population and assigns fitness values to its solutions. This class is intended to be used by
 * indicator-based algorithms.
 */
public interface FitnessEvaluator {

	/**
	 * Attribute key for the fitness of a solution.
	 */
	public static final String FITNESS_ATTRIBUTE = "fitness";

	/**
	 * Evaluates the solutions in the specified population assigning the {@code FITNESS_ATTRIBUTE} attribute.
	 * 
	 * @param population the population to be evaluated
	 */
	public void evaluate(Population population);
	
	/**
	 * Returns {@code true} if larger fitness values are preferred; otherwise smaller fitness values are preferred.
	 * 
	 * @return {@code true} if larger fitness values are preferred; otherwise smaller fitness values are preferred
	 */
	public boolean areLargerValuesPreferred();
	
	/**
	 * Returns the fitness value for the given solution.
	 * 
	 * @param solution the solution
	 * @return the fitness value
	 */
	public static double getFitness(Solution solution) {
		return (Double)solution.getAttribute(FITNESS_ATTRIBUTE);
	}
	
	/**
	 * Sets the fitness value on the given solution.
	 * 
	 * @param solution the solution
	 * @param value the fitness value
	 */
	public static void setFitness(Solution solution, double value) {
		solution.setAttribute(FITNESS_ATTRIBUTE, value);
	}

}
