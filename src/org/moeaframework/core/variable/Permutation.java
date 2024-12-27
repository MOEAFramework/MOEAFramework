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
import org.moeaframework.core.Defined;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.validate.Validate;

/**
 * Decision variable for permutations.
 */
public class Permutation extends AbstractVariable {

	private static final long serialVersionUID = 5690584295426235286L;

	/**
	 * The permutation array.
	 */
	private int[] permutation;
	
	/**
	 * Constructs a permutation variable with the specified number of elements.
	 * 
	 * @param size the number of elements in the permutation
	 */
	public Permutation(int size) {
		this(null, size);
	}

	/**
	 * Constructs a permutation variable with the specified number of elements.
	 * 
	 * @param name the name of this decision variable
	 * @param size the number of elements in the permutation
	 */
	public Permutation(String name, int size) {
		super(name);
		Validate.that("size", size).isGreaterThan(0);

		permutation = new int[size];

		for (int i = 0; i < size; i++) {
			permutation[i] = i;
		}
	}

	@Override
	public Permutation copy() {
		Permutation copy = new Permutation(name, permutation.length);
		copy.fromArray(permutation);
		return copy;
	}

	/**
	 * Returns the number of elements in this permutation.
	 * 
	 * @return the number of elements in this permutation
	 */
	public int size() {
		return permutation.length;
	}

	/**
	 * Returns the value of the permutation at the specified index.
	 * 
	 * @param index the index of the permutation value to be returned
	 * @return the permutation element at the specified index
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range {@code [0, size()-1]}
	 */
	public int get(int index) {
		return permutation[index];
	}

	/**
	 * Swaps the {@code i}-th and {@code j}-th elements in this permutation.
	 * 
	 * @param i the first index
	 * @param j the second index
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out or range @{code [0, size()-1]}
	 */
	public void swap(int i, int j) {
		int temp = permutation[i];
		permutation[i] = permutation[j];
		permutation[j] = temp;
	}

	/**
	 * Removes the {@code i}-th element and inserts it at the {@code j}-th position.
	 * 
	 * @param i the first index
	 * @param j the second index
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out or range @{code [0, size()-1]}
	 */
	public void insert(int i, int j) {
		int temp = permutation[i];

		// shifts entries in the permutation
		if (i < j) {
			for (int k = i + 1; k <= j; k++) {
				permutation[k - 1] = permutation[k];
			}
		} else if (i > j) {
			for (int k = i - 1; k >= j; k--) {
				permutation[k + 1] = permutation[k];
			}
		}

		permutation[j] = temp;
	}

	/**
	 * Returns a copy of the permutation array.
	 * 
	 * @return a copy of the permutation array
	 */
	public int[] toArray() {
		return permutation.clone();
	}

	/**
	 * Sets the permutation array.
	 * 
	 * @param permutation the permutation array
	 * @throws IllegalArgumentException if the permutation array is not a valid permutation
	 */
	public void fromArray(int[] permutation) {
		if (this.permutation != null) {
			Validate.that("permutation.length", permutation.length).isEqualTo(this.permutation.length);
		}
		
		validatePermutation(permutation);
		
		this.permutation = permutation.clone();
	}
	
	/**
	 * Validate the given array is a permutation.
	 * 
	 * @param permutation the permutation array
	 * @throws IllegalArgumentException if the permutation array is not a valid permutation
	 */
	protected static void validatePermutation(int[] permutation) {
		BitSet bits = new BitSet(permutation.length);
		
		for (int i = 0; i < permutation.length; i++) {
			Validate.that("permutation[i]", permutation[i]).isBetween(0, permutation.length - 1);
			
			if (bits.get(permutation[i])) {
				Validate.that("permutation", permutation).fails("Duplicate value " + permutation[i] + " at index " + i);
			}
			
			bits.set(permutation[i]);
		}
		
		if (bits.cardinality() != permutation.length) {
			Validate.that("permutation", permutation).fails("Missing value " + bits.nextClearBit(0));
		}
	}

	/**
	 * Returns {@code true} if the specified permutation is valid; {@code false} otherwise.
	 * 
	 * @param permutation the permutation array
	 * @return {@code true} if the specified permutation is valid; {@code false} otherwise
	 */
	public static boolean isPermutation(int[] permutation) {
		try {
			validatePermutation(permutation);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(permutation)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Permutation rhs = (Permutation)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(permutation, rhs.permutation)
					.isEquals();
		}
	}

	@Override
	public void randomize() {
		PRNG.shuffle(permutation);
	}
	
	@Override
	public String getDefinition() {
		if (name == null) {
			return Defined.createDefinition(Variable.class, Permutation.class, size());
		} else {
			return Defined.createDefinition(Variable.class, Permutation.class, name, size());
		}
	}
	
	@Override
	public String toString() {
		return encode();
	}
	
	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();
		int[] array = toArray();

		sb.append('[');
		
		for (int i=0; i<array.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			
			sb.append(array[i]);
		}
		
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public void decode(String value) {
		if (value.startsWith("[") && value.endsWith("]")) {
			value = value.substring(1, value.length()-1);
		}
		
		String[] tokens = value.split(",");
		int[] array = new int[tokens.length];
		
		for (int i=0; i<tokens.length; i++) {
			array[i] = Integer.parseInt(tokens[i]);
		}
		
		fromArray(array);
	}
	
	/**
	 * Returns the value stored in a permutation decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a permutation decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Permutation}
	 */
	public static int[] getPermutation(Variable variable) {
		Permutation permutation = Validate.that("variable", variable).isA(Permutation.class);
		return permutation.toArray();
	}
	
	/**
	 * Sets the value of a permutation decision variable.
	 * 
	 * @param variable the decision variable
	 * @param values the permutation to assign the permutation decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Permutation}
	 * @throws IllegalArgumentException if {@code values} is not a valid permutation
	 */
	public static void setPermutation(Variable variable, int[] values) {
		Permutation permutation = Validate.that("variable", variable).isA(Permutation.class);
		permutation.fromArray(values);
	}

}
