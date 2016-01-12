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

/**
 * Abstract superclass of all distance function implementations.  This class
 * ensures the two nodes provided to the {@link #distance(Node, Node)} method
 * are compatible.
 */
public abstract class DistanceFunction {
	
	/**
	 * Constructs a new distance function.
	 */
	public DistanceFunction() {
		super();
	}

	/**
	 * Computes and returns the distance (or edge weight) between the two
	 * specified nodes.
	 * 
	 * @param node1 the first node
	 * @param node2 the second node
	 * @return the distance between the two nodes
	 * @throws IllegalArgumentException if the nodes are not the same dimension
	 */
	public double distance(Node node1, Node node2) {
		double[] position1 = node1.getPosition();
		double[] position2 = node2.getPosition();
		
		if (position1.length != position2.length) {
			throw new IllegalArgumentException(
					"nodes are not the same dimension");
		}
		
		return distance(position1.length, position1, position2);
	}
	
	/**
	 * Calculates and returns the distance between the two positions.
	 * Implementations should throw an {@link IllegalArgumentException} if any
	 * preconditions fail.
	 * 
	 * @param length the length (or dimension) of the two positions
	 * @param position1 the position of the first node
	 * @param position2 the position of the second node
	 * @return the distance between the two positions
	 */
	public abstract double distance(int length, double[] position1,
			double[] position2);
	
}
