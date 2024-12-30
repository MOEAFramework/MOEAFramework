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
package org.moeaframework.core.fitness;

import org.moeaframework.core.population.Population;

/**
 * Evaluates a population and assigns fitness values to its solutions.  This class is intended to be used by
 * indicator-based algorithms.
 */
public interface FitnessEvaluator {

	/**
	 * Evaluates the fitness of solutions in the population, updating the
	 * {@link org.moeaframework.core.attribute.Fitness} attribute.
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

}
