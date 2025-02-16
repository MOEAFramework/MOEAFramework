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
package org.moeaframework.util.sequence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;
import org.moeaframework.util.io.Tokenizer;

/**
 * Generates sequences using the Sobol' low-discrepancy sequence generator.  When replacing uniformly random numbers
 * in Monte-Carlo integration, the error growth rate is reduced from {@code 1.0/sqrt(n)} to {@code 1.0/n}, where
 * {@code n} is the size of the sequence.
 */
public class Sobol implements Sequence {

	/**
	 * The maximum number of bits supported by this generator.
	 */
	private static final int SCALE = 31;

	/**
	 * The directions used by Kuo and Joe's Sobol' sequence generator.  The array is structured so that
	 * {@code directions[i] = [a, m1, m2, ..., mk]} for dimension {@code i-1}.
	 */
	private static final int[][] DIRECTIONS;

	/**
	 * The path to the resource containing Sobol' directions.
	 */
	private static final String DIRECTIONS_RESOURCE = "joe-kuo-6.21000";

	static {
		try {
			DIRECTIONS = loadDirectionNumbers(DIRECTIONS_RESOURCE);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to load " + DIRECTIONS_RESOURCE, e);
		}
	}

	/**
	 * Constructs a Sobol' low-discrepancy sequence generator.
	 */
	public Sobol() {
		super();
	}

	/**
	 * Loads the direction numbers from an embedded resource.
	 * 
	 * @param resourceName the name of the resource containing the direction numbers
	 * @return the direction numbers
	 */
	private static int[][] loadDirectionNumbers(String resourceName) throws IOException {
		try (LineReader lineReader = Resources.asLineReader(Sobol.class, resourceName,
				ResourceOption.REQUIRED).skipComments()) {
			List<int[]> directions = new ArrayList<>();

			lineReader.readLine(); // remove header line

			for (String line : lineReader) {
				Tokenizer tokenizer = new Tokenizer();
				String[] tokens = tokenizer.decodeToArray(line);
				
				// Skip d, parse s, a, and m_i
				int s = Integer.parseInt(tokens[1]);
				int[] dirs = new int[s + 1];

				for (int i = 0; i <= s; i++) {
					dirs[i] = Integer.parseInt(tokens[2+i]);
				}

				directions.add(dirs);
			}

			return directions.toArray(int[][]::new);
		}
	}

	/**
	 * Returns the index of the least significant zero bit in the specified value.
	 * 
	 * @param value the value
	 * @return the index of the least significant zero bit in the specified value
	 */
	private static int indexOfLeastSignificantZeroBit(int value) {
		int index = 1;

		while ((value & 1) != 0) {
			value >>= 1;
			index++;
		}

		return index;
	}

	/*
	 * The following code is based on the Sobol sequence generator by Frances Y. Kuo and Stephen Joe.  The license
	 * terms are provided below.
	 * 
	 * Copyright (c) 2008, Frances Y. Kuo and Stephen Joe.  All rights reserved.
	 * 
	 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
	 * following conditions are met:
	 * 
	 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
	 *   following disclaimer.
	 * 
	 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
	 *   following disclaimer in the documentation and/or other materials provided with the distribution.
	 * 
	 * * Neither the names of the copyright holders nor the names of the University of New South Wales and the
	 *   University of Waikato and its contributors may be used to endorse or promote products derived from this
	 *   software without specific prior written permission.
	 * 
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
	 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
	 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
	 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
	@Override
	public double[][] generate(int N, int D) {
		if (D > DIRECTIONS.length + 1) {
			throw new IllegalArgumentException("Number of dimensions (D = " + D +
					") too large for Sobol direction file");
		}

		// max number of bits needed
		int L = (int)Math.ceil(Math.log(N) / Math.log(2));

		if (L > SCALE) {
			throw new IllegalArgumentException("Number of samples (N = " + N + ") too large for number of bits");
		}

		double[][] points = new double[N][D];

		for (int i = 0; i < D; i++) {
			// direction numbers, scaled by pow(2, scale)
			long[] V = new long[L + 1];

			if (i == 0) {
				for (int j = 1; j <= L; j++) {
					V[j] = 1 << (SCALE - j); // all m's = 1
				}
			} else {
				int[] m = Sobol.DIRECTIONS[i - 1];
				int a = m[0];
				int s = m.length - 1;

				if (L <= s) {
					for (int j = 1; j <= L; j++) {
						V[j] = m[j] << (SCALE - j);
					}
				} else {
					for (int j = 1; j <= s; j++) {
						V[j] = m[j] << (SCALE - j);
					}

					for (int j = s + 1; j <= L; j++) {
						V[j] = V[j - s] ^ (V[j - s] >> s);
						for (int k = 1; k < s; k++) {
							V[j] ^= ((a >> (s - 1 - k)) & 1) * V[j - k];
						}
					}
				}
			}

			long X = 0;
			for (int j = 1; j < N; j++) {
				X ^= V[indexOfLeastSignificantZeroBit(j - 1)];
				points[j][i] = (double)X / Math.pow(2, SCALE);
			}
		}

		return points;
	}

}
