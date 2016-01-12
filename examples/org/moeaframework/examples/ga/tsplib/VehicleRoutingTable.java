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
