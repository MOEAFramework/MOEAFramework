/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.parallel.island.executor;

import java.util.concurrent.Executors;

import org.moeaframework.parallel.island.IslandModel;

/**
 * Executes an island model locally using multiple threads.
 */
public class ThreadedIslandExecutor extends BasicIslandExecutor {
	
	/**
	 * Constructs a new executor that runs each island on a separate thread.
	 * 
	 * @param model the island model
	 */
	public ThreadedIslandExecutor(IslandModel model) {
		super(model, Executors.newFixedThreadPool(model.getIslands().size()));
	}

}
