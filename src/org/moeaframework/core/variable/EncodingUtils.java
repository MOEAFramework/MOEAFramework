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

import java.util.Arrays;
import java.util.BitSet;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

/**
 * Helper methods for working with various decision variable types and
 * encodings.  First, these methods perform any necessary type checking and
 * type conversion.  Instead of writing:
 * <pre>
 *   double value = ((RealVariable)solution.getVariable(i)).getValue()
 * </pre>
 * the following simplified version is allowed:
 * <pre>
 *   double value = getReal(solution.getVariable(i));
 * </pre>
 * <p>
 * Support for integer encodings is now supported using the
 * {@link #newInt(int, int)} or {@link #newBinaryInt(int, int)}.  The former
 * represents integers as floating-point values while the latter uses binary
 * strings.  Both representations are accessed using {@link #getInt(Variable)}
 * and {@link #setInt(Variable, int)} methods.
 * <p>
 * This class also provides methods for converting between {@link RealVariable}
 * and {@link BinaryVariable} in both binary and gray code formats.
 */
public class EncodingUtils {
	
	/**
	 * The error message shown when the array length is not valid.
	 */
	private static final String INVALID_LENGTH = "invalid number of values";
	
	/**
	 * The error message shown when the number of bits provided is not valid.
	 */
	private static final String INVALID_BITS = "invalid number of bits";
	
	/**
	 * The error message shown when the decision variable is not real-valued.
	 */
	private static final String NOT_REAL = "not a real variable";
	
	/**
	 * The error message shown when the decision variable is not integer-valued.
	 */
	private static final String NOT_INT = "not an integer variable";
	
	/**
	 * The error message shown when the decision variable is not a permutation.
	 */
	private static final String NOT_PERMUTATION = "not a permutation";
	
	/**
	 * The error message shown when the decision variable is not a subset.
	 */
	private static final String NOT_SUBSET = "not a subset";
	
	/**
	 * The error message shown when the decision variable is not a binary value.
	 */
	private static final String NOT_BINARY = "not a binary variable";
	
	/**
	 * The error message shown when the decision variable is not a boolean.
	 */
	private static final String NOT_BOOLEAN = "not a boolean variable";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private EncodingUtils() {
		super();
	}

	/**
	 * Encodes the specified real variable into a binary variable. The number of
	 * bits used in the encoding is {@code binary.getNumberOfBits()}.
	 * 
	 * @param real the real variable
	 * @param binary the binary variable to which the real value is encoded
	 */
	public static void encode(RealVariable real, BinaryVariable binary) {
		int numberOfBits = binary.getNumberOfBits();
		double lowerBound = real.getLowerBound();
		double upperBound = real.getUpperBound();

		double value = real.getValue();
		double scale = (value - lowerBound) / (upperBound - lowerBound);
		long index = Math.round(scale * ((1L << numberOfBits) - 1));

		encode(index, binary);
	}

	/**
	 * Decodes the specified binary variable into its real value.
	 * 
	 * @param binary the binary variable
	 * @param real the real variable to which the value is decoded
	 */
	public static void decode(BinaryVariable binary, RealVariable real) {
		int numberOfBits = binary.getNumberOfBits();
		double lowerBound = real.getLowerBound();
		double upperBound = real.getUpperBound();

		long index = decode(binary);
		double scale = index / (double)((1L << numberOfBits) - 1);
		double value = lowerBound + (upperBound - lowerBound) * scale;

		real.setValue(value);
	}

	/**
	 * Encodes the integer into the specified binary variable. The number of
	 * bits used in the encoding is {@code binary.getNumberOfBits()}.
	 * 
	 * @param value an integer
	 * @param binary the binary variable to which the value is encoded
	 */
	public static void encode(long value, BinaryVariable binary) {
		int numberOfBits = binary.getNumberOfBits();

		if (value < 0) {
			throw new IllegalArgumentException("negative value");
		}

		if ((numberOfBits < 1) || (numberOfBits > 63)) {
			throw new IllegalArgumentException(INVALID_BITS);
		}

		if ((1L << numberOfBits) <= value) {
			throw new IllegalArgumentException(
					"number of bits not sufficient to represent value");
		}

		for (int i = 0; i < numberOfBits; i++) {
			binary.set(i, (value & (1L << i)) != 0);
		}
	}

