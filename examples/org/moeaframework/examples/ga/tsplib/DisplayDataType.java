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

/**
 * Enumeration of ways a graphical display can be generated from the data.
 */
public enum DisplayDataType {
	
	/**
	 * The display is generated from the node coordinates.
	 */
	COORD_DISPLAY,
	
	/**
	 * Explicit coordinates in 2-D are given.
	 */
	TWOD_DISPLAY,
	
	/**
	 * No graphical display is available.
	 */
	NO_DISPLAY

}
