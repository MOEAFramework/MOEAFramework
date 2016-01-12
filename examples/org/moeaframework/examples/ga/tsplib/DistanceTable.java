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
package org.moeaframework.examples.ga.tsplib;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A distance table provides a lookup of the distances between the nodes in a
 * TSPLIB problem instance.
 */
public abstract class DistanceTable {
	
	/**
	 * Constructs a new distance table instance.
	 */
	public DistanceTable() {
		super();
	}
	
	/**
	 * Returns the identifiers of all nodes in this distance table.
	 * 
	 * @return the identifiers of all nodes in this distance table
	 */
	public abstract int[] listNodes();
	
	/**
	 * Returns the identifiers of all neighbors of the specified node.  A
	 * neighbor must have a direct edge between itself and the specified
	 * node.
	 * 
	 * @param id the identifier of the node whose neighbors are enumerated
	 * @return the identifiers of all neighbors of the specified node
	 * @throws IllegalArgumentException if no node exists with the specified
	 *         identifier
	 */
	public abstract int[] getNeighborsOf(int id);
	
	/**
	 * Returns the distance between the two specified nodes.
	 * 
	 * @param id1 the identifier of the first node
	 * @param id2 the identifier of the second node
	 * @return the distance between the two specified nodes
	 * @throws IllegalArgumentException if there is no direct edge between the
	 *         two nodes, or if no node exists with the specified identifier
	 */
	public abstract double getDistanceBetween(int id1, int id2);
	
	/**
	 * Loads the distance table from the specified reader.
	 * 
	 * @param reader the reader containing the distance table
	 * @throws IOException if an I/O error occurred while reading the distance
	 *         table
	 */
	public abstract void load(BufferedReader reader) throws IOException;
	
	/**
	 * Returns {@code true} if the specified nodes are neighbors; {@code false}
	 * otherwise.
	 * 
	 * @param id1 the identifier of the first node
	 * @param id2 the identifier of the second node
	 * @return {@code true} if the specified nodes are neighbors; {@code false}
	 *         otherwise
	 */
	public boolean isNeighbor(int id1, int id2) {
		int[] neighbors = getNeighborsOf(id1);
		
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] == id2) {
				return true;
			}
		}
		
		return false;
	}

}