	/**
	 * Decodes the specified binary variable into its integer value.
	 * 
	 * @param binary the binary variable
	 * @return the integer value of the specified binary variable
	 */
	public static long decode(BinaryVariable binary) {
		int numberOfBits = binary.getNumberOfBits();

		if ((numberOfBits < 1) || (numberOfBits > 63)) {
			throw new IllegalArgumentException(INVALID_BITS);
		}

		long value = 0;

		for (int i = 0; i < numberOfBits; i++) {
			if (binary.get(i)) {
				value |= (1L << i);
			}
		}

		return value;
	}

	/**
	 * Converts a binary variable from a binary encoding to gray encoding. The
	 * gray encoding ensures two adjacent values have binary representations
	 * differing in only {@code 1} bit (a Hamming distance of {@code 1}).
	 * 
	 * @param variable the variable to be converted
	 */
	public static void binaryToGray(BinaryVariable variable) {
		int n = variable.getNumberOfBits();

		BitSet binary = variable.getBitSet();

		variable.set(n - 1, binary.get(n - 1));
		for (int i = n - 2; i >= 0; i--) {
			variable.set(i, binary.get(i + 1) ^ binary.get(i));
		}
	}

	/**
	 * Converts a binary variable from a gray encoding to binary encoding.
	 * 
	 * @param variable the variable to be converted
	 */
	public static void grayToBinary(BinaryVariable variable) {
		int n = variable.getNumberOfBits();

		BitSet gray = variable.getBitSet();

		variable.set(n - 1, gray.get(n - 1));
		for (int i = n - 2; i >= 0; i--) {
			variable.set(i, variable.get(i + 1) ^ gray.get(i));
		}
	}
	
	/**
	 * Returns a new floating-point decision variable bounded within the
	 * specified range.
	 * 
	 * @param lowerBound the lower bound of the floating-point value
	 * @param upperBound the upper bound of the floating-point value
	 * @return a new floating-point decision variable bounded within the
	 *         specified range
	 */
	public static RealVariable newReal(double lowerBound, double upperBound) {
		return new RealVariable(lowerBound, upperBound);
	}
	
	/**
	 * Returns a new integer-valued decision variable bounded within the
	 * specified range.  The integer value is encoded using a
	 * {@link RealVariable}.
	 * 
	 * @param lowerBound the lower bound of the integer value
	 * @param upperBound the upper bound of the integer value
	 * @return a new integer-valued decision variable bounded within the
	 *         specified range
	 */
	public static RealVariable newInt(int lowerBound, int upperBound) {
		return new RealVariable(lowerBound, Math.nextAfter(
				(double)(upperBound+1), Double.NEGATIVE_INFINITY));
	}
	
	/**
	 * Returns a new integer-valued decision variable bounded within the
	 * specified range.  The integer value is encoded using a
	 * {@link BinaryVariable}.
	 * 
	 * @param lowerBound the lower bound of the integer value
	 * @param upperBound the upper bound of the integer value
	 * @return a new integer-valued decision variable bounded within the
	 *         specified range
	 */
	public static BinaryIntegerVariable newBinaryInt(int lowerBound,
			int upperBound) {
		return new BinaryIntegerVariable(lowerBound, upperBound);
	}
	
	/**
	 * Returns a new boolean decision variable.
	 * 
	 * @return a new boolean decision variable
	 */
	public static BinaryVariable newBoolean() {
		return new BinaryVariable(1);
	}
	
	/**
	 * Returns a new binary decision variable with the specified number of bits.
	 * 
	 * @param length the number of bits in the binary decision variable
	 * @return a new binary decision variable with the specified number of bits
	 */
	public static BinaryVariable newBinary(int length) {
		return new BinaryVariable(length);
	}
	
	/**
	 * Returns a new permutation with the specified number of items.
	 * 
	 * @param length the number of items in the permutation
	 * @return a new permutation with the specified number of items
	 */
	public static Permutation newPermutation(int length) {
		return new Permutation(length);
	}
	
	/**
	 * Returns a new fixed-size subset with the specified number of items.
	 * 
	 * @param k the fixed size of the subset
	 * @param n the total number of items in the set
	 * @return a new subset with the specified number of items
	 */
	public static Subset newSubset(int k, int n) {
		return new Subset(k, n);
	}
	
	/**
	 * Returns a new variable-size subset with the specified number of items.
	 * 
	 * @param l the minimum number of members in the subset
	 * @param u the maximum number of members in the subset
	 * @param n the total number of items in the set
	 * @return a new subset with the specified number of items
	 */
	public static Subset newSubset(int l, int u, int n) {
		return new Subset(l, u, n);
	}
	
	/**
	 * Returns the value stored in a floating-point decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a floating-point decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link RealVariable}
	 */
	public static double getReal(Variable variable) {
		if (variable instanceof RealVariable) {
			return ((RealVariable)variable).getValue();
		} else {
			throw new IllegalArgumentException(NOT_REAL);
		}
	}
	
