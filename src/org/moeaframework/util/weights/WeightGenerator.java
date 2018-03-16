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
package org.moeaframework.util.weights;

import java.util.List;

/**
 * Interface for generating a sequence of weights.  The nature of the weights
 * depends on the specific implementation.  The only requirement is that the
 * sum of each component in a weight vector be 1.
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
	 * @return the genreated weights
	 */
	public List<double[]> generate();

}
