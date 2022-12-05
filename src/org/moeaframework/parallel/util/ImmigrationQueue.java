/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.parallel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.moeaframework.core.Solution;

/**
 * A queue for tracking migrating solutions that is (1) thread-safe, and (2)
 * automatically creates copies of the solutions to avoid issues in shared-memory
 * environments.
 */
public class ImmigrationQueue {
	
	/**
	 * The underlying thread-safe queue.
	 */
	private final Queue<Solution> queue;
	
	/**
	 * Creates a new, empty immigration queue.
	 */
	public ImmigrationQueue() {
		super();
		queue = new ConcurrentLinkedQueue<Solution>();
	}
	
	/**
	 * Adds a copy of the solution to this immigration queue.
	 * 
	 * @param solution the solution to add
	 */
	public void add(Solution solution) {
		queue.add(solution.copy());
	}
	
	/**
	 * Adds a copy of all solutions to this immigration queue.
	 * 
	 * @param solutions the solutions to add
	 */
	public void addAll(Solution[] solutions) {
		for (Solution solution : solutions) {
			queue.add(solution.copy());
		}
	}
	
	/**
	 * Adds a copy of all solutions to this immigration queue.
	 * 
	 * @param solutions the solutions to add
	 */
	public void addAll(Collection<? extends Solution> solutions) {
		for (Solution solution : solutions) {
			queue.add(solution.copy());
		}
	}
	
	/**
	 * Removes and returns one solution from this immigration queue.
	 * 
	 * @return the solution that was removed
	 */
	public Solution pop() {
		return queue.remove();
	}
	
	/**
	 * Removes and returns all solutions currently in the immigration queue.
	 * 
	 * @return the solutions that were removed
	 */
	public List<Solution> popAll() {
		List<Solution> result = new ArrayList<Solution>();
		
		while (!queue.isEmpty()) {
			result.add(queue.remove());
		}
		
		return result;
	}
	
	/**
	 * Returns {@code true} if this immigration queue is empty.
	 * 
	 * @return {@code true} if empty; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

}
