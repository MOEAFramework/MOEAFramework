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
package org.moeaframework.examples.external;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

/**
 * Demonstrates how problems can be defined externally to the MOEA Framework.
 * This is useful in many scenarios, including when the problem:
 * 
 * 1. is already written in another programming language (C/C++, Python, etc.),
 * 2. is computationally expensive and benefits from compilation, or
 * 3. uses functionality not available in Java (GPU acceleration, etc.).
 * 
 * One way to achieve this is to invoke the executable from within the problem's
 * evaluation function.  This works fine when the overhead of starting a new process
 * is minimal.
 * 
 * For cases where startup is expensive, the MOEA Framework can stream all evaluations
 * to a single process by passing the inputs and reading the outputs from either the
 * standard input/output streams or over network sockets.  This example demonstrates
 * standard input/output.
 * 
 * To help setup such programs, we have provided a C library used to receive the inputs
 * sent from the MOEA Framework and write the responses (objectives and constraints) back.
 * See {@code moeaframework.c} and {@code moeaframework.h} in the {@code examples/} folder.
 * The {@code dtlz2.c} program demonstrates how to use this library.
 * 
 * To run this example, first compile the C program by running the {@code make} command
 * in the {@code ./examples/} folder.  Then, run this Java example.
 */
public class ExternalProblemWithStdio {

	/**
	 * The ExternalProblem opens a communication channel with the external
	 * process defined in the constructor.
	 */
	public static class MyDTLZ2 extends ExternalProblem {

		public MyDTLZ2() throws IOException {
			super(new ExternalProblem.Builder()
					.withCommand("./examples/dtlz2_stdio.exe"));
		}

		@Override
		public String getName() {
			return "DTLZ2";
		}

		@Override
		public int getNumberOfVariables() {
			return 11;
		}

		@Override
		public int getNumberOfObjectives() {
			return 2;
		}

		@Override
		public int getNumberOfConstraints() {
			return 0;
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(0.0, 1.0));
			}

			return solution;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		//check if the executable exists
		File file = new File("./examples/dtlz2_stdio.exe");
				
		if (!file.exists()) {
			System.err.println("Please compile the executable by running make in the ./examples/ folder");
			return;
		}
		
		//configure and run this example problem
		try (Problem problem = new MyDTLZ2()) {
			Algorithm algorithm = new NSGAII(problem);
			algorithm.run(10000);
			algorithm.getResult().display();
		}
	}
	
}