	/**
	 * Returns the value stored in an integer-valued decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in an integer-valued decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link RealVariable}
	 */
	public static int getInt(Variable variable) {
		if (variable instanceof RealVariable) {
			return (int)Math.floor(((RealVariable)variable).getValue());
		} else if (variable instanceof BinaryIntegerVariable) {
			return (int)Math.floor(((BinaryIntegerVariable)variable).getValue());
		} else {
			throw new IllegalArgumentException(NOT_INT);
		}
	}
	
	/**
	 * Returns the value stored in a binary decision variable as a
	 * {@link BitSet}.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a binary decision variable as a
	 *         {@code BitSet}
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 */
	public static BitSet getBitSet(Variable variable) {
		if (variable instanceof BinaryVariable) {
			return ((BinaryVariable)variable).getBitSet();
		} else {
			throw new IllegalArgumentException(NOT_BINARY);
		}
	}
	
	/**
	 * Returns the value stored in a binary decision variable as a boolean
	 * array.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a binary decision variable as a boolean
	 *         array
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 */
	public static boolean[] getBinary(Variable variable) {
		if (variable instanceof BinaryVariable) {
			BinaryVariable binaryVariable = (BinaryVariable)variable;
			boolean[] result = new boolean[binaryVariable.getNumberOfBits()];
			
			for (int i=0; i<binaryVariable.getNumberOfBits(); i++) {
				result[i] = binaryVariable.get(i);
			}
			
			return result;
		} else {
			throw new IllegalArgumentException(NOT_BINARY);
		}
	}
	
	/**
	 * Returns the value stored in a boolean decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a boolean decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 */
	public static boolean getBoolean(Variable variable) {
		boolean[] values = getBinary(variable);
		
		if (values.length == 1) {
			return values[0];
		} else {
			throw new IllegalArgumentException(NOT_BOOLEAN);
		}
	}
	
	/**
	 * Returns the value stored in a permutation decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a permutation decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Permutation}
	 */
	public static int[] getPermutation(Variable variable) {
		if (variable instanceof Permutation) {
			return ((Permutation)variable).toArray();
		} else {
			throw new IllegalArgumentException(NOT_PERMUTATION);
		}
	}
	
	/**
	 * Returns the value stored in a subset decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a subset decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Subset}
	 */
	public static int[] getSubset(Variable variable) {
		if (variable instanceof Subset) {
			return ((Subset)variable).toArray();
		} else {
			throw new IllegalArgumentException(NOT_SUBSET);
		}
	}
	
	/**
	 * Returns the value stored in a subset decision variable with the items sorted.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a subset decision variable with the items sorted
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Subset}
	 */
	public static int[] getOrderedSubset(Variable variable) {
		int[] subset = getSubset(variable);
		Arrays.sort(subset);
		return subset;
	}
	
	/**
	 * Returns the subset as a binary string, where 1 indicates the index is included
	 * in the set.
	 * 
	 * @param variable the decision variable
	 * @return a binary string representation of the subset
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Subset}
	 */
	public static boolean[] getSubsetAsBinary(Variable variable) {
		if (variable instanceof Subset) {
			Subset subset = (Subset)variable;
			boolean[] result = new boolean[subset.getN()];
			
			for (int i = 0; i < subset.getN(); i++) {
				result[i] = subset.contains(i);
			}
			
			return result;
		} else {
			throw new IllegalArgumentException(NOT_SUBSET);
		}
	}
	
	/**
	 * Returns the subset as a BitSet, where a set bit indicates the index is included
	 * in the set.
	 * 
	 * @param variable the decision variable
	 * @return a BitSet representation of the subset
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Subset}
	 */
	public static BitSet getSubsetAsBitSet(Variable variable) {
		if (variable instanceof Subset) {
			Subset subset = (Subset)variable;
			BitSet bitSet = new BitSet(subset.getN());
			
			for (int i = 0; i < subset.getN(); i++) {
				bitSet.set(i, subset.contains(i));
			}
			
			return bitSet;
		} else {
			throw new IllegalArgumentException(NOT_SUBSET);
		}
	}
	
	/**
	 * Returns the array of floating-point decision variables stored in a
	 * solution.  The solution must contain only floating-point decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @return the array of floating-point decision variables stored in a
	 *          solution
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution is not of type {@link RealVariable}
	 */
	public static double[] getReal(Solution solution) {
		return getReal(solution, 0, solution.getNumberOfVariables());
	}
	
