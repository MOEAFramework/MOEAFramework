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
package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;

/**
 * Uniform selection operator. Solutions are selected from the population with
 * equal probability.
 */
public class UniformSelection implements Selection {

	/**
	 * Constructs a uniform selection operator.
	 */
	public UniformSelection() {
		super();
	}

	@Override
	public Solution[] select(int arity, Population population) {
		Solution[] result = new Solution[arity];

		for (int i = 0; i < arity; i++) {
			result[i] = population.get(PRNG.nextInt(population.size()));
		}

		return result;
	}

}
