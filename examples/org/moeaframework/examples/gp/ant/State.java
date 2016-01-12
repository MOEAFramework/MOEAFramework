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

/**
 * The state of each cell in the world.
 */
public enum State {

	/**
	 * The cell contains food.
	 */
	FOOD,
	
	/**
	 * The cell is empty.
	 */
	EMPTY,
	
	/**
	 * The cell is empty but located on the "ideal" ant trail.  This must NOT
	 * be used to influence the ant's behavior, and should be treated like an
	 * EMPTY cell.
	 */
	TRAIL,
	
	/**
	 * The cell previously contained food, but the ant reached this location
	 * and ate the food.
	 */
	EATEN

}
