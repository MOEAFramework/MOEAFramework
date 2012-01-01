/* Copyright 2009-2012 David Hadka
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

/**
 * Methods for converting between {@link RealVariable} and
 * {@link BinaryVariable} in both binary and gray code formats.
 */
public class EncodingUtils {

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
			throw new IllegalArgumentException("invalid number of bits");
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
			throw new IllegalArgumentException("invalid number of bits");
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

}