	/**
	 * Returns the array of floating-point decision variables stored in a
	 * solution between the specified indices.  The decision variables located
	 * between the start and end index must all be floating-point decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @return the array of floating-point decision variables stored in a
	 *         solution between the specified indices
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution between the start and end index is not of type
	 *         {@link RealVariable}
	 */
	public static double[] getReal(Solution solution, int startIndex,
			int endIndex) {
		double[] result = new double[endIndex - startIndex];
		
		for (int i=startIndex; i<endIndex; i++) {
			result[i-startIndex] = getReal(solution.getVariable(i));
		}
		
		return result;
	}
	
	/**
	 * Returns the array of integer-valued decision variables stored in a
	 * solution.  The solution must contain only integer-valued decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @return the array of integer-valued decision variables stored in a
	 *          solution
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution is not of type {@link RealVariable}
	 */
	public static int[] getInt(Solution solution) {
		return getInt(solution, 0, solution.getNumberOfVariables());
	}
	
	/**
	 * Returns the array of integer-valued decision variables stored in a
	 * solution between the specified indices.  The decision variables located
	 * between the start and end index must all be integer-valued decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @return the array of integer-valued decision variables stored in a
	 *         solution between the specified indices
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution between the start and end index is not of type
	 *         {@link RealVariable}
	 */
	public static int[] getInt(Solution solution, int startIndex, int endIndex) {
		int[] result = new int[endIndex - startIndex];
		
		for (int i=startIndex; i<endIndex; i++) {
			result[i-startIndex] = getInt(solution.getVariable(i));
		}
		
		return result;
	}
	
	/**
	 * Sets the value of a floating-point decision variable.
	 * 
	 * @param variable the decision variable
	 * @param value the value to assign the floating-point decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link RealVariable}
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Variable variable, double value) {
		if (variable instanceof RealVariable) {
			((RealVariable)variable).setValue(value);
		} else {
			throw new IllegalArgumentException(NOT_REAL);
		}
	}
	
	/**
	 * Sets the values of all floating-point decision variables stored in the
	 * solution.  The solution must contain only floating-point decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param values the array of floating-point values to assign the solution
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution is not of type {@link RealVariable}
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Solution solution, double[] values) {
		setReal(solution, 0, solution.getNumberOfVariables(), values);
	}
	
	/**
	 * Sets the values of the floating-point decision variables stored in a
	 * solution between the specified indices.  The decision variables located
	 * between the start and end index must all be floating-point decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @param values the array of floating-point values to assign the
	 *        decision variables
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution between the start and end index is not of type
	 *         {@link RealVariable}
	 * @throws IllegalArgumentException if an invalid number of values are
	 *         provided
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setReal(Solution solution, int startIndex, int endIndex,
			double[] values) {
		if (values.length != (endIndex - startIndex)) {
			throw new IllegalArgumentException(INVALID_LENGTH);
		}
		
		for (int i=startIndex; i<endIndex; i++) {
			setReal(solution.getVariable(i), values[i-startIndex]);
		}
	}
	
	/**
	 * Sets the value of an integer-valued decision variable.
	 * 
	 * @param variable the decision variable
	 * @param value the value to assign the integer-valued decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link RealVariable}
	 * @throws IllegalArgumentException if the value is out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Variable variable, int value) {
		if (variable instanceof RealVariable) {
			((RealVariable)variable).setValue(value);
		} else if (variable instanceof BinaryIntegerVariable) {
			((BinaryIntegerVariable)variable).setValue(value);
		} else {
			throw new IllegalArgumentException(NOT_INT);
		}
	}
	
	/**
	 * Sets the values of all integer-valued decision variables stored in the
	 * solution.  The solution must contain only integer-valued decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param values the array of integer values to assign the solution
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution is not of type {@link RealVariable}
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Solution solution, int[] values) {
		setInt(solution, 0, solution.getNumberOfVariables(), values);
	}
	
	/**
	 * Sets the values of the integer-valued decision variables stored in a
	 * solution between the specified indices.  The decision variables located
	 * between the start and end index must all be integer-valued decision
	 * variables.
	 * 
	 * @param solution the solution
	 * @param startIndex the start index (inclusive)
	 * @param endIndex the end index (exclusive)
	 * @param values the array of floating-point values to assign the
	 *        decision variables
	 * @throws IllegalArgumentException if any decision variable contained in
	 *         the solution between the start and end index is not of type
	 *         {@link RealVariable}
	 * @throws IllegalArgumentException if an invalid number of values are
	 *         provided
	 * @throws IllegalArgumentException if any of the values are out of bounds
	 *         ({@code value < getLowerBound()) || (value > getUpperBound()})
	 */
	public static void setInt(Solution solution, int startIndex, int endIndex,
			int[] values) {
		if (values.length != (endIndex - startIndex)) {
			throw new IllegalArgumentException(INVALID_LENGTH);
		}
		
		for (int i=startIndex; i<endIndex; i++) {
			setInt(solution.getVariable(i), values[i-startIndex]);
		}
	}
	
