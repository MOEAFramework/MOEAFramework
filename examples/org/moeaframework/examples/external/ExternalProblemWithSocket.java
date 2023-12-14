/* Copyright 2009-2023 David Hadka
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

import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

/**
 * Demonstrates connecting to an external program using sockets for
 * communication.  For more details on setting up an external program, see
 * the {@link ExternalProblemWithStdio} example.
 * 
 * To run this example, first compile the C program by running the {@code make} command
 * in the {@code ./examples/} folder.  Then, run this Java example.
 */
public class ExternalProblemWithSocket {

	/**
	 * Notice that the only change is in the constructor, where the hostname and
	 * port are specified.
	 */
	public static class MyDTLZ2 extends ExternalProblem {

		public MyDTLZ2() throws IOException {
			super("localhost", ExternalProblem.DEFAULT_PORT);
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
	
	public static void main(String[] args) throws IOException, InterruptedException {
		//check if the executable exists
		File file = new File("./examples/dtlz2_socket.exe");
		
		if (!file.exists()) {
			if (!SystemUtils.IS_OS_UNIX) {
				System.err.println("This example only works on POSIX-compliant systems; see the Makefile for details");
				return;
			}
			
			System.err.println("Please compile the executable by running make in the ./examples/ folder");
			return;
		}
		
		//run the executable
		new ProcessBuilder(file.toString()).start();
		
		//wait a short period of time for the process to start and listen to the socket
		Thread.sleep(1000);
		
		//configure and run this example problem
		try (Problem problem = new MyDTLZ2()) {
			Algorithm algorithm = new NSGAII(problem);
			algorithm.run(10000);
			algorithm.getResult().display();
		}
	}
	
}
