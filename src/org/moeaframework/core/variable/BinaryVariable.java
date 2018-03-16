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

import java.util.BitSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

/**
 * Decision variable for binary strings.
 */
public class BinaryVariable implements Variable {

	private static final long serialVersionUID = -682157453241538355L;

	/**
	 * The number of bits stored in this variable.
	 */
	private final int numberOfBits;

	/**
	 * The internal storage for the bits.
	 */
	private final BitSet bitSet;

	/**
	 * Constructs a binary variable with the specified number of bits. All bits
	 * are initially set to {@code false}.
	 * 
	 * @param numberOfBits the number of bits stored in this variable
	 */
	public BinaryVariable(int numberOfBits) {
		super();
		this.numberOfBits = numberOfBits;

		bitSet = new BitSet(numberOfBits);
	}

	/**
	 * Returns the number of bits stored in this variable.
	 * 
	 * @return the number of bits stored in this variable
	 */
	public int getNumberOfBits() {
		return numberOfBits;
	}

	/**
	 * Returns the number of bits in this variable set to {@code true}.
	 * 
	 * @return the number of bits in this variable set to {@code true}
	 */
	public int cardinality() {
		return bitSet.cardinality();
	}

	/**
	 * Sets all bits in this variable to {@code false}.
	 */
	public void clear() {
		bitSet.clear();
	}

	/**
	 * Returns {@code true} if all bits in this variable are set to
	 * {@code false}; {@code false} otherwise.
	 * 
	 * @return {@code true} if all bits in this variable are set to
	 *         {@code false}; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return bitSet.isEmpty();
	}

	/**
	 * Returns the value of the bit at the specified index.
	 * 
	 * @param index the index of the bit to return
	 * @return the value of the bit at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 *         {@code (index < 0) || (index >= getNumberOfBits())}
	 */
	public boolean get(int index) {
		if ((index < 0) || (index >= numberOfBits)) {
			throw new IndexOutOfBoundsException();
		}

		return bitSet.get(index);
	}

	/**
	 * Sets the value of the bit at the specified index.
	 * 
	 * @param index the index of the bit to set
	 * @param value the new value of the bit being set
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 *         {@code (index < 0) || (index >= getNumberOfBits())}
	 */
	public void set(int index, boolean value) {
		if ((index < 0) || (index >= numberOfBits)) {
			throw new IndexOutOfBoundsException();
		}

		bitSet.set(index, value);
	}

	/**
	 * Returns a {@link BitSet} representing the state of this variable.
	 * 
	 * @return a {@code BitSet} representing the state of this variable
	 */
	public BitSet getBitSet() {
		return (BitSet)bitSet.clone();
	}

	/**
	 * Returns the Hamming distance between this instance and the specified
	 * {@code BinaryVariable}. The Hamming distance is the number of bit
	 * positions in which the two binary strings differ.
	 * 
	 * @param variable the other {@code BinaryVariable}
	 * @return the Hamming distance between this instance and the specified
	 *         {@code BinaryVariable}
	 * @throws IllegalArgumentException if the two binary strings differ in the
	 *         number of bits
	 */
	public int hammingDistance(BinaryVariable variable) {
		if (numberOfBits != variable.numberOfBits) {
			throw new IllegalArgumentException("must have same number of bits");
		}

		int count = 0;

		for (int i = 0; i < numberOfBits; i++) {
			if (bitSet.get(i) != variable.bitSet.get(i)) {
				count++;
			}
		}

		return count;
	}

	@Override
	public BinaryVariable copy() {
		BinaryVariable copy = new BinaryVariable(numberOfBits);
		copy.bitSet.or(bitSet);
		return copy;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(numberOfBits)
				.append(bitSet)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			BinaryVariable rhs = (BinaryVariable)obj;
			
			return new EqualsBuilder()
					.append(numberOfBits, rhs.numberOfBits)
					.append(bitSet, rhs.bitSet)
					.isEquals();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < numberOfBits; i++) {
			sb.append(bitSet.get(i) ? "1" : "0");
		}
		
		return sb.toString();
	}

	@Override
	public void randomize() {
		for (int i = 0; i < getNumberOfBits(); i++) {
			set(i, PRNG.nextBoolean());
		}
	}

}