	/**
	 * Sets the bits in a binary decision variable using the given
	 * {@link BitSet}.
	 * 
	 * @param variable the decision variable
	 * @param bitSet the bits to set in the binary decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 */
	public static void setBitSet(Variable variable, BitSet bitSet) {
		if (variable instanceof BinaryVariable) {
			BinaryVariable binaryVariable = (BinaryVariable)variable;
			
			for (int i=0; i<binaryVariable.getNumberOfBits(); i++) {
				binaryVariable.set(i, bitSet.get(i));
			}
		} else {
			throw new IllegalArgumentException(NOT_BINARY);
		}
	}
	
	/**
	 * Sets the bits in a binary decision variable using the given boolean
	 * array.
	 * 
	 * @param variable the decision variable
	 * @param values the bits to set in the binary decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 * @throws IllegalArgumentException if an invalid number of values are
	 *         provided
	 */
	public static void setBinary(Variable variable, boolean[] values) {
		if (variable instanceof BinaryVariable) {
			BinaryVariable binaryVariable = (BinaryVariable)variable;
			
			if (values.length != binaryVariable.getNumberOfBits()) {
				throw new IllegalArgumentException(INVALID_LENGTH);
			}
			
			for (int i=0; i<binaryVariable.getNumberOfBits(); i++) {
				binaryVariable.set(i, values[i]);
			}
		} else {
			throw new IllegalArgumentException(NOT_BINARY);
		}
	}
	
	/**
	 * Sets the value of a boolean decision variable.
	 * 
	 * @param variable the decision variable
	 * @param value the value to assign the boolean decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link BinaryVariable}
	 * @throws IllegalArgumentException if the number of bits in the binary
	 *         variable is not {@code 1}
	 */
	public static void setBoolean(Variable variable, boolean value) {
		setBinary(variable, new boolean[] { value });
	}
	
	/**
	 * Sets the value of a permutation decision variable.
	 * 
	 * @param variable the decision variable
	 * @param values the permutation to assign the permutation decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Permutation}
	 * @throws IllegalArgumentException if {@code values} is not a valid
	 *         permutation
	 */
	public static void setPermutation(Variable variable, int[] values) {
		if (variable instanceof Permutation) {
			((Permutation)variable).fromArray(values);
		} else {
			throw new IllegalArgumentException(NOT_PERMUTATION);
		}
	}
	
	/**
	 * Sets the value of a subset decision variable.
	 * 
	 * @param variable the decision variable
	 * @param values the subset to assign the subset decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type
	 *         {@link Subset}
	 * @throws IllegalArgumentException if {@code values} is not a valid
	 *         subset
	 */
	public static void setSubset(Variable variable, int[] values) {
		if (variable instanceof Subset) {
			((Subset)variable).fromArray(values);
		} else {
			throw new IllegalArgumentException(NOT_SUBSET);
		}
	}
	
	/**
	 * Sets the value of a subset decision variable using a binary string representation.
	 * 
	 * @param variable the decision variable
	 * @param values the binary string representation of a subset
	 * @throws IllegalArgumentException if the binary string representation is not a
	 *         valid subset
	 */
	public static void setSubset(Variable variable, boolean[] values) {
		BitSet bitSet = new BitSet(values.length);
			
		for (int i = 0; i < values.length; i++) {
			bitSet.set(i, values[i]);
		}
			
		setSubset(variable, bitSet);
	}
	
	/**
	 * Sets the value of a subset decision variable using a BitSet representation.
	 * 
	 * @param variable the decision variable
	 * @param values the BitSet representation of a subset
	 * @throws IllegalArgumentException if the BitSet representation is not a
	 *         valid subset
	 */
	public static void setSubset(Variable variable, BitSet bitSet) {
		if (variable instanceof Subset) {
			int[] values = new int[bitSet.cardinality()];
			int count = 0;
			
			for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
				values[count++] = i;
			}
			
			setSubset(variable, values);
		} else {
			throw new IllegalArgumentException(NOT_SUBSET);
		}
	}

}
