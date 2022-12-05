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
package org.moeaframework.parallel.island.topology;

import java.util.List;
import org.moeaframework.parallel.island.Island;

/**
 * Defines the topology for an island model.
 */
public interface Topology {

	/**
	 * Returns the neighboring islands for the current island.
	 * 
	 * @param current the current island
	 * @param allIslands the list of all islands
	 * @return the neighboring islands
	 */
	public List<Island> getNeighbors(Island current, List<Island> allIslands);
	
}
