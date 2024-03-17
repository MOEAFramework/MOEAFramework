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
package org.moeaframework.core.operator.binary;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafeCrossover;
import org.moeaframework.core.variable.BinaryVariable;

/**
 * Half-uniform crossover (HUX) operator. Half of the non-matching bits are swapped between the two parents.
 * <p>
 * This variation operator is type-safe.
 */
@Prefix("hux")
public class HUX extends TypeSafeCrossover<BinaryVariable> {

	/**
	 * Constructs a HUX operator with a 100% chance of applying this operator to each solution.
	 */
	public HUX() {
		this(1.0);
	}

	/**
	 * Constructs a HUX operator.
	 * 
	 * @param probability the probability of applying this operator to each solution
	 */
	public HUX(double probability) {
		super(BinaryVariable.class, probability);
	}
	
	@Override
	public String getName() {
		return "hux";
	}

	/**
	 * Evolves the specified variables using the HUX operator.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 */
	public void evolve(BinaryVariable v1, BinaryVariable v2) {
		if (v1.getNumberOfBits() != v2.getNumberOfBits()) {
			throw new FrameworkException("binary variables not same length");
		}

		for (int i = 0; i < v1.getNumberOfBits(); i++) {
			boolean value = v1.get(i);

			if ((value != v2.get(i)) && PRNG.nextBoolean()) {
				v1.set(i, !value);
				v2.set(i, value);
			}
		}
	}

}
