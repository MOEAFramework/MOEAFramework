/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
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
