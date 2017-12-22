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
package org.moeaframework.examples.ga.knapsack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;

/**
 * Variant of the Knapsack problem using the subset encoding.
 */
public class KnapsackSubset extends Knapsack {

	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified file.
	 * 
	 * @param file the file containing the knapsack problem instance
	 * @throws IOException if an I/O error occurred
	 */
	public KnapsackSubset(File file) throws IOException {
		super(file);
	}
	
	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified input stream.
	 * 
	 * @param inputStream the input stream containing the knapsack problem
	 *        instance
	 * @throws IOException if an I/O error occurred
	 */
	public KnapsackSubset(InputStream inputStream) throws IOException {
		super(inputStream);
	}
	
	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified reader.
	 * 
	 * @param reader the reader containing the knapsack problem instance
	 * @throws IOException if an I/O error occurred
	 */
	public KnapsackSubset(Reader reader) throws IOException {
		super(reader);
	}

	@Override
	public void evaluate(Solution solution) {
		int[] items = EncodingUtils.getSubset(solution.getVariable(0));
		double[] f = new double[nsacks];
		double[] g = new double[nsacks];

		// calculate the profits and weights for the knapsacks
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < nsacks; j++) {
				f[j] += profit[j][items[i]];
				g[j] += weight[j][items[i]];
			}
		}

		// check if any weights exceed the capacities
		for (int j = 0; j < nsacks; j++) {
			if (g[j] <= capacity[j]) {
				g[j] = 0.0;
			} else {
				g[j] = g[j] - capacity[j];
			}
		}

		// negate the objectives since Knapsack is maximization
		solution.setObjectives(Vector.negate(f));
		solution.setConstraints(g);
	}

	@Override
	public String getName() {
		return "KnapsackSubset";
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, nsacks, nsacks);
		solution.setVariable(0, EncodingUtils.newSubset(0, nitems, nitems));
		return solution;
	}

}
