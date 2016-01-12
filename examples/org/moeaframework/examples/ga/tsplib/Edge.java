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
 * An edge between two nodes.
 */
public class Edge {
	
	/**
	 * The identifier of the first node.
	 */
	private final int id1;
	
	/**
	 * The identifier of the second node.
	 */
	private final int id2;
	
	/**
	 * Constructs an edge between two nodes.
	 * 
	 * @param id1 the identifier of the first node
	 * @param id2 the identifier of the second node
	 */
	public Edge(int id1, int id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}

	/**
	 * Returns the identifier of the first node.
	 * 
	 * @return the identifier of the first node
	 */
	public int getId1() {
		return id1;
	}

	/**
	 * Returns the identifier of the second node.
	 * 
	 * @return the identifier of the second node
	 */
	public int getId2() {
		return id2;
	}
	
	/**
	 * Returns {@code true} if either endpoint of this edge is the specified
	 * node; {@code false} otherwise.
	 * 
	 * @param id the identifier of the node
	 * @return {@code true} if either endpoint of this edge is the specified
	 *         node; {@code false} otherwis
	 */
	public boolean hasEndpoint(int id) {
		return (id == id1) || (id == id2);
	}
	
	/**
	 * Returns the opposite endpoint of this edge.
	 * 
	 * @param id the identifier of the one endpoint
	 * @return the opposite endpoint of this edge
	 * @throws IllegalArgumentException if this edge does not have the any
	 *         endpoint with the specified identifier
	 */
	public int getOppositeEndpoint(int id) {
		if (id == id1) {
			return id2;
		} else if (id == id2) {
			return id1;
		} else {
			throw new IllegalArgumentException("edge does not have endpoint " +
					id);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		// since the edge is undirected, hashCode must not depend on the order
		// of the identifier; this if condition ensures that hashCode is
		// consistent with the equals method
		if (id1 < id2) {
			result = prime * result + id1;
			result = prime * result + id2;
		} else {
			result = prime * result + id2;
			result = prime * result + id1;
		}
		
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
		
		Edge other = (Edge)obj;
		
		return ((id1 == other.id1) && (id2 == other.id2)) ||
			   ((id1 == other.id2) && (id2 == other.id1));
	}

}
