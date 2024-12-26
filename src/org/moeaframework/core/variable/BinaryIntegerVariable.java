/* Copyright 2009-2025 David Hadka
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

import java.util.BitSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.util.validate.Validate;

/**
 * Decision variable for integers encoded as a binary string.  Note that if {@code upperBound-lowerBound} is not a
 * power of 2, then some values will occur more frequently after a variation operator.
 */
public class BinaryIntegerVariable extends BinaryVariable {

	private static final long serialVersionUID = 5045946885389529638L;

	/**
	 * The lower bound of this decision variable.
	 */
	private final int lowerBound;
	
	/**
	 * The upper bound of this decision variable.
	 */
	private final int upperBound;
	
	/**
	 * If {@code true}, the binary representation uses gray coding.  Gray coding ensures that two successive values
	 * differ in only one bit.
	 */
	private final boolean gray;
	
	/**
	 * Constructs an integer-valued variable in the range {@code lowerBound <= x <= upperBound} with an uninitialized
	 * value.  Uses gray coding by default.
	 * 
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 */
	public BinaryIntegerVariable(int lowerBound, int upperBound) {
		this(lowerBound, upperBound, true);
	}
	
	/**
	 * Constructs an integer-valued variable in the range {@code lowerBound <= x <= upperBound} with the specified
	 * initial value.  Uses gray coding by default.
	 * 
	 * @param value the initial value of this decision variable
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < lowerBound) || (value > upperBound)}
	 */
	public BinaryIntegerVariable(int value, int lowerBound, int upperBound) {
		this(value, lowerBound, upperBound, true);
	}
	
	/**
	 * Constructs an integer-valued variable in the range {@code lowerBound <= x <= upperBound} with an uninitialized
	 * value.
	 * 
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @param gray if the binary representation uses gray coding
	 */
	public BinaryIntegerVariable(int lowerBound, int upperBound, boolean gray) {
		super(getNumberOfBits(lowerBound, upperBound));
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.gray = gray;
	}
	
	/**
	 * Constructs an integer-valued variable in the range {@code lowerBound <= x <= upperBound} with the specified
	 * initial value.  Uses gray coding by default.
	 * 
	 * @param value the initial value of this decision variable
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @param gray if the binary representation uses gray coding
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < lowerBound) || (value > upperBound)}
	 */
	public BinaryIntegerVariable(int value, int lowerBound, int upperBound, boolean gray) {
		this(lowerBound, upperBound, gray);
		setValue(value);
	}
	
	/**
	 * Returns the minimum number of bits required to represent an integer within the given bounds.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @return the minimum number of bits required to represent an integer within the given bounds
	 */
	public static final int getNumberOfBits(int lowerBound, int upperBound) {
		return Integer.SIZE-Integer.numberOfLeadingZeros(upperBound-lowerBound);
	}
	
	/**
	 * Returns the current value of this decision variable.
	 * 
	 * @return the current value of this decision variable
	 */
	public int getValue() {
		BitSet bits = getBitSet();
		
		if (gray) {
			bits = grayToBinary(bits);
		}
		
		int value = (int)decode(bits);
		
		// if difference is not a power of 2, then the decoded value may be larger than the difference
		if (value > upperBound - lowerBound) {
			value -= upperBound - lowerBound;
		}
		
		return lowerBound + value;
	}
	
	/**
	 * Sets the value of this decision variable.
	 * 
	 * @param value the new value for this decision variable
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < getLowerBound()) || (value > getUpperBound())}
	 */
	public void setValue(int value) {
		Validate.that("value", value).isBetween(lowerBound, upperBound);
		BitSet bits = encode(value - lowerBound);

		if (gray) {
			bits = binaryToGray(bits);
		}
		
		for (int i = 0; i < getNumberOfBits(); i++) {
			set(i, bits.get(i));
		}
	}
	
	/**
	 * Returns {@code true} if the binary representation using gray coding.  Gray coding ensures that two successive
	 * values differ by only one bit.
	 * 
	 * @return {@code true} if the binary representation using gray coding; {@code false} otherwise
	 */
	protected boolean isGray() {
		return gray;
	}

	/**
	 * Returns the lower bound of this decision variable.
	 * 
	 * @return the lower bound of this decision variable, inclusive
	 */
	public int getLowerBound() {
		return lowerBound;
	}
	
	/**
	 * Returns the upper bound of this decision variable.
	 * 
	 * @return the upper bound of this decision variable, inclusive
	 */
	public int getUpperBound() {
		return upperBound;
	}
	
	@Override
	public BinaryIntegerVariable copy() {
		BinaryIntegerVariable result = new BinaryIntegerVariable(lowerBound, upperBound);
	
		// copy the bits instead of the value to ensure the clone has the same internal representation
		for (int i = 0; i < result.getNumberOfBits(); i++) {
			result.set(i, get(i));
		}
		
		return result;
	}
	
