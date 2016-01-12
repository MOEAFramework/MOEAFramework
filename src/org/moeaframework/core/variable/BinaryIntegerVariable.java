/* Copyright 2009-2016 David Hadka
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

/**
 * Decision variable for integers encoded as a binary string.  Note that if
 * {@code upperBound-lowerBound} is not a power of 2, then some values will
 * occur more frequently after a variation operator.
 */
public class BinaryIntegerVariable extends BinaryVariable {

	private static final long serialVersionUID = 5045946885389529638L;

	private static final String VALUE_OUT_OF_BOUNDS = 
			"value out of bounds (value: {0}, min: {1}, max: {2})";

	/**
	 * The lower bound of this decision variable.
	 */
	private final int lowerBound;
	
	/**
	 * The upper bound of this decision variable.
	 */
	private final int upperBound;
	
	/**
	 * If {@code true}, the binary representation uses gray coding.  Gray
	 * coding ensures that two successive values differ in only one bit.
	 */
	private final boolean gray;
	
	/**
	 * Constructs an integer-valued variable in the range
	 * {@code lowerBound <= x <= upperBound} with an uninitialized value.
	 * Uses gray coding by default.
	 * 
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 */
	public BinaryIntegerVariable(int lowerBound, int upperBound) {
		this(lowerBound, upperBound, true);
	}
	
	/**
	 * Constructs an integer-valued variable in the range
	 * {@code lowerBound <= x <= upperBound} with the specified initial value.
	 * Uses gray coding by default.
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
	 * Constructs an integer-valued variable in the range
	 * {@code lowerBound <= x <= upperBound} with an uninitialized value.
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
	 * Constructs an integer-valued variable in the range
	 * {@code lowerBound <= x <= upperBound} with the specified initial value.
	 * Uses gray coding by default.
	 * 
	 * @param value the initial value of this decision variable
	 * @param lowerBound the lower bound of this decision variable, inclusive
	 * @param upperBound the upper bound of this decision variable, inclusive
	 * @param gray if the binary representation uses gray coding
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         {@code (value < lowerBound) || (value > upperBound)}
	 */
	public BinaryIntegerVariable(int value, int lowerBound, int upperBound,
			boolean gray) {
		this(lowerBound, upperBound, gray);
		setValue(value);
	}
	
	/**
	 * Returns the minimum number of bits required to represent an integer
	 * within the given bounds.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @return the minimum number of bits required to represent an integer
	 *         within the given bounds
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
		if (gray) {
			EncodingUtils.grayToBinary(this);
		}
		
		int value = (int)EncodingUtils.decode(this);
		
		if (gray) {
			EncodingUtils.binaryToGray(this);
		}
		
		// if difference is not a power of 2, then the decoded value may be
		// larger than the difference
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
		if ((value < lowerBound) || (value > upperBound)) {
			throw new IllegalArgumentException(MessageFormat.format(
					VALUE_OUT_OF_BOUNDS, value, lowerBound, upperBound));
		}

		EncodingUtils.encode(value - lowerBound, this);
		
		if (gray) {
			EncodingUtils.binaryToGray(this);
		}
	}
	
	/**
	 * Returns {@code true} if the binary representation using gray coding.
	 * Gray coding ensures that two successive values differ by only one bit.
	 * 
	 * @return {@code true} if the binary representation using gray coding;
	 *         {@code false} otherwise
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
		BinaryIntegerVariable result = new BinaryIntegerVariable(getValue(),
				lowerBound, upperBound);
	
		// ensure the copy has the same internal binary string
		for (int i = 0; i < result.getNumberOfBits(); i++) {
			result.set(i, get(i));
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
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

}
