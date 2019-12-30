/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link ExternalProblem} class using the C/C++ executable.
 */
public class ExternalProblemWithCSocketTest extends 
ExternalProblemWithCStdioTest {
	
	@Before
	public void setUp() throws IOException {
		//skip this test if the machine is not POSIX compliant
		TestUtils.assumePOSIX();
		
		file = new File("./test/org/moeaframework/problem/test_socket.exe");
		
		//attempt to run make if the file does not exist
		if (!file.exists()) {
			TestUtils.runMake(file.getParentFile());
		}
		
		TestUtils.assumeFileExists(file);
		
		//start the process separately to intercept the error (debug) data
		Process process = new ProcessBuilder(file.toString()).start();

		//sleep to allow the external process to begin listening to the port
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//handle silently
		}

		debugReader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		problem = new ExternalProblem("localhost", 
				ExternalProblem.DEFAULT_PORT) {

			@Override
			public String getName() {
				return "Test";
			}

			@Override
			public int getNumberOfVariables() {
				return 4;
			}

			@Override
			public int getNumberOfObjectives() {
				return 2;
			}

			@Override
			public int getNumberOfConstraints() {
				return 1;
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(4, 2, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				solution.setVariable(1, new RealVariable(-1e26, 1e26));
				solution.setVariable(2, new BinaryVariable(5));
				solution.setVariable(3, new Permutation(3));
				return solution;
			}

		};
	}

}
