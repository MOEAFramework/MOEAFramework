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
package org.moeaframework.snippet;

import java.util.BitSet;

import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

@SuppressWarnings("unused")
public class VariableSnippet {

	@Test
	public void real() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		double lowerBound = 0.0;
		double upperBound = 1.0;
		
		// Creating a real-valued variable:
		solution.setVariable(i, EncodingUtils.newReal(lowerBound, upperBound));

		// Reading and writing a single variable:
		double value = EncodingUtils.getReal(solution.getVariable(i));
		EncodingUtils.setReal(solution.getVariable(i), value);

		// Reading and writing all variables (when all variables in the solution are real-valued):
		double[] values = EncodingUtils.getReal(solution);
		EncodingUtils.setReal(solution, values);
	}
	
	@Test
	public void binary() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int length = 10;
		
		// Creating a binary variable:
		solution.setVariable(i, EncodingUtils.newBinary(length));

		// Reading the values as an array or BitSet:
		boolean[] bits = EncodingUtils.getBinary(solution.getVariable(i));
		BitSet bitSet = EncodingUtils.getBitSet(solution.getVariable(i));

		// Updating the bits:
		EncodingUtils.setBinary(solution.getVariable(i), bits);
	}
	
	@Test
	public void integer() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int lowerBound = 0;
		int upperBound = 10;
		
		// Creating an integer variable:
		solution.setVariable(i, EncodingUtils.newInt(lowerBound, upperBound));
		solution.setVariable(i, EncodingUtils.newBinaryInt(lowerBound, upperBound));

		// Reading and writing a single variable:
		int value = EncodingUtils.getInt(solution.getVariable(i));
		EncodingUtils.setInt(solution.getVariable(i), value);

		// Reading and writing all variables (when all variables in the solution are integers):
		int[] values = EncodingUtils.getInt(solution);
		EncodingUtils.setInt(solution, values);
	}
	
	@Test
	public void permutation() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int length = 10;
		
		// Creating a permutation:
		solution.setVariable(i, EncodingUtils.newPermutation(length));

		// Reading and writing a permutation:
		int[] permutation = EncodingUtils.getPermutation(solution.getVariable(i));
		EncodingUtils.setPermutation(solution.getVariable(i), permutation);
	}
	
	@Test
	public void subset() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int fixedSize = 10;
		int minSize = 5;
		int maxSize = 10;
		int numberOfElements = 20;
		
		// Creating a fixed and variable-length subset:
		solution.setVariable(i, EncodingUtils.newSubset(fixedSize, numberOfElements));
		solution.setVariable(i, EncodingUtils.newSubset(minSize, maxSize, numberOfElements));

		// Reading and writing the sets
		int[] subset = EncodingUtils.getSubset(solution.getVariable(i));
		EncodingUtils.setSubset(solution.getVariable(i), subset);
	}
	
}
