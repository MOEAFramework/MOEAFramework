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
package org.moeaframework.examples.NK;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.objective.Maximize;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/* The following code is derived from derived from the ANSI C code developed by Martin Pelikan available from
 * <http://medal-lab.org/software.php>.  The original license terms are copied below:
 * 
 * Feel free to use, modify and distribute the code with an appropriate acknowledgment of the source, but in all
 * resulting publications please include a citation to the above publication (MEDAL Report No. 2006001).  There is no
 * warranty of any kind for this software.
 */

/**
 * Additively decomposable problems are a class of problems where the fitness function is the sum of several
 * subproblems applied to subsets of the decision variables.  The parameter {@code overlap} controls the amount of
 * overlap in the subsets between subproblems (epistasis).  Overlap can be expected to increase problem difficulty.
 * <p>
 * References:
 * <ol>
 *   <li>Pelikan, M., et al. (2006).  "Hierarchical BOA on Random Decomposable Problems."  MEDAL Report No. 2006001.
 * </ol>
 */
public class AdditivelyDecomposableProblem implements Problem {
	
	/**
	 * The number of bits.
	 */
	private int n;
	
	/**
	 * The size in bits of each subproblem.
	 */
	private int k;
	
	/**
	 * The number of overlapping bits between subproblems.
	 */
	private int overlap;
	
	/**
	 * The permutation identifying each subproblem.
	 */
	private int[] permutation;
	
	/**
	 * The fitness function contributions.
	 */
	private double[][] function;
	
	/**
	 * Constructs a new, random additively deomposable problem.
	 * 
	 * @param n the number of bits
	 * @param k the size in bits of each subproblem
	 * @param overlap the number of overlapping bits between subproblems
	 * @throws IllegalArgumentException if any of the arguments are invalid
	 */
	public AdditivelyDecomposableProblem(int n, int k, int overlap) {
		super();
		this.n = n;
		this.k = k;
		this.overlap = overlap;
		
		checkArguments();
		generateRandom();
	}
	
	/**
	 * Constructs an additively decomposable problem from the given instance file.
	 * 
	 * @param file the instance file
	 * @throws IOException if an I/O error occurred while loading the file
	 * @throws IllegalArgumentException if any of the instance parameters are invalid
	 */
	public AdditivelyDecomposableProblem(File file) throws IOException {
		super();
		
		load(file);
	}

	/**
	 * Returns the number of bits.
	 * 
	 * @return the number of bits
	 */
	public int getN() {
		return n;
	}

	/**
	 * Returns the size in bits of each subproblem.
	 * 
	 * @return the size in bits of each subproblem
	 */
	public int getK() {
		return k;
	}

	/**
	 * Returns the number of overlapping bits between subproblems.
	 * 
	 * @return the number of overlapping bits between subproblems
	 */
	public int getOverlap() {
		return overlap;
	}

	@Override
	public String getName() {
		return "AdditivelydecomposableProblem";
	}

	@Override
	public int getNumberOfVariables() {
		return n;
	}

	@Override
	public int getNumberOfObjectives() {
		return 1;
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}

	@Override
	public void evaluate(Solution solution) {
		boolean[] bits = BinaryVariable.getBinary(solution.getVariable(0));
		double result = 0.0;
		int step = k-overlap;
		
		for (int i = 0; i <= n-k; i += step) {
			int index = 0;
			
			for (int j = 0; j < k; j++) {
				index = (2*index) + (bits[permutation[i+j]] ? 1 : 0);
			}
			
			result += function[i/step][index];
		}
		
		solution.setObjectiveValue(0, result);
	}
	
