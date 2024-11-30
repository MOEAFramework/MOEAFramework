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
package org.moeaframework.core.variable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.util.validate.Validate;

/**
 * Decision variable for real values.
 */
public class RealVariable extends AbstractVariable {

	private static final long serialVersionUID = 3141851312155686224L;
	
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
	 * Constructs a real variable in the range {@code lowerBound <= x <= upperBound} with an uninitialized value.
	 * 
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 */
	public RealVariable(double lowerBound, double upperBound) {
		this(Double.NaN, lowerBound, upperBound);
	}

	/**
	 * Constructs a real variable in the range {@code lowerBound <= x <= upperBound} with the specified initial value.
	 * 
	 * @param value the initial value of this decision variable
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < lowerBound) || (value > upperBound)}
	 */
	public RealVariable(double value, double lowerBound, double upperBound) {
		super();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		
		setValue(value);
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
	 * Sets the value of this decision variable.  The value can be set to {@value Double#NaN} to indicate no value
	 * is assigned.
	 * 
	 * @param value the new value for this decision variable
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < getLowerBound()) || (value > getUpperBound())}
	 */
	public void setValue(double value) {
		if (!Double.isNaN(value)) {
			Validate.that("value", value).isBetween(lowerBound, upperBound);
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
	public String getDefinition() {
		return Constructable.createDefinition(Variable.class, RealVariable.class, lowerBound, upperBound);
	}

	@Override
	public String toString() {
		return encode();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
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
					.appendSuper(super.equals(obj))
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
	
	@Override
	public String encode() {
		return Double.toString(value);
	}
	
	@Override
	public void decode(String value) {
		this.value = Double.parseDouble(value);
	}
	
	/**
	 * Returns the value stored in a floating-point decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a floating-point decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link RealVariable}
	 */
	public static double getReal(Variable variable) {
		RealVariable realVariable = Validate.that("variable", variable).isA(RealVariable.class);
		return realVariable.getValue();
	}
	
	/**
	 * Returns the array of floating-point decision variables stored in a solution.  The solution must contain only
	 * floating-point decision variables.
	 * 
	 * @param solution the solution
	 * @return the array of floating-point decision variables stored in a solution
	 * @throws IllegalArgumentException if any decision variable contained in the solution is not of type
	 *         {@link RealVariable}
	 */
	public static double[] getReal(Solution solution) {
		return getReal(solution, 0, solution.getNumberOfVariables());
	}
	
	/**
	 * Returns the array of floating-point decision variables stored in a solution between the specified indices.
	 * The decision variables located between the start and end index must all be floating-point decision variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @return the array of floating-point decision variables stored in a solution between the specified indices
	 * @throws IllegalArgumentException if any decision variable contained in the solution between the start and end
	 *         index is not of type {@link RealVariable}
	 */
	public static double[] getReal(Solution solution, int startIndex, int endIndex) {
		double[] result = new double[endIndex - startIndex];
		
		for (int i=startIndex; i<endIndex; i++) {
			result[i-startIndex] = getReal(solution.getVariable(i));
		}
		
		return result;
	}
	
	/**
	 * Sets the value of a floating-point decision variable.
	 * 
	 * @param variable the decision variable
	 * @param value the value to assign the floating-point decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link RealVariable}
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Variable variable, double value) {
		RealVariable realVariable = Validate.that("variable", variable).isA(RealVariable.class);
		realVariable.setValue(value);
	}
	
	/**
	 * Sets the values of all floating-point decision variables stored in the solution.  The solution must contain
	 * only floating-point decision variables.
	 * 
	 * @param solution the solution
	 * @param values the array of floating-point values to assign the solution
	 * @throws IllegalArgumentException if any decision variable contained in the solution is not of type
	 *         {@link RealVariable}
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Solution solution, double[] values) {
		setReal(solution, 0, solution.getNumberOfVariables(), values);
	}
	
	/**
	 * Sets the values of the floating-point decision variables stored in a solution between the specified indices.
	 * The decision variables located between the start and end index must all be floating-point decision variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @param values the array of floating-point values to assign the decision variables
	 * @throws IllegalArgumentException if any decision variable contained in the solution between the start and end
	 *         index is not of type {@link RealVariable}
	 * @throws IllegalArgumentException if an invalid number of values are provided
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Solution solution, int startIndex, int endIndex, double[] values) {
		if (values.length != (endIndex - startIndex)) {
			Validate.that("values", values).fails("The start / end index and array length are not compatible.");
		}
		
		for (int i=startIndex; i<endIndex; i++) {
			setReal(solution.getVariable(i), values[i-startIndex]);
		}
	}

}
