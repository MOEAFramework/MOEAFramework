/* Copyright 2009-2022 David Hadka
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
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;

/**
 * An abstract variation class that validates the types of each variable before
 * applying the crossover operation with a given probability.
 * 
 * @param <T> the type of decision variable this operator supports
 */
public abstract class TypeSafeCrossover<T extends Variable> implements Variation {
	
	/**
	 * The probability of applying this operator to each decision variable.
	 */
	private double probability;
	
	/**
	 * The type of decision variable this operator supports.
	 */
	private Class<T> type;

	/**
	 * Constructs a new variation operator for the given type.
	 * 
	 * @param type the type of decision variable this operator supports
	 * @param probability the probability of applying this operator to each decision variable
	 */
	public TypeSafeCrossover(Class<T> type, double probability) {
		super();
		this.type = type;
		
		setProbability(probability);
	}
	
	/**
	 * Returns the probability of applying this operator to each decision variable
	 * 
	 * @return the probability between 0.0 and 1.0, inclusive
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the probability of applying this operator to each decision variable.
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
	
		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);
			Variable variable2 = result2.getVariable(i);
	
			if ((PRNG.nextDouble() <= probability) && type.isInstance(variable1) && type.isInstance(variable2)) {
				evolve(type.cast(variable1), type.cast(variable2));
			}
		}
		return new Solution[] { result1, result2 };
	}
	
	@Override
	public int getArity() {
		return 2;
	}
	
	/**
	 * Performs the crossover operation on two decision variables.
	 * 
	 * @param variable1 the first variable
	 * @param variable2 the second variable
	 */
	public abstract void evolve(T variable1, T variable2);

}
