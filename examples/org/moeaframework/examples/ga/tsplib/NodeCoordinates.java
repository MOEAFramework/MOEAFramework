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
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the nodes in a TSPLIB problem instance and provides methods for 
 * calculating the distances between nodes.
 */
public class NodeCoordinates extends DistanceTable {
	
	/**
	 * The number of nodes to load into this problem instance.
	 */
	private final int size;
	
	/**
	 * The type of coordinates, used to ensure the TSPLIB problem instance is
	 * parsed correctly.
	 */
	private final NodeCoordType type;
	
	/**
	 * The distance function.
	 */
	private final DistanceFunction distanceFunction;

	/**
	 * The mapping from identifiers to nodes.
	 */
	private final Map<Integer, Node> nodes;
	
	/**
	 * Constructs a new, empty node coordinates instance.
	 * 
	 * @param size the number of nodes to load into this problem instance
	 * @param edgeWeightType the edge weight type
	 */
	public NodeCoordinates(int size, EdgeWeightType edgeWeightType) {
		this(size, edgeWeightType.getNodeCoordType(),
				edgeWeightType.getDistanceFunction());
	}
	
	/**
	 * Constructs a new, empty node coordinates instance.
	 * 
	 * @param size the number of nodes to load into this problem instance
	 * @param type the type of coordinates (i.e., 2D or 3D)
	 * @param distanceFunction the distance function
	 */
	public NodeCoordinates(int size, NodeCoordType type,
			DistanceFunction distanceFunction) {
		super();
		this.size = size;
		this.type = type;
		this.distanceFunction = distanceFunction;
		
		nodes = new HashMap<Integer, Node>();
	}
	
	@Override
	public void load(BufferedReader reader) throws IOException {
		for (int i = 0; i < size; i++) {
			String line = reader.readLine();
			String[] tokens = line.trim().split("\\s+");

			if (tokens.length != type.getLength() + 1) {
				throw new IOException(
						"invalid number of tokens for node entry");
			}

			double[] position = new double[type.getLength()];
			int id = Integer.parseInt(tokens[0]);

			for (int j = 0; j < type.getLength(); j++) {
				position[j] = Double.parseDouble(tokens[j+1]);
			}

			add(new Node(id, position));
		}
	}
	
	/**
	 * Adds the specified node to this problem instance.  If a node with the
	 * same identifier already exists, the previous node will be replaced.
	 * 
	 * @param node the node to add
	 */
	protected void add(Node node) {
		nodes.put(node.getId(), node);
	}
	
	/**
	 * Returns the node with the specified identifier.
	 * 
	 * @param id the identifier of the node to return
	 * @return the node with the specified identifier
	 */
	public Node get(int id) {
		return nodes.get(id);
	}
	
	/**
	 * Removes the node with the specified identifier from this problem
	 * instance.
	 * 
	 * @param id the identifier of the node to remove
	 */
	protected void remove(int id) {
		nodes.remove(id);
	}
	
	/**
	 * Removes all nodes from this problem instance.
	 */
	protected void clear() {
		nodes.clear();
	}
	
	/**
	 * Returns the number of nodes that this instance contains.
	 * 
	 * @return the number of nodes that this instance contains
	 */
	public int size() {
		return nodes.size();
	}
	
	@Override
	public int[] listNodes() {
		int index = 0;
		int[] result = new int[size];
		
		for (Node node : nodes.values()) {
			result[index++] = node.getId();
		}
		
		return result;
	}

	@Override
	public int[] getNeighborsOf(int id) {
		int index = 0;
		int[] neighbors = new int[size-1];
		
		if (!nodes.containsKey(id)) {
			throw new IllegalArgumentException("no node with identifier " + id);
		}
		
		for (Node node : nodes.values()) {
			if (node.getId() != id) {
				neighbors[index++] = node.getId();
			}
		}
		
		return neighbors;
	}

	@Override
	public double getDistanceBetween(int id1, int id2) {
		Node node1 = get(id1);
		Node node2 = get(id2);
		
		if (node1 == null) {
			throw new IllegalArgumentException("no node with identifier " +
					id1);
		}
		
		if (node2 == null) {
			throw new IllegalArgumentException("no node with identifier " +
					id2);
		}
		
		return distanceFunction.distance(get(id1), get(id2));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Node node : nodes.values()) {
			sb.append(node.toString());
			sb.append('\n');
		}
		
		return sb.toString();
	}

}
