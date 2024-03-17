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
package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;

/**
 * Crossover operator where each index is randomly swapped between the parents with a 50% chance.
 */
@Prefix("ux")
public class UniformCrossover implements Variation {

	/**
	 * The probability of applying this operator to solutions.
	 */
	private double probability;
	
	/**
	 * Constructs a uniform crossover operator with a 100% probability of mutating the parents.
	 */
	public UniformCrossover() {
		this(1.0);
	}

	/**
	 * Constructs a uniform crossover operator with the specified probability of applying this operator to solutions.
	 * 
	 * @param probability the probability of applying this operator to solutions
	 */
	public UniformCrossover(double probability) {
		super();
		setProbability(probability);
	}
	
	@Override
	public String getName() {
		return "ux";
	}

	/**
	 * Returns the probability of applying this operator to solutions
	 * 
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
	
	/**
	 * Sets the probability of applying this operator to solutions.
	 * 
	 * @param probability the probability between 0.0 and 1.0, inclusive
	 */
	@Property("rate")
	public void setProbability(double probability) {
		Validate.probability("probability", probability);
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		if (PRNG.nextDouble() <= probability) {
			for (int i = 0; i < result1.getNumberOfVariables(); i++) {
				if (PRNG.nextBoolean()) {
					Variable temp = result1.getVariable(i);
					result1.setVariable(i, result2.getVariable(i));
					result2.setVariable(i, temp);
				}
			}
		}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}
