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
package org.moeaframework.examples.gp.ant;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

/**
 * The node for determining if food is located in the position directly ahead
 * of the ant.
 * 
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Name</th>
 *     <th width="25%" align="left">Type</th>
 *     <th width="50%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Boolean</td>
 *     <td>{@code true} if food is located in the position directly ahead of
 *         the ant; {@code false} otherwise</td>
 *   </tr>
 * </table>
 */
public class IsFoodAhead extends Node {
	
	/**
	 * Constructs a new node for determining if food is located in the position
	 * directly ahead of the ant.
	 */
	public IsFoodAhead() {
		super(Boolean.class);
	}

	@Override
	public IsFoodAhead copyNode() {
		return new IsFoodAhead();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		return world.isFoodAhead();
	}

}
