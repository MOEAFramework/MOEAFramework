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
