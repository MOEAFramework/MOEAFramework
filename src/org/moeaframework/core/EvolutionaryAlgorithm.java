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
package org.moeaframework.core;

/**
 * Interface for an evolutionary algorithm. Evolutionary algorithms are
 * characterized by their use of a <em>population</em> and inspiration from
 * biological and other natural processes.
 */
public interface EvolutionaryAlgorithm extends Algorithm {

	/**
	 * Returns the current population of this evolutionary algorithm.
	 * 
	 * @return the current population of this evolutionary algorithm
	 */
	public Population getPopulation();

	/**
	 * Returns the current non-dominated archive of the best solutions generated
	 * by this evolutionary algorithm, or {@code null} if no archive is used.
	 * 
	 * @return the current non-dominated archive of the best solutions generated
	 *         by this evolutionary algorithm, or {@code null} if no archive is
	 *         used
	 */
	public NondominatedPopulation getArchive();

}