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
