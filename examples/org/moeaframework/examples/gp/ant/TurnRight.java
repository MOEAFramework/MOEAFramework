/* Copyright 2009-2018 David Hadka
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
 * The node for turning the ant right.  Performing this operation consumes one
 * move.
 */
public class TurnRight extends Node {
	
	/**
	 * Constructs a new node for turning the ant right.
	 */
	public TurnRight() {
		super();
	}

	@Override
	public TurnRight copyNode() {
		return new TurnRight();
	}

	@Override
	public Void evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		world.turnRight();
		return null;
	}

}
