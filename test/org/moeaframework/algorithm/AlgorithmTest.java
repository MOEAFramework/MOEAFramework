/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.algorithm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.Assert;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.spi.AlgorithmFactory;

/**
 * Methods for comparing two algorithm implementations statistically.
 */
public abstract class AlgorithmTest {
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default
	 * {@link AlgorithmFactory} is used to create instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @throws IOException should not occur
	 */
	public void test(String problem, String algorithm1, String algorithm2)
			throws IOException {
		test(problem, algorithm1, algorithm2, AlgorithmFactory.getInstance());
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param factory the factory used to construct the algorithms
	 * @throws IOException should not occur
	 */
	public void test(String problem, String algorithm1, String algorithm2, 
			AlgorithmFactory factory) throws IOException {
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeAllMetrics()
				.showAggregate()
				.showStatisticalSignificance();
		
		Executor executor = new Executor()
				.withProblem(problem)
				.usingAlgorithmFactory(factory)
				.withMaxEvaluations(10000)
				.distributeOnAllCores();
		
		analyzer.addAll(algorithm1, 
				executor.withAlgorithm(algorithm1).runSeeds(10));
		analyzer.addAll(algorithm2, 
				executor.withAlgorithm(algorithm2).runSeeds(10));
		
		ByteArrayOutputStream output = null;
		
		try {
			output = new ByteArrayOutputStream();
			
			analyzer.printAnalysis(new PrintStream(output));
			Assert.assertTrue(countIndifferences(output.toString(), algorithm1)
					>= 5);
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	/**
	 * Counts the number of indifferences in the statistical output by counting
	 * the number of lines matching
	 * <pre>
	 *        Indifferent: [<algorithmName>]
	 * </pre>
	 * 
	 * @param output the statistical output from 
	 *        {@link Analyzer#printAnalysis(PrintStream)}
	 * @param algorithmName the name of one of the algorithms being tested
	 * @return the number of indifferences in the statistical output
	 * @throws IOException should not occur
	 */
	public int countIndifferences(String output, String algorithmName) 
	throws IOException {
		BufferedReader reader = null;
		String line = null;
		int count = 0;
		
		try {
			reader = new BufferedReader(new StringReader(output));
			
			while ((line = reader.readLine()) != null) {
				if (line.matches("^\\s*Indifferent:\\s*\\[" + algorithmName + 
						"\\]\\s*$")) {
					count++;
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return count;
	}

}
