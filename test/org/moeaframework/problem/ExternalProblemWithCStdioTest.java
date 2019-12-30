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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link ExternalProblem} class using the C/C++ executable.
 */
public class ExternalProblemWithCStdioTest {
	
	protected File file;
	
	protected ExternalProblem problem;
	
	protected BufferedReader debugReader;
	
	@Before
	public void setUp() throws IOException {
		file = new File("./test/org/moeaframework/problem/test_stdio.exe");
		
		//attempt to run make if the file does not exist
		if (!file.exists()) {
			TestUtils.runMake(file.getParentFile());
		}

		TestUtils.assumeFileExists(file);
		
		//start the process separately to intercept the error (debug) data
		Process process = new ProcessBuilder(file.toString()).start();

		debugReader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		problem = new ExternalProblem(process.getInputStream(), 
				process.getOutputStream()) {

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
	
	@After
	public void tearDown() throws IOException {
		file = null;
		
		if (problem != null) {
			problem.close();
			problem = null;
		}
		
		if (debugReader != null) {
			debugReader.close();
			debugReader = null;
		}
	}
	
	@Test
	public void test() throws IOException {
		Initialization initialization = new RandomInitialization(problem, 
				TestThresholds.SAMPLES);

		Solution[] solutions = initialization.initialize();
		
		for (int i=0; i<solutions.length; i++) {
			Solution solution = solutions[i];
			problem.evaluate(solution);
			
			//check objectives and constraints
			Assert.assertArrayEquals(new double[] { i+1, 1e-10/(i+1) }, 
					solution.getObjectives(), Settings.EPS);
			Assert.assertArrayEquals(new double[] { 1e10*(i+1) }, 
					solution.getConstraints(), Settings.EPS);
			
			//check the debug stream
			String debugLine = debugReader.readLine();
			
			Assert.assertNotNull(debugLine);
			
			String[] debugTokens = debugLine.split("\\s+");
			
			for (int j=0; j<2; j++) {
				Assert.assertEquals(
						((RealVariable)solution.getVariable(j)).getValue(), 
						Double.parseDouble(debugTokens[j]), Settings.EPS);
			}
			
			BinaryVariable bv = ((BinaryVariable)solution.getVariable(2));

			for (int j=0; j<bv.getNumberOfBits(); j++) {
				Assert.assertEquals(bv.get(j) ? 1 : 0, 
						Integer.parseInt(debugTokens[2+j]));
			}
			
			Permutation p = ((Permutation)solution.getVariable(3));
			
			for (int j=0; j<p.size(); j++) {
				Assert.assertEquals(p.get(j), 
						Integer.parseInt(debugTokens[7+j]));
			}
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testError1() {
		Solution solution = problem.newSolution();
		solution.setVariable(2, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testError2() {
		Solution solution = problem.newSolution();
		solution.setVariable(3, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testError3() {
		Solution solution = problem.newSolution();
		solution.setVariable(1, new Permutation(3));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testError4() {
		Solution solution = new Solution(1, 2, 1);
		copy(solution, problem.newSolution(), 1);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testError5() {
		Solution solution = new Solution(2, 2, 1);
		copy(solution, problem.newSolution(), 2);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testError6() {
		Solution solution = new Solution(3, 2, 1);
		copy(solution, problem.newSolution(), 3);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testReturnLength() {
		Solution solution = new Solution(4, 2, 2);
		copy(solution, problem.newSolution(), 4);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testUnsupportedVariableType() {
		Solution solution = new Solution(4, 2, 2);
		copy(solution, problem.newSolution(), 3);
		solution.setVariable(3, new Variable() {

			private static final long serialVersionUID = 7614517658356868257L;

			@Override
			public Variable copy() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void randomize() {
				throw new UnsupportedOperationException();
			}
			
		});
		problem.evaluate(solution);
	}
	
	protected void copy(Solution s1, Solution s2, int size) {
		for (int i=0; i<size; i++) {
			s1.setVariable(i, s2.getVariable(i));
		}
	}

}
