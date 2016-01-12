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
package org.moeaframework.examples.ga.NK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/* The following code is derived from derived from the ANSI C code developed
 * by Martin Pelikan available from <http://medal-lab.org/software.php>.  The
 * original license terms are copied below:
 * 
 * Feel free to use, modify and distribute the code with an appropriate
 * acknowledgment of the source, but in all resulting publications please
 * include a citation to the above publication (MEDAL Report No. 2008001).
 * There is no warranty of any kind for this software.
 */

/**
 * MNK-fitness landscape problem.  The NK-model defines a problem with N bits
 * and a fixed amount of interaction between the bits, K.  The fitness function
 * is a summation of N individual components, where the i-th component is
 * dependent on the value if the i-th bit plus K other bits.  By increasing
 * the value of K, from 0 to N-1, the amount of epistasis is increased.  For
 * the multiobjective (MNK) case when M > 1, each objective is a separate
 * NK-model.
 * <p>
 * This code is derived from the ANSI C code developed by Martin Pelikan
 * available from <http://medal-lab.org/software.php>.  It is able to save and
 * load instance files in the same format.  Note that this implementation
 * does not divide the final fitness value by N, as some formulations of the
 * NK-model specify.
 * <p>
 * References:
 * <ol>
 *   <li>Pelikan, M. (2008).  "Analysis of Estimation of Distribution Algorithms
 *       and Genetic Algorithms on NK Landscapes."  MEDAL Report No. 2008001.
 * </ol>
 */
public class MNKProblem implements Problem {
	
	/**
	 * The number of objectives.
	 */
	private int M;
	
	/**
	 * The number of bits.
	 */
	private int N;
	
	/**
	 * The number of epistatic interactions.
	 */
	private int K;
	
	/**
	 * The neighboring bits describing the epistatic interactions.
	 */
	private int[][][] neighbors;
	
	/**
	 * The fitness function contributions.
	 */
	private double[][][] function;
	
	/**
	 * Constructs a new, random single-objective NK-landscape problem.
	 * 
	 * @param N the number of bits
	 * @param K the number of epistatic interactions
	 * @throws IllegalArgumentException if any of the arguments are invalid
	 */
	public MNKProblem(int N, int K) {
		this(1, N, K);
	}
	
	/**
	 * Constructs a new, random single-objective NK-landscape problem (when
	 * {@code M=1}) or multi-objective MNK-landscape problem (when {@code M>1}).
	 * 
	 * @param M the number of objectives
	 * @param N the number of bits
	 * @param K the number of epistatic interactions
	 * @throws IllegalArgumentException if any of the arguments are invalid
	 */
	public MNKProblem(int M, int N, int K) {
		super();
		this.M = M;
		this.N = N;
		this.K = K;
		
		checkArguments();
		generateRandom();
	}
	
	/**
	 * Constructs an MNK-landscape problem from the given instance file.
	 * 
	 * @param file the instance file
	 * @throws IOException if an I/O error occurred while loading the file
	 * @throws IllegalArgumentException if any of the instance parameters are
	 *         invalid
	 */
	public MNKProblem(File file) throws IOException {
		super();
		
		load(file);
	}
	
	@Override
	public void evaluate(Solution solution) {
		boolean[] bits = EncodingUtils.getBinary(solution.getVariable(0));
		double result = 0.0;
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				int index = 0;
				
				for (int k = 0; k <= K; k++) {
					index = (index << 1) + (bits[neighbors[i][j][k]] ? 1 : 0);
				}
				
				result += function[i][j][index];
			}
			
