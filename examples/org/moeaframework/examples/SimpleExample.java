/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.examples;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.util.CommandLineUtility;

/**
 * A simple example.
 */
public class SimpleExample extends CommandLineUtility {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private SimpleExample() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		Analyzer analyzer = new Analyzer()
				.withProblem("UNROT_DTLZ2_4")
				.includeAllMetrics();
		
		Executor executor = new Executor()
				.withProblem("UNROT_DTLZ2_4")
				.withAlgorithm("NSGAII")
				.withEpsilon(0.15)
				.withMaxEvaluations(10000);
		
		analyzer.add("NSGAII", executor.run()).printAnalysis();
	}

	/**
	 * Command line utility running a simple example.
	 * 
	 * @param args the command line arguments
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws IOException {
		new SimpleExample().start(args);
	}

}