	@Override
	public String getDefinition() {
		return Constructable.createDefinition(Variable.class, BinaryIntegerVariable.class, lowerBound, upperBound);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(lowerBound)
				.append(upperBound)
				.append(getValue())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			BinaryIntegerVariable rhs = (BinaryIntegerVariable)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(lowerBound, rhs.lowerBound)
					.append(upperBound, rhs.upperBound)
					.append(getValue(), rhs.getValue())
					.isEquals();
		}
	}
	
	@Override
	public String toString() {
		return Integer.toString(getValue());
	}

	@Override
	public void randomize() {
		setValue(PRNG.nextInt(lowerBound, upperBound));
	}
	
	/**
	 * Returns the value stored in an integer-valued decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in an integer-valued decision variable
	 * @throws IllegalArgumentException if the decision variable is not convertible to an integer
	 */
	public static int getInt(Variable variable) {
		BinaryIntegerVariable integer = Validate.that("variable", variable).isA(BinaryIntegerVariable.class);
		return integer.getValue();
	}
	
	/**
	 * Returns the array of integer-valued decision variables stored in a solution.  The solution must contain only
	 * integer-valued decision variables.
	 * 
	 * @param solution the solution
	 * @return the array of integer-valued decision variables stored in a solution
	 * @throws IllegalArgumentException if any decision variable contained in the solution is not of type
	 *         {@link RealVariable}
	 */
	public static int[] getInt(Solution solution) {
		return getInt(solution, 0, solution.getNumberOfVariables());
	}
	
	/**
	 * Returns the array of integer-valued decision variables stored in a solution between the specified indices.
	 * The decision variables located between the start and end index must all be integer-valued decision variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @return the array of integer-valued decision variables stored in a solution between the specified indices
	 * @throws IllegalArgumentException if any decision variable contained in the solution between the start and end
	 *         index is not of type {@link RealVariable}
	 */
	public static int[] getInt(Solution solution, int startIndex, int endIndex) {
		int[] result = new int[endIndex - startIndex];
		
		for (int i=startIndex; i<endIndex; i++) {
			result[i-startIndex] = getInt(solution.getVariable(i));
		}
		
		return result;
	}
	
	/**
	 * Sets the value of an integer-valued decision variable.
	 * 
	 * @param variable the decision variable
	 * @param value the value to assign the integer-valued decision variable
	 * @throws IllegalArgumentException if the decision variable is not convertible to an integer
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Variable variable, int value) {
		BinaryIntegerVariable integer = Validate.that("variable", variable).isA(BinaryIntegerVariable.class);
		integer.setValue(value);
	}
	
	/**
	 * Sets the values of all integer-valued decision variables stored in the solution.  The solution must contain
	 * only integer-valued decision variables.
	 * 
	 * @param solution the solution
	 * @param values the array of integer values to assign the solution
	 * @throws IllegalArgumentException if the decision variable is not convertible to an integer
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Solution solution, int[] values) {
		setInt(solution, 0, solution.getNumberOfVariables(), values);
	}
	
	/**
	 * Sets the values of the integer-valued decision variables stored in a solution between the specified indices.
	 * The decision variables located between the start and end index must all be integer-valued decision variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @param values the array of floating-point values to assign the decision variables
	 * @throws IllegalArgumentException if the decision variables are not convertible to an integer
	 * @throws IllegalArgumentException if an invalid number of values are provided
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Solution solution, int startIndex, int endIndex, int[] values) {
		if (values.length != (endIndex - startIndex)) {
			Validate.that("values", values).fails("The start / end index and array length are not compatible.");
		}
		
		for (int i=startIndex; i<endIndex; i++) {
			setInt(solution.getVariable(i), values[i-startIndex]);
		}
	}
	
	/**
	 * Converts a binary string from binary code to gray code.  The gray code ensures two adjacent values have binary
	 * representations differing in only one big (i.e., a Hamming distance of {@code 1}).
	 * 
	 * @param binary the binary code string
	 * @return the gray code string
	 */
	static BitSet binaryToGray(BitSet binary) {
		int n = binary.length();
		BitSet gray = new BitSet();

		if (n > 0) {
			gray.set(n - 1, binary.get(n - 1));
			for (int i = n - 2; i >= 0; i--) {
				gray.set(i, binary.get(i + 1) ^ binary.get(i));
			}
		}
		
		return gray;
	}

	/**
	 * Converts a binary string from gray code to binary code.
	 * 
	 * @param gray the gray code string
	 * @return the binary code string
	 */
	static BitSet grayToBinary(BitSet gray) {
		int n = gray.length();
		BitSet binary = new BitSet();

		if (n > 0) {
			binary.set(n - 1, gray.get(n - 1));
			for (int i = n - 2; i >= 0; i--) {
				binary.set(i, binary.get(i + 1) ^ gray.get(i));
			}
		}
		
		return binary;
	}
	
	/**
	 * Converts the given long value into its binary string representation.
	 * 
	 * @param value the long value
	 * @return the binary string representation
	 */
	static BitSet encode(long value) {
		BitSet binary = new BitSet();

		for (int i = 0; i < 64; i++) {
			binary.set(i, (value & (1L << i)) != 0);
		}
		
		return binary;
	}
	
	/**
	 * Converts the given binary string into its long value.
	 * 
	 * @param binary the binary string
	 * @return the long value
	 */
	static long decode(BitSet binary) {
		int numberOfBits = binary.length();

		if (numberOfBits > 64) {
			Validate.that("binary", binary).fails("Number of bits exceeds 64, the maximum size of a long.");
		}

		long value = 0;

		for (int i = 0; i < numberOfBits; i++) {
			if (binary.get(i)) {
				value |= (1L << i);
			}
		}

		return value;
	}

}
