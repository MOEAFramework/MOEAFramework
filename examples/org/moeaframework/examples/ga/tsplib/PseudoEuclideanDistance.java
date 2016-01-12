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
 * The psuedo-Euclidean distance function used by the {@code ATT} TSPLIB problem
 * instances.
 */
public class PseudoEuclideanDistance extends DistanceFunction {
	
	/**
	 * Constructs a new pseudo-Euclidean distance function.
	 */
	public PseudoEuclideanDistance() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException if the nodes are not two dimensional
	 */
	@Override
	public double distance(int length, double[] position1, double[] position2) {
		if (length != 2) {
			throw new IllegalArgumentException("nodes must be 2D");
		}
		
		double xd = position1[0] - position2[0];
		double yd = position1[1] - position2[1];
		double r = Math.sqrt((Math.pow(xd, 2.0) + Math.pow(yd, 2.0)) / 10.0);
		double t = Math.round(r);

		if (t < r) {
			return t + 1.0;
		} else {
			return t;
		}
	}

}
