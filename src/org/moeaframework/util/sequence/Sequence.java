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
package org.moeaframework.util.sequence;

/**
 * Interface for generating a sequence of real numbers. The nature of the
 * sequence is specific to the implementation; generated sequences may be
 * deterministic or stochastic, uniform or non-uniform, etc. Refer to the
 * implementing class' documentation for details.
 */
public interface Sequence {

	/**
	 * Returns a {@code N x D} matrix of real numbers in the range {@code [0,
	 * 1]}.
	 * 
	 * @param N the number of sample points
	 * @param D the dimension of each sample point
	 * @return a {@code N x D} matrix of real numbers in the range {@code [0,
	 *         1]}
	 */
	public double[][] generate(int N, int D);

}
