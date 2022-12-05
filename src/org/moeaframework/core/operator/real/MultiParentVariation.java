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
package org.moeaframework.core.operator.real;

import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;

/**
 * Abstract class for operators that can take a variable number of parents
 * and produce a variable number of offspring.
 */
public abstract class MultiParentVariation implements Variation {

	/**
	 * The number of parents required by this operator.
	 */
	protected int numberOfParents;

	/**
	 * The number of offspring produced by this operator.
	 */
	protected int numberOfOffspring;

	/**
	 * Creates a new multi-parent variation operator.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this operator
	 */
	public MultiParentVariation(int numberOfParents, int numberOfOffspring) {
		super();
		setNumberOfParents(numberOfParents);
		setNumberOfOffspring(numberOfOffspring);
	}
	

	/**
	 * Returns the number of parents required by this operator.
	 * 
	 * @return the number of parents required by this operator
	 */
	public int getNumberOfParents() {
		return numberOfParents;
	}
	
	/**
	 * Sets the number of parents required by this operator.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 */
	@Property("parents")
	public void setNumberOfParents(int numberOfParents) {
		Validate.greaterThanZero("numberOfParents", numberOfParents);
		this.numberOfParents = numberOfParents;
	}

	/**
	 * Returns the number of offspring produced by this operator.
	 * 
	 * @return the number of offspring produced by this operator
	 */
	public int getNumberOfOffspring() {
		return numberOfOffspring;
	}
	
	/**
	 * Sets the number of offspring produced by this operator.
	 * 
	 * @param numberOfOffspring the number of offspring produced by this operator
	 */
	@Property("offspring")
	public void setNumberOfOffspring(int numberOfOffspring) {
		Validate.greaterThanZero("numberOfOffspring", numberOfOffspring);
		this.numberOfOffspring = numberOfOffspring;
	}
	
	@Override
	public int getArity() {
		return numberOfParents;
	}
	
}
