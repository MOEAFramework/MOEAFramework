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
 * Enumeration of the ways that explicit edge weights (distances) can be
 * specified.
 */
public enum EdgeWeightType {
	
	/**
	 * Weights are listed explicitly.
	 */
	EXPLICIT,
	
	/**
	 * Weights are Euclidean distances in 2-D.
	 */
	EUC_2D,
	
	/**
	 * Weights are Euclidean distances in 3-D.
	 */
	EUC_3D,
	
	/**
	 * Weights are maximum distances in 2-D.
	 */
	MAX_2D,
	
	/**
	 * Weights are maximum distances in 3-D.
	 */
	MAX_3D,
	
	/**
	 * Weights are Manhattan distances in 2-D.
	 */
	MAN_2D,
	
	/**
	 * Weights are Manhattan distances in 3-D.
	 */
	MAN_3D,
	
	/**
	 * Weights are Euclidean distances in 2-D rounded up.
	 */
	CEIL_2D,
	
	/**
	 * Weights are geographical distances.
	 */
	GEO,
	
	/**
	 * Special distance function for problems att48 and att532.
	 */
	ATT,
	
	/**
	 * Special distance function for crystallography problems.
	 */
	XRAY1,
	
	/**
	 * Special distance function for crystallography problems.
	 */
	XRAY2,
	
	/**
	 * Special distance function documented elsewhere.
	 */
	SPECIAL;
	
	public DistanceFunction getDistanceFunction() {
		switch (this) {
		case EUC_2D:
		case EUC_3D:
			return new EuclideanDistance();
		case MAX_2D:
		case MAX_3D:
			return new MaximumDistance();
		case MAN_2D:
		case MAN_3D:
			return new ManhattanDistance();
		case CEIL_2D:
			return new CeilingDistance();
		case GEO:
			return new GeographicalDistance();
		case ATT:
			return new PseudoEuclideanDistance();
		default:
			throw new IllegalArgumentException(
					"no distance function defined for " + this);
		}
	}
	
	public NodeCoordType getNodeCoordType() {
		switch (this) {
		case EUC_2D:
		case MAX_2D:
		case MAN_2D:
		case CEIL_2D:
		case GEO:
		case ATT:
			return NodeCoordType.TWOD_COORDS;
		case EUC_3D:
		case MAX_3D:
		case MAN_3D:
			return NodeCoordType.THREED_COORDS;
		default:
			throw new IllegalArgumentException(
					"no node coordinate type defined for " + this);
		}
	}

}
