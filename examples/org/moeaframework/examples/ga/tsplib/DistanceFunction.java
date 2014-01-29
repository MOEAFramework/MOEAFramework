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
