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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Multiobjective 0/1 knapsack problem. Problem instances are loaded from files
 * in the format defined by Eckart Zitzler and Marco Laumanns at <a href=
 * "http://www.tik.ee.ethz.ch/sop/download/supplementary/testProblemSuite/">
 * http://www.tik.ee.ethz.ch/sop/download/supplementary/testProblemSuite/</a>.
 */
public class Knapsack implements Problem {

	/**
	 * The number of sacks.
	 */
	private int nsacks;

	/**
	 * The number of items.
	 */
	private int nitems;

	/**
	 * Entry {@code profit[i][j]} is the profit from including item {@code j}
	 * in sack {@code i}.
	 */
	private int[][] profit;

	/**
	 * Entry {@code weight[i][j]} is the weight incurred from including item
	 * {@code j} in sack {@code i}.
	 */
	private int[][] weight;

	/**
	 * Entry {@code capacity[i]} is the weight capacity of sack {@code i}.
	 */
	private int[] capacity;

	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified file.
	 * 
	 * @param file the file containing the knapsack problem instance
	 * @throws IOException if an I/O error occurred
	 */
	public Knapsack(File file) throws IOException {
		this(new FileReader(file));
	}
	
	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified input stream.
	 * 
	 * @param inputStream the input stream containing the knapsack problem
	 *        instance
	 * @throws IOException if an I/O error occurred
	 */
	public Knapsack(InputStream inputStream) throws IOException {
		this(new InputStreamReader(inputStream));
	}
	
	/**
	 * Constructs a multiobjective 0/1 knapsack problem instance loaded from
	 * the specified reader.
	 * 
	 * @param reader the reader containing the knapsack problem instance
	 * @throws IOException if an I/O error occurred
	 */
	public Knapsack(Reader reader) throws IOException {
		super();
		
		load(reader);
	}

	/**
	 * Loads the knapsack problem instance from the specified reader.
	 * 
	 * @param reader the file containing the knapsack problem instance
	 * @throws IOException if an I/O error occurred
	 */
	private void load(Reader reader) throws IOException {
		Pattern specificationLine = Pattern.compile("knapsack problem specification \\((\\d+) knapsacks, (\\d+) items\\)");
		Pattern capacityLine = Pattern.compile(" capacity: \\+(\\d+)");
		Pattern weightLine = Pattern.compile("  weight: \\+(\\d+)");
		Pattern profitLine = Pattern.compile("  profit: \\+(\\d+)");

		CommentedLineReader lineReader = null;
		String line = null;
		Matcher matcher = null;
		
		try {
			lineReader = new CommentedLineReader(reader);
			line = lineReader.readLine(); // the problem specification line
			matcher = specificationLine.matcher(line);
			
			if (matcher.matches()) {
				nsacks = Integer.parseInt(matcher.group(1));
				nitems = Integer.parseInt(matcher.group(2));
			} else {
				throw new IOException("knapsack data file not properly formatted: invalid specification line");
			}
			
			capacity = new int[nsacks];
			profit = new int[nsacks][nitems];
			weight = new int[nsacks][nitems];
	
			for (int i = 0; i < nsacks; i++) {
				line = lineReader.readLine(); // line containing "="
				line = lineReader.readLine(); // line containing "knapsack i:"
				line = lineReader.readLine(); // the knapsack capacity
				matcher = capacityLine.matcher(line);
				
				if (matcher.matches()) {
					capacity[i] = Integer.parseInt(matcher.group(1));
				} else {
					throw new IOException("knapsack data file not properly formatted: invalid capacity line");
				}
	
				for (int j = 0; j < nitems; j++) {
					line = lineReader.readLine(); // line containing "item j:"
					line = lineReader.readLine(); // the item weight
					matcher = weightLine.matcher(line);
					
					if (matcher.matches()) {
						weight[i][j] = Integer.parseInt(matcher.group(1));
					} else {
						throw new IOException("knapsack data file not properly formatted: invalid weight line");
					}
	
					line = lineReader.readLine(); // the item profit
					matcher = profitLine.matcher(line);
					
					if (matcher.matches()) {
						profit[i][j] = Integer.parseInt(matcher.group(1));
					} else {
						throw new IOException("knapsack data file not properly formatted: invalid profit line");
					}
				}
			}
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
	}

	@Override
	public void evaluate(Solution solution) {
		boolean[] d = EncodingUtils.getBinary(solution.getVariable(0));
		double[] f = new double[nsacks];
		double[] g = new double[nsacks];

		// calculate the profits and weights for the knapsacks
		for (int i = 0; i < nitems; i++) {
			if (d[i]) {
				for (int j = 0; j < nsacks; j++) {
					f[j] += profit[j][i];
					g[j] += weight[j][i];
				}
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
		return "Knapsack";
	}

	@Override
	public int getNumberOfConstraints() {
		return nsacks;
	}

	@Override
	public int getNumberOfObjectives() {
		return nsacks;
	}

	@Override
	public int getNumberOfVariables() {
		return 1;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, nsacks, nsacks);
		solution.setVariable(0, EncodingUtils.newBinary(nitems));
		return solution;
	}

	@Override
	public void close() {
		//do nothing
	}

}
