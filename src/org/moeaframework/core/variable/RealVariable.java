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
package org.moeaframework.core.variable;

import java.text.MessageFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

/**
 * Decision variable for real values.
 */
public class RealVariable implements Variable {

	private static final long serialVersionUID = 3141851312155686224L;
	
	private static final String VALUE_OUT_OF_BOUNDS = 
		"value out of bounds (value: {0}, min: {1}, max: {2})";

	/**
	 * The current value of this decision variable.
	 */
	private double value;

	/**
	 * The lower bound of this decision variable.
	 */
	private final double lowerBound;

	/**
	 * The upper bound of this decision variable.
	 */
	private final double upperBound;

	/**
	 * Constructs a real variable in the range {@code lowerBound <= x <=
	 * upperBound} with an uninitialized value.
	 * 
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 */
	public RealVariable(double lowerBound, double upperBound) {
		this(Double.NaN, lowerBound, upperBound);
	}

	/**
	 * Constructs a real variable in the range {@code lowerBound <= x <=
	 * upperBound} with the specified initial value.
	 * 
	 * @param value the initial value of this decision variable
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < lowerBound) || (value > upperBound)}
	 */
	public RealVariable(double value, double lowerBound, double upperBound) {
		super();
		this.value = value;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		if ((value < lowerBound) || (value > upperBound)) {
			throw new IllegalArgumentException(MessageFormat.format(
					VALUE_OUT_OF_BOUNDS, value, lowerBound, upperBound));
		}
	}

	/**
	 * Returns the current value of this decision variable.
	 * 
	 * @return the current value of this decision variable
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of this decision variable.
	 * 
	 * @param value the new value for this decision variable
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < getLowerBound()) || (value > getUpperBound())}
	 */
	public void setValue(double value) {
		if ((value < lowerBound) || (value > upperBound)) {
			throw new IllegalArgumentException(MessageFormat.format(
					VALUE_OUT_OF_BOUNDS, value, lowerBound, upperBound));
		}

		this.value = value;
	}

	/**
	 * Returns the lower bound of this decision variable.
	 * 
	 * @return the lower bound of this decision variable, inclusive
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound of this decision variable.
	 * 
	 * @return the upper bound of this decision variable, inclusive
	 */
	public double getUpperBound() {
		return upperBound;
	}

	@Override
	public RealVariable copy() {
		return new RealVariable(value, lowerBound, upperBound);
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(lowerBound)
				.append(upperBound)
				.append(value)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			RealVariable rhs = (RealVariable)obj;
			
			return new EqualsBuilder()
					.append(lowerBound, rhs.lowerBound)
					.append(upperBound, rhs.upperBound)
					.append(value, rhs.value)
					.isEquals();
		}
	}

	@Override
	public void randomize() {
		setValue(PRNG.nextDouble(lowerBound, upperBound));
	}

}
