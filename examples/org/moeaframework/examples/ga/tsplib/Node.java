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

import java.util.Arrays;

/**
 * Represents a node (i.e., city) or arbitrary dimension.
 */
public class Node {
	
	/**
	 * The identifier of this node.
	 */
	private final int id;
	
	/**
	 * The position of this node.
	 */
	private final double[] position;
	
	/**
	 * Constructs a new node with the specified identifier and position.
	 * 
	 * @param id the identifier of this node
	 * @param position the position of this node
	 */
	public Node(int id, double... position) {
		super();
		this.id = id;
		this.position = position;
	}

	/**
	 * Returns the identifier of this node.
	 * 
	 * @return the identifier of this node
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the position of this node.
	 * 
	 * @return the position of this node
	 */
	public double[] getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		
		for (int i = 0; i < position.length; i++) {
			sb.append(' ');
			sb.append(position[i]);
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(position);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Node other = (Node) obj;
		
		if (id != other.id) {
			return false;
		}
		
		if (!Arrays.equals(position, other.position)) {
			return false;
		}
		
		return true;
	}

}
