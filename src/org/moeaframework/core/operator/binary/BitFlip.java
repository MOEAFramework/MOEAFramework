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

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.BinaryVariable;

/**
 * Bit flip mutation operator.  Each bit is flipped (switched from a {@code 0} to a {@code 1}, or vice versa) using
 * the specified probability.
 * <p>
 * This operator is type-safe.
 */
@Prefix("bf")
public class BitFlip implements Mutation {

	/**
	 * The probability of flipping a bit.
	 */
	private double probability;
	
	/**
	 * Constructs a bit flip operator with the default settings.
	 */
	public BitFlip() {
		this(0.01);
	}

	/**
	 * Constructs a bit flip operator.
	 * 
	 * @param probability the probability of flipping a bit
	 */
	public BitFlip(double probability) {
		super();
		setProbability(probability);
	}
	
	@Override
	public String getName() {
		return "bf";
	}

	/**
	 * Returns the probability of flipping a bit.
	 * 
	 * @return the probability of flipping a bit
	 */
	public double getProbability() {
		return probability;
	}
	
	/**
	 * Sets the probability of flipping a bit.
	 * 
	 * @param probability the probability of flipping a bit
	 */
	@Property("rate")
	public void setProbability(double probability) {
		Validate.probability("probability", probability);
		this.probability = probability;
	}

	@Override
	public Solution mutate(Solution parent) {
		Solution result = parent.copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if (variable instanceof BinaryVariable binaryVariable) {
				mutate(binaryVariable, probability);
			}
		}

		return result;
	}

	/**
	 * Mutates the specified variable using bit flip mutation.
	 * 
	 * @param variable the variable to be mutated
	 * @param probability the probability of flipping a bit
	 */
	public static void mutate(BinaryVariable variable, double probability) {
		for (int i = 0; i < variable.getNumberOfBits(); i++) {
			if (PRNG.nextDouble() <= probability) {
				variable.set(i, !variable.get(i));
			}
		}
	}

}
