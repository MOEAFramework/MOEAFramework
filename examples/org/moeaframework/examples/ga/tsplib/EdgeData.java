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
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Stores the edges in a graph.
 */
public class EdgeData extends DistanceTable {
	
	/**
	 * The number of nodes represented in this graph.
	 */
	private final int size;
	
	/**
	 * The format of the edge data section.
	 */
	private final EdgeDataFormat format;
	
	/**
	 * The edges.
	 */
	private final List<Edge> edges;
	
	/**
	 * Constructs a new, empty graph with no edges.
	 * 
	 * @param size the number of nodes represented in this graph
	 * @param format the format of the edge data section
	 */
	public EdgeData(int size, EdgeDataFormat format) {
		super();
		this.size = size;
		this.format = format;
		
		edges = new ArrayList<Edge>();
	}
	
	/**
	 * Reads the next line of adjacent edges, adding the parsed values to the
	 * queue.
	 * 
	 * @param reader the reader containing the adjacent edge data
	 * @param entries the queue of identifies read by this method
	 * @throws IOException if an I/O error occurred while reading the adjacent
	 *         edge data
	 */
	private void readNextLine(BufferedReader reader, Queue<Integer> entries)
			throws IOException {
		String line = reader.readLine();
		
		if (line == null) {
			throw new EOFException("unexpectedly reached EOF");
		}
		
		String[] tokens = line.trim().split("\\s+");
		
		for (int i = 0; i < tokens.length; i++) {
			entries.offer(Integer.parseInt(tokens[i]));
		}
	}
	
	@Override
	public void load(BufferedReader reader) throws IOException {
		String line = null;
		
		switch (format) {
		case EDGE_LIST:
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.equals("-1")) {
					break;
				} else {
					String[] tokens = line.split("\\s+");
					int id1 = Integer.parseInt(tokens[0]);
					int id2 = Integer.parseInt(tokens[1]);
					addEdge(id1, id2);
				}
			}
			
			break;
		case ADJ_LIST:
			int currentId = -1;
			Queue<Integer> values = new LinkedList<Integer>();
			
			readNextLine(reader, values);
				
			while ((currentId != -1) && (values.peek() != -1)) {
				if (currentId == -1) {
					currentId = values.poll();
				} else {
					int id = values.poll();
					
					if (id == -1) {
						currentId = -1;
					} else {
						addEdge(currentId, id);
					}
				}
				
				if (values.isEmpty()) {
					readNextLine(reader, values);
				}
			}
			
			break;
		default:
			throw new IllegalArgumentException("edge format not supported");
		}
	}
	
	/**
	 * Adds an edge to this graph.
	 * 
	 * @param id1 the identifier of the first node
	 * @param id2 the identifier of the second node
	 * @throws IllegalArgumentException if a node with the specified identifier
	 *         does not exist
	 */
	private void addEdge(int id1, int id2) {
		if ((id1 < 1) || (id1 > size)) {
			throw new IllegalArgumentException("no node with identifier " +
					id1);
		}
		
		if ((id2 < 1) || (id2 > size)) {
			throw new IllegalArgumentException("no node with identifier " +
					id2);
		}
		
		edges.add(new Edge(id1, id2));
	}
	
	/**
	 * Returns the edges contained in this graph.  Changes to the returned
	 * list will be reflected in this graph.
	 * 
	 * @return the edges contained in this graph
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	@Override
	public int[] listNodes() {
		int[] nodes = new int[size];
		
		for (int i = 1; i <= size; i++) {
			nodes[i-1] = i;
		}
		
		return nodes;
	}

	@Override
	public int[] getNeighborsOf(int id) {
		if ((id < 1) || (id > size)) {
			throw new IllegalArgumentException("no node with identifier " + id);
		}
		
		List<Integer> neighbors = new ArrayList<Integer>();
		
		for (Edge edge : edges) {
			if (edge.hasEndpoint(id)) {
				neighbors.add(edge.getOppositeEndpoint(id));
			}
		}
		
		// copy neighbors to an array
		int[] result = new int[neighbors.size()];
		
		for (int i = 0; i < neighbors.size(); i++) {
			result[i] = neighbors.get(i);
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The distance between two nodes is {@code 1} when an edge exists, or 
	 * {@code Double.POSITIVE_INFINITY} when no such edge exists.
	 */
	@Override
	public double getDistanceBetween(int id1, int id2) {
		if (isNeighbor(id1, id2)) {
			return 1.0;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

}