	/**
	 * Validates the instance parameters.
	 * 
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
	private void checkArguments() {
		Validate.that("n", n).isGreaterThan(0);
		Validate.that("k", k).isGreaterThan(0);
		Validate.that("overlap", overlap).isGreaterThanOrEqualTo(0);
		Validate.that("overlap", overlap).isLessThanOrEqualTo("k/2", k/2);
		Validate.that("n-k", n-k).isDivisibleBy("k-overlap", k-overlap);
	}
	
	/**
	 * Loads the instance file.
	 * 
	 * @param file the instance file
	 * @throws IOException if an I/O error occurred while loading the file
	 * @throws IllegalArgumentException if any of the instance parameters are invalid
	 */
	private void load(File file) throws IOException {
		try (LineReader reader = LineReader.wrap(new FileReader(file))) {
			// read first line containing n, k, offset
			Tokenizer tokenizer = new Tokenizer();
			String[] tokens = tokenizer.decodeToArray(reader.readLine());
			
			if (tokens.length != 3) {
				throw new IOException("Expected 3 values on first line, found " + tokens.length);
			}
			
			n = Integer.parseInt(tokens[0]);
			k = Integer.parseInt(tokens[1]);
			overlap = Integer.parseInt(tokens[2]);
			
			checkArguments();
			
			// populate the function array
			int numberOfFunctions = (n-k) / (k-overlap) + 1;
			
			function = new double[numberOfFunctions][1 << k];
			tokens = tokenizer.decodeToArray(reader.readLine());
			
			if (tokens.length != numberOfFunctions * (1 << k)) {
				throw new IOException("Incorrect number of values on second line, expected " +
						(numberOfFunctions * (1 << k)));
			}
			
			for (int i = 0; i < numberOfFunctions; i++) {
				for (int j = 0; j < (1 << k); j++) {
					function[i][j] = Double.parseDouble(tokens[i*(1 << k) + j]);
				}
			}
			
			// skip the optimum value
			reader.readLine();
			
			// read the permutation
			permutation = new int[n];
			tokens = tokenizer.decodeToArray(reader.readLine());
			
			if (tokens.length != n) {
				throw new IOException("Incorrect number of values on fourth line, expected " + n);
			}
			
			for (int i = 0; i < n; i++) {
				permutation[i] = Integer.parseInt(tokens[i]);
			}
		}
	}
	
	/**
	 * Saves this problem instance to a file.
	 * 
	 * @param file the destination file
	 * @throws IOException if an I/O error occurred while saving the file
	 */
	public void save(File file) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			writer.print(n);
			writer.print(" ");
			writer.print(k);
			writer.print(" ");
			writer.println(overlap);
			
			int numberOfFunctions = (n-k) / (k-overlap) + 1;
			
			for (int i = 0; i < numberOfFunctions; i++) {
				for (int j = 0; j < (1 << k); j++) {
					if ((i > 0) || (j > 0)) {
						writer.print(" ");
					}
					
					writer.print(function[i][j]);
				}
			}
			
			writer.println();
			writer.print(computeOptimum());
			
			for (int i = 0; i < n; i++) {
				if (i > 0) {
					writer.print(" ");
				}
				
				writer.print(permutation[i]);
			}
		}
	}
	
	/**
	 * Uses dynamic programming to compute the optimum value.
	 * 
	 * @return the optimum value
	 */
	public double computeOptimum() {
		double[] lastOptimum = new double[1 << overlap];
		double[] newLastOptimum = new double[1 << overlap];
		int step = k-overlap;
		
		for (int j = 0; j+k <= n; j += step) {
			int index = 1 << k;
			
			Arrays.fill(newLastOptimum, Double.NEGATIVE_INFINITY);
			
			for (int l = 0; l < index; l++) {
				int l1 = l >> step;
				int l2 = l & ((1 << overlap) - 1);
				double value = lastOptimum[l1]+function[j/step][l];
				
				if (value > newLastOptimum[l2]) {
					newLastOptimum[l2] = value;
				}
			}
			
			System.arraycopy(newLastOptimum, 0, lastOptimum, 0, 1 << overlap);
		}
		
		return -StatUtils.max(lastOptimum);
	}
	
	/**
	 * Generates a new, random problem instance.
	 */
	private void generateRandom() {
		int step = k-overlap;
		int numberOfFunctions = (n-k) / step + 1;
		
		function = new double[numberOfFunctions][1 << k];
		permutation = new int[n];
		
		// generate the function values
		for (int i = 0; i+k <= n; i += step) {
			double max = Double.NEGATIVE_INFINITY;
			
			for (int j = 0; j < (1 << k); j++) {
				double value = PRNG.nextDouble();
				
				function[i/step][j] = value;
				max = Math.max(max, value);
			}
		}
		
		// generate the permutation
		for (int i = 0; i < n; i++) {
			permutation[i] = i;
		}
		
		PRNG.shuffle(permutation);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1, 0);
		solution.setVariable(0, new BinaryVariable(n));
		solution.setObjective(0, new Maximize());
		return solution;
	}

	@Override
	public void close() {
		// do nothing
	}

}
