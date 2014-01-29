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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores the demand at each node and identifies the depot nodes for vehicle
 * routing problems.
 */
public class VehicleRoutingTable {
	
	/**
	 * The number of nodes in the problem instance.
	 */
	private final int size;
	
	/**
	 * The mapping from node identifiers to the node demand.
	 */
	private final Map<Integer, Integer> demands;
	
	/**
	 * The set of depot nodes.
	 */
	private final Set<Integer> depots;
	
	/**
	 * Constructs a new, empty vehicle routing table.
	 * 
	 * @param size the number of nodes the the problem instance
	 */
	public VehicleRoutingTable(int size) {
		super();
		this.size = size;
		
		demands = new HashMap<Integer, Integer>();
		depots = new HashSet<Integer>();
	}
	
	/**
	 * Loads the demands from the specified reader.
	 * 
	 * @param reader the reader containing the demands
	 * @throws IOException if an I/O error occurred while reading the demands
	 */
	public void loadDemands(BufferedReader reader) throws IOException {
		for (int i = 0; i < size; i++) {
			String line = reader.readLine();
			String[] tokens = line.trim().split("\\s+");
			int id = Integer.parseInt(tokens[0]);
			int demand = Integer.parseInt(tokens[1]);
			
			demands.put(id, demand);
		}
	}
	
	/**
	 * Loads the depot list from the given reader.
	 * 
	 * @param reader the reader that defines the depot nodes
	 * @throws IOException if an I/O error occurred while reading the depot
	 *         list
	 */
	public void loadDepots(BufferedReader reader) throws IOException {
		String line = null;
		
		outer: while ((line = reader.readLine()) != null) {
			String[] tokens = line.trim().split("\\s+");
			
			for (int i = 0; i < tokens.length; i++) {
				int id = Integer.parseInt(tokens[i]);
				
				if (id == -1) {
					break outer;
				} else {
					depots.add(id);
				}
			}
		}
	}
	
	/**
	 * Returns the demand at the specified node.
	 * 
	 * @param id the identifier of the node
	 * @return the demand at the specified node
	 * @throws IllegalArgumentException if a node with the specified identifier
	 *         does not exist
	 */
	public int getDemand(int id) {
		if ((id < 1) || (id > size)) {
			throw new IllegalArgumentException("no node with identifier " + id);
		}
		
		if (demands.containsKey(id)) {
			return demands.get(id);
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns {@code true} if this is a depot node; {@code false} otherwise.
	 * 
	 * @param id the identifier of the node
	 * @return {@code true} if this is a depot node; {@code false} otherwise
	 * @throws IllegalArgumentException if a node with the specified identifier
	 *         does not exist
	 */
	public boolean isDepot(int id) {
		if ((id < 1) || (id > size)) {
			throw new IllegalArgumentException("no node with identifier " + id);
		}
		
		return depots.contains(id);
	}

}
