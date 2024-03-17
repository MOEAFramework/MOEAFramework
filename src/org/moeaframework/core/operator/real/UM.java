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
package org.moeaframework.core.operator.real;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafeMutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Uniform mutation (UM) operator.  Each decision variable is mutated by selecting a new value within its bounds
 * uniformly at random.  The figure below depicts the offspring distribution.
 * <p>
 * <img src="doc-files/UM-1.png" alt="Example UM operator distribution" />
 * <p>
 * It is recommended each decision variable is mutated with a probability of {@code 1 / L}, where {@code L} is the
 * number of decision variables.  This results in one mutation per offspring on average.
 * <p>
 * This operator is type-safe.
 */
@Prefix("um")
public class UM extends TypeSafeMutation<RealVariable> {
	
	/**
	 * Constructs a uniform mutation operator with default settings.
	 */
	public UM() {
		this(0.1);
	}

	/**
	 * Constructs a uniform mutation operator.
	 * 
	 * @param probability the probability of mutating each variable in a solution
	 */
	public UM(double probability) {
		super(RealVariable.class, probability);
	}
	
	@Override
	public String getName() {
		return "um";
	}

	/**
	 * Mutates the specified variable using uniform mutation.
	 * 
	 * @param variable the variable to be mutated
	 */
	public void mutate(RealVariable variable) {
		variable.setValue(PRNG.nextDouble(variable.getLowerBound(), variable.getUpperBound()));
	}

}
