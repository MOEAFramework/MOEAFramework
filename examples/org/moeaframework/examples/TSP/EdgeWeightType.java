/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.examples.TSP;

/**
 * Enumeration of the ways that explicit edge weights (distances) can be specified.
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
		return switch (this) {
			case EUC_2D, EUC_3D -> new EuclideanDistance();
			case MAX_2D, MAX_3D -> new MaximumDistance();
			case MAN_2D, MAN_3D -> new ManhattanDistance();
			case CEIL_2D -> new CeilingDistance();
			case GEO -> new GeographicalDistance();
			case ATT -> new PseudoEuclideanDistance();
			default -> throw new IllegalArgumentException("No distance function defined for " + this);
		};
	}
	
	public NodeCoordType getNodeCoordType() {
		return switch (this) {
			case EUC_2D, MAX_2D, MAN_2D, CEIL_2D, GEO, ATT -> NodeCoordType.TWOD_COORDS;
			case EUC_3D, MAX_3D, MAN_3D -> NodeCoordType.THREED_COORDS;
			default -> throw new IllegalArgumentException("No node coordinate type defined for " + this);
		};
	}

}