			solution.setObjective(i, -result);
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, M, 0);
		solution.setVariable(0, EncodingUtils.newBinary(N));
		return solution;
	}
	
	/**
	 * Returns the number of objectives.
	 * 
	 * @return the number of objectives
	 */
	public int getM() {
		return M;
	}
	
	/**
	 * Returns the number of bits.
	 * 
	 * @return the number of bits
	 */
	public int getN() {
		return N;
	}

	/**
	 * Returns the number of epistatic interactions.
	 * 
	 * @return the number of epistatic interactions
	 */
	public int getK() {
		return K;
	}

	/**
	 * Validates the instance parameters.
	 * 
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
	private void checkArguments() {
		if (M <= 0) {
			throw new IllegalArgumentException("M must be greater than 0");
		}
		
		if (N <= 0) {
			throw new IllegalArgumentException("N must be greater than 0");
		}
		
		if (K >= N) {
			throw new IllegalArgumentException("K must be less than N");
		}
		
		if ((K < 0) || (K > 31)) {
			throw new IllegalArgumentException("K must be an integer between 0 and 31");
		}
	}
	
	/**
	 * Loads the instance file.
	 * 
	 * @param file the instance file
	 * @throws IOException if an I/O error occurred while loading the file
	 * @throws IllegalArgumentException if any of the instance parameters are
	 *         invalid
	 */
	private void load(File file) throws IOException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			
			// read M, N, and K
			String line = reader.readLine();
			String[] tokens = line.split("\\s+");

			if (tokens.length == 2) {
				M = 1;
				N = Integer.parseInt(tokens[0]);
				K = Integer.parseInt(tokens[1]);
			} else if (tokens.length == 3) {
				M = Integer.parseInt(tokens[0]);
				N = Integer.parseInt(tokens[1]);
				K = Integer.parseInt(tokens[2]);
			} else {
				throw new IOException("expected two or three values on first line");
			}
			
			checkArguments();
			
			// load the data
			neighbors = new int[M][N][K+1];
			function = new double[M][N][1 << (K+1)];
			
			for (int i = 0; i < M; i++) {
				for (int j = 0; j < N; j++) {
					for (int k = 0; k < K+1; k++) {
						neighbors[i][j][k] = Integer.parseInt(reader.readLine());
					}
				}
			}
			
			for (int i = 0; i < M; i++) {
				for (int j = 0; j < N; j++) {
					for (int k = 0; k < (1 << (K+1)); k++) {
						function[i][j][k] = Double.parseDouble(reader.readLine());
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
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
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(new FileWriter(file));
			
			if (M > 1) {
				writer.print(M);
				writer.print(" ");
			}
			
			writer.print(N);
			writer.print(" ");
			writer.println(K);
			
			for (int i = 0; i < M; i++) {
				for (int j = 0; j < N; j++) {
					for (int k = 0; k < K+1; k++) {
						writer.println(neighbors[i][j][k]);
					}
				}
			}
			
			for (int i = 0; i < M; i++) {
				for (int j = 0; j < N; j++) {
					for (int k = 0; k < (1 << (K+1)); k++) {
						writer.println(function[i][j][k]);
					}
				}
			}
			
			writer.println("Optimum: ?");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * Generates a new, random problem instance.
	 */
	private void generateRandom() {
		neighbors = new int[M][N][K+1];
		function = new double[M][N][1 << (K+1)];
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				boolean[] generated = new boolean[N];
				generated[j] = true;
				
				for (int k = 0; k < K; k++) {
					int neighbor;
					
					do {
						neighbor = PRNG.nextInt(N);
					} while (generated[neighbor]);
					
					generated[neighbor] = true;
					neighbors[i][j][k] = neighbor;
				}
				
				neighbors[i][j][K] = j;
				Arrays.sort(neighbors[i][j]);
				
				for (int k = 0; k < (1 << (K+1)); k++) {
					function[i][j][k] = PRNG.nextDouble();
				}
			}
		}
	}

	@Override
	public String getName() {
		return "MNKProblem";
	}

	@Override
	public int getNumberOfVariables() {
		return 1;
	}

	@Override
	public int getNumberOfObjectives() {
		return M;
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}

	@Override
	public void close() {
		// do nothing
	}

}
