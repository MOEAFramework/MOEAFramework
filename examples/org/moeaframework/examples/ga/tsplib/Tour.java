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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the nodes (by their identifier) that are visited in a tour.  Tours
 * are cyclic, so an implicit edge exists between the last index and the first.
 */
public class Tour {
	
	/**
	 * The nodes that are visited in this tour.
	 */
	private final List<Integer> nodes;
	
	/**
	 * Constructs a new, empty tour.
	 */
	public Tour() {
		super();
		
		nodes = new ArrayList<Integer>();
	}
	
	/**
	 * Loads the contents of this tour from the given reader.
	 * 
	 * @param reader the reader that defines this tour
	 * @throws IOException if an I/O error occurred while reading the tour
	 */
	public void load(BufferedReader reader) throws IOException {
		String line = null;
		
		outer: while ((line = reader.readLine()) != null) {
			String[] tokens = line.trim().split("\\s+");
			
			for (int i = 0; i < tokens.length; i++) {
				int id = Integer.parseInt(tokens[i]);
				
				if (id == -1) {
					break outer;
				} else {
					nodes.add(id);
				}
			}
		}
	}
	
	/**
	 * The number of nodes visited in this tour.
	 * 
	 * @return the number of nodes visited in this tour
	 */
	public int size() {
		return nodes.size();
	}
	
	/**
	 * Returns the identifier of the node visited at the specified index.
	 * Tours are cyclic, so specifying an index {@code < 0} or {@code >= size()}
	 * is cycled through the tour, never causing an out-of-bounds exception.
	 * 
	 * @param index the index
	 * @return the identifier of the node visited at the specified index
	 */
	public int get(int index) {
		while (index < 0) {
			index += nodes.size();
		}
		
		return nodes.get(index % nodes.size());
	}
	
	/**
	 * Sets the identifier of the node visited at the specified index.  Tours
	 * are cyclic, so setting an index {@code < 0} or {@code >= size()} is
	 * cycled through the tour, never causing an out-of-bounds exception.
	 * 
	 * @param index the index
	 * @param node the identifier of the node visited at the specified index
	 */
	private void set(int index, int node) {
		while (index < 0) {
			index += nodes.size();
		}
		
		nodes.set(index % nodes.size(), node);
	}
	
	/**
	 * Returns the edges belonging to this tour.
	 * 
	 * @return the edges belonging to this tour
	 */
	public List<Edge> toEdges() {
		List<Edge> result = new ArrayList<Edge>();
		
		for (int i = 0; i < nodes.size(); i++) {
			result.add(new Edge(get(i), get(i+1)));
		}
		
		return result;
	}
	
	/**
	 * Returns this tour as an array of integers.
	 * 
	 * @return this tour as an array of integers
	 */
	public int[] toArray() {
		int[] result = new int[nodes.size()];
		
		for (int i = 0; i < nodes.size(); i++) {
			result[i] = nodes.get(i);
		}
		
		return result;
	}
	
	/**
	 * Sets this tour equal to the specified array.
	 * 
	 * @param array the array of integers defining a tour
	 */
	public void fromArray(int... array) {
		nodes.clear();
		
		for (int i = 0; i < array.length; i++) {
			nodes.add(array[i]);
		}
	}
	
	/**
	 * Calculates and returns the total distance of this tour.  The total
	 * distance includes the distance from the last node back to the first node
	 * in the tour.
	 * 
	 * @param problem the TSPLIB problem instance this tour is a solution for
	 * @return the total distance of this tour
	 */
	public double distance(TSPInstance problem) {
		DistanceTable distanceTable = problem.getDistanceTable();
		double result = 0.0;
		
		for (int i = 0; i < nodes.size(); i++) {
			result += distanceTable.getDistanceBetween(get(i), get(i+1));
		}
		
		return result;
	}
	
	/**
	 * Returns {@code true} if this tour contains all the fixed edges required
	 * by the TSPLIB problem instances; {@code false} otherwise.
	 * 
	 * @param problem the TSPLIB problem instance this tour is a solution for
	 * @return {@code true} if this tour contains all the fixed edges required
	 *         by the TSPLIB problem instances; {@code false} otherwise
	 */
	public boolean containsFixedEdges(TSPInstance problem) {
		EdgeData fixedEdges = problem.getFixedEdges();
		
		if (fixedEdges == null) {
			return true;
		} else {
			return toEdges().containsAll(fixedEdges.getEdges());
		}
	}
	
