/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
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
