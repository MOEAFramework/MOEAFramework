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
package org.moeaframework.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockUnsupportedVariable;

public class ExternalProblemWithCStdioTest {
	
	protected File file;
	
	protected ExternalProblem problem;
	
	protected BufferedReader debugReader;
	
	@Before
	public void setUp() throws IOException {
		TestUtils.assumeMakeExists();
		
		if (new File("src/test/resources").exists()) {
			file = new File("src/test/resources/org/moeaframework/problem/test_stdio.exe");
		} else {
			file = new File("test/org/moeaframework/problem/test_stdio.exe");
		}
		
		//attempt to run make if the file does not exist
		if (!file.exists()) {
			TestUtils.runMake(file.getParentFile());
		}

		TestUtils.assumeFileExists(file);
		
		//start the process separately to intercept the error (debug) data
		Process process = new ProcessBuilder(file.toString()).start();

		debugReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		problem = new ExternalProblem(process.getInputStream(), process.getOutputStream()) {

			@Override
			public String getName() {
				return "Test";
			}

			@Override
			public int getNumberOfVariables() {
				return 5;
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
				Solution solution = new Solution(5, 2, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				solution.setVariable(1, new RealVariable(-1e26, 1e26));
				solution.setVariable(2, new BinaryVariable(5));
				solution.setVariable(3, new BinaryIntegerVariable(5, 20));
				solution.setVariable(4, new Permutation(3));
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
		Initialization initialization = new RandomInitialization(problem);

		Solution[] solutions = initialization.initialize(TestThresholds.SAMPLES);
		
		for (int i=0; i<solutions.length; i++) {
			Solution solution = solutions[i];
			problem.evaluate(solution);
			
			//check objectives and constraints
			Assert.assertArrayEquals(new double[] { i+1, 1e-10/(i+1) }, solution.getObjectives(), Settings.EPS);
			Assert.assertArrayEquals(new double[] { 1e10*(i+1) }, solution.getConstraints(), Settings.EPS);
			
			//check the debug stream
			String debugLine = debugReader.readLine();
			
			Assert.assertNotNull(debugLine);
			
			String[] debugTokens = debugLine.split("\\s+");
			
			for (int j=0; j<2; j++) {
				Assert.assertEquals(((RealVariable)solution.getVariable(j)).getValue(), 
						Double.parseDouble(debugTokens[j]), Settings.EPS);
			}
			
			BinaryVariable bv = ((BinaryVariable)solution.getVariable(2));

			for (int j=0; j<bv.getNumberOfBits(); j++) {
				Assert.assertEquals(bv.get(j) ? 1 : 0, Integer.parseInt(debugTokens[2+j]));
			}
			
			BinaryIntegerVariable biv = ((BinaryIntegerVariable)solution.getVariable(3));
			
			Assert.assertEquals(biv.getValue(), Integer.parseInt(debugTokens[7]));
			
			Permutation p = ((Permutation)solution.getVariable(4));
			
			for (int j=0; j<p.size(); j++) {
				Assert.assertEquals(p.get(j), Integer.parseInt(debugTokens[8+j]));
			}
		}
	}
	
	@Test
	public void testIntToDoubleCompatibility() {
		Solution solution = problem.newSolution();
		solution.setVariable(1, new BinaryIntegerVariable(8, 5, 20));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingBinary() {
		Solution solution = problem.newSolution();
		solution.setVariable(2, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingInteger() {
		Solution solution = problem.newSolution();
		solution.setVariable(3, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}

	@Test(expected = ProblemException.class)
	public void testErrorParsingPermutation() {
		Solution solution = problem.newSolution();
		solution.setVariable(4, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingReal() {
		Solution solution = problem.newSolution();
		solution.setVariable(1, new Permutation(3));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewVariables() {
		Solution solution = new Solution(4, 2, 1);
		copy(solution, problem.newSolution(), 4);
		problem.evaluate(solution);
	}
	
	@Ignore("this case is not detected by the C/C++ library")
	@Test(expected = ProblemException.class)
	public void testErrorTooManyVariables() {
		Solution solution = new Solution(6, 2, 1);
		copy(solution, problem.newSolution(), 5);
		solution.setVariable(5, new RealVariable(0.5, 0.0, 1.0));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewObjectives() {
		Solution solution = new Solution(5, 1, 1);
		copy(solution, problem.newSolution(), 5);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooManyObjectives() {
		Solution solution = new Solution(5, 3, 1);
		copy(solution, problem.newSolution(), 5);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewConstraints() {
		Solution solution = new Solution(5, 2, 0);
		copy(solution, problem.newSolution(), 5);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooManyConstraints() {
		Solution solution = new Solution(5, 2, 2);
		copy(solution, problem.newSolution(), 5);
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorPermutationTooShort() {
		Solution solution = problem.newSolution();
		solution.setVariable(4, new Permutation(2));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorPermutationTooLong() {
		Solution solution = problem.newSolution();
		solution.setVariable(4, new Permutation(4));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorBinaryTooShort() {
		Solution solution = problem.newSolution();
		solution.setVariable(2, new BinaryVariable(4));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorBinaryTooLong() {
		Solution solution = problem.newSolution();
		solution.setVariable(2, new BinaryVariable(6));
		problem.evaluate(solution);
	}
	
	@Test(expected = ProblemException.class)
	public void testUnsupportedVariableType() {
		Solution solution = new Solution(5, 2, 1);
		copy(solution, problem.newSolution(), 4);
		solution.setVariable(4, new MockUnsupportedVariable());
		problem.evaluate(solution);
	}
	
	protected void copy(Solution s1, Solution s2, int size) {
		for (int i=0; i<size; i++) {
			s1.setVariable(i, s2.getVariable(i));
		}
	}

}