	/**
	 * Returns {@code true} if this tour is a Hamiltonian cycle; {@code false}
	 * otherwise.  A Hamiltonian cycle is a path through a graph that visits
	 * every node exactly once.
	 * 
	 * @param problem the TSPLIB problem instance this tour is a solution for
	 * @return {@code true} if this tour is a Hamiltonian cycle; {@code false}
	 *         otherwise
	 */
	public boolean isHamiltonianCycle(TSPInstance problem) {
		DistanceTable distanceTable = problem.getDistanceTable();
		Set<Integer> visited = new HashSet<Integer>();
		
		// scan through nodes to determine if any invalid edges are followed
		for (int i = 0; i < nodes.size(); i++) {
			int id1 = get(i);
			int id2 = get(i+1);
			
			if (visited.contains(id2)) {
				return false;
			} else if (!distanceTable.isNeighbor(id1, id2)) {
				return false;
			} else {
				visited.add(id2);
			}
		}
		
		// determine if all nodes were visited
		for (int id : distanceTable.listNodes()) {
			if (!visited.contains(id)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Reverses the order in which the nodes are visited between the two
	 * specified indices.  The first index defines the start of the
	 * reverse operation; the second index defines the stopping position.
	 * Tours are cyclic, so the reverse operation, when the second index is less
	 * than the first, gets wrapped around the end of the array.  The indices
	 * are inclusive.
	 * 
	 * @param i the first index, or starting index
	 * @param j the second index, or stopping index
	 */
	public void reverse(int i, int j) {
		while (j < i) {
			j += nodes.size();
		}
		
		for (int k = 0; k < (j - i + 1) / 2; k++) {
			int temp = get(i+k);
			set(i+k, get(j-k));
			set(j-k, temp);
		}
	}
	
	/**
	 * Returns {@code true} if this tour is equivalent to the specified tour;
	 * {@code false} otherwise.  Two tours are considered equivalent if they
	 * visit the same nodes in the same order.  This comparison ignores the
	 * direction of the node traversal.
	 * 
	 * @param other the tour that is being compared
	 * @return {@code true} if this tour is equivalent to the specified tour;
	 *         {@code false} otherwise
	 */
	public boolean isEquivalent(Tour other) {
		int size = size();
		
		// two equivalent tours must have the same length
		if (size != other.size()) {
			return false;
		}
		
		// find index of matching node
		int startingIndex = -1;
		
		for (int i = 0; i < size; i++) {
			if (get(0) == other.get(i)) {
				startingIndex = i;
				break;
			}
		}
		
		// if for some reason no matching id was found
		if (startingIndex == -1) {
			return false;
		}
		
		// scan one direction to see if tours are equal
		boolean isEqual = true;
		
		for (int i = 0; i < size; i++) {
			if (get(i) != other.get(startingIndex+i)) {
				isEqual = false;
				break;
			}
		}
		
		// if necessary, scan the other direction to see if tours are equal
		if (!isEqual) {
			isEqual = true;
			
			for (int i = 0; i < size; i++) {
				if (get(i) != other.get(startingIndex-i)) {
					isEqual = false;
					break;
				}
			}
		}
		
		return isEqual;
	}
	
	@Override
	public String toString() {
		return nodes.toString();
	}
	
	/**
	 * Returns the canonical tour with the given length.  The canonical
	 * tour visits the nodes in order, i.e., {@code [1, 2, 3, ..., length]}.
	 * 
	 * @param length the number of nodes in the resulting tour
	 * @return the canonical tour with the given length
	 */
	public static Tour createCanonicalTour(int length) {
		Tour tour = new Tour();
		
		for (int i = 1; i <= length; i++) {
			tour.nodes.add(i);
		}
		
		return tour;
	}
	
	/**
	 * Returns a random tour with the given length.  A random tour will visit
	 * every node exactly once.
	 * 
	 * @param length the number of nodes in the resulting tour
	 * @return the random tour with the given length
	 */
	public static Tour createRandomTour(int length) {
		Tour tour = createCanonicalTour(length);
		Collections.shuffle(tour.nodes);
		return tour;
	}
	
	/**
	 * Constructs a tour from the specified array.  The values in the array
	 * should start at {@code 1}.
	 * 
	 * @param entries the nodes visited in the tour
	 * @return the tour constructed from the specified array
	 */
	public static Tour createTour(int... entries) {
		Tour tour = new Tour();
		tour.fromArray(entries);
		return tour;
	}

}
