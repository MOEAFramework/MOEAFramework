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
 * Enumeration of the ways node coordinates can be specified.
 */
public enum NodeCoordType {
	
	/**
	 * Nodes are specified by coordinates in 2-D.
	 */
	TWOD_COORDS(2),
	
	/**
	 * Nodes are specified by coordinates in 3-D.
	 */
	THREED_COORDS(3),
	
	/**
	 * Nodes do not have associated coordinates.
	 */
	NO_COORDS(-1);
	
	/**
	 * The length (dimension) of the coordinates.
	 */
	private final int length;
	
	/**
	 * Constructs a new node coordinate enumeration.
	 * 
	 * @param length the length (dimension) of the coordinates
	 */
	private NodeCoordType(int length) {
		this.length = length;
	}
	
	/**
	 * Returns the length (dimension) of the coordinates.
	 * 
	 * @return the length (dimension) of the coordinates
	 */
	public int getLength() {
		return length;
	}

}
