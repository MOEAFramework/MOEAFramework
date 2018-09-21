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
package org.moeaframework.examples.ga.tsplib;

/**
 * Enumeration of the supported data types.
 */
public enum DataType {
	
	/**
	 * Data for a symmetric traveling salesman problem.
	 */
	TSP,
	
	/**
	 * Data for an asymmetric traveling salesman problem.
	 */
	ATSP,
	
	/**
	 * Data for a sequential ordering problem.
	 */
	SOP,
	
	/**
	 * Hamiltonian cycle problem data.
	 */
	HCP,
	
	/**
	 * Capacitated vehicle routing problem data.
	 */
	CVRP,
	
	/**
	 * A collection of tours.
	 */
	TOUR

}
