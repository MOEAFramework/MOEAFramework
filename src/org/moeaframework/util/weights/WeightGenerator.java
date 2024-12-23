/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.util.weights;

import java.util.List;

/**
 * Interface for generating a sequence of weights.  A weight generator must satisfy these conditions:
 * <ol>
 *   <li>Each individual weight is {@code 0.0 <= w[i] <= 1.0}.
 *   <li>The sum of all weights must be exactly {@code 1.0}.
 * </ol>
 */
public interface WeightGenerator {
	
	/**
	 * Returns the number of weights that will be generated.
	 * 
	 * @return the number of weights that will be generated
	 */
	public int size();
	
	/**
	 * Returns the generated weights.
	 * 
	 * @return the generated weights
	 */
	public List<double[]> generate();

}
