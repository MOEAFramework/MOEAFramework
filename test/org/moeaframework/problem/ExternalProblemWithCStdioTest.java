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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.Set;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Make;
import org.moeaframework.TestResources;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.Initialization;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.mock.MockUnsupportedVariable;
import org.moeaframework.problem.ExternalProblem.Builder;

public class ExternalProblemWithCStdioTest {

	protected File getExecutable(String filename) {
		try {
			File file = TestResources.getLocalTestResource("org/moeaframework/problem/" + filename);
			
			if (!file.exists()) {
				Assume.assumeMakeExists();
				Make.runMake(file.getParentFile());
			}
	
			Assume.assumeFileExists(file);
			return file;
		} catch (IOException e) {
			Assume.assumeNoException(e);
			return null;
		}
	}
	
	public Builder createBuilder() {
		File executable = getExecutable("test_stdio.exe");
		
		return new Builder()
				.withCommand(executable.toString())
				.withDebugging();
	}
	
	@Test
	public void test() throws IOException {
		Builder builder = createBuilder();
		
		try (PipedOutputStream out = new PipedOutputStream(); PipedInputStream in = new PipedInputStream(out)) {
			builder.redirectErrorTo(out);
			
			try (BufferedReader debugReader = new BufferedReader(new InputStreamReader(in));
					TestExternalProblem problem = new TestExternalProblem(builder)) {
				Initialization initialization = new RandomInitialization(problem);
		
				Solution[] solutions = initialization.initialize(100);
				
				for (int i=0; i<solutions.length; i++) {					
					Solution solution = solutions[i];
					problem.evaluate(solution);
					
					//check objectives and constraints
					Assert.assertArrayEquals(new double[] { i+1, 1e-10/(i+1) }, solution.getObjectiveValues(), TestThresholds.HIGH_PRECISION);
					Assert.assertArrayEquals(new double[] { 1e10*(i+1) }, solution.getConstraintValues(), TestThresholds.HIGH_PRECISION);
					
					//check the debug stream
					String debugLine = debugReader.readLine();
					
					Assert.assertNotNull(debugLine);
					System.out.println(debugLine);
					
					String[] debugTokens = debugLine.split("\\s+");
					int tokenIndex = 0;
					
					for (int j=0; j<2; j++) {
						Assert.assertEquals(((RealVariable)solution.getVariable(j)).getValue(), 
								Double.parseDouble(debugTokens[tokenIndex++]), TestThresholds.HIGH_PRECISION);
					}
					
					BinaryVariable bv = ((BinaryVariable)solution.getVariable(2));
		
					for (int j=0; j<bv.getNumberOfBits(); j++) {
						Assert.assertEquals(bv.get(j) ? 1 : 0, Integer.parseInt(debugTokens[tokenIndex++]));
					}
					
					BinaryIntegerVariable biv = ((BinaryIntegerVariable)solution.getVariable(3));
					
					Assert.assertEquals(biv.getValue(), Integer.parseInt(debugTokens[tokenIndex++]));
					
					Permutation p = ((Permutation)solution.getVariable(4));
					
					for (int j=0; j<p.size(); j++) {
						Assert.assertEquals(p.get(j), Integer.parseInt(debugTokens[tokenIndex++]));
					}
					
					Subset s = ((Subset)solution.getVariable(5));
					Set<Integer> set = s.getSet();
					int setSize = set.size();
					
					Assert.assertEquals(setSize, Integer.parseInt(debugTokens[tokenIndex++]));
					
					for (int j=0; j<setSize; j++) {
						Assert.assertTrue(set.remove(Integer.parseInt(debugTokens[tokenIndex++])));
					}
										
					Assert.assertEquals("Elements not found in set: " + set, 0, set.size());
				}
				
				validateClose(problem);
			}
		}
	}
	
	@Test
	public void testMissingExecutable() {
		Builder builder = new Builder().withCommand("test_not_exists.exe");
		
		try (TestExternalProblem problem = new TestExternalProblem(builder)) {
			Assert.assertEquals(6, problem.getNumberOfVariables());
			Assert.assertEquals(2, problem.getNumberOfObjectives());
			Assert.assertEquals(1, problem.getNumberOfConstraints());
			
			Solution solution = problem.newSolution();
			Assert.assertNotNull(solution);
			
			Assert.assertThrows(ProblemException.class, () -> problem.evaluate(solution));
		}
	}
	
	@Test
	public void testIntToDoubleCompatibility() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(1, new BinaryIntegerVariable(8, 5, 20));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingBinary() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(2, new RealVariable(0.5, 0.0, 1.0));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingInteger() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(3, new RealVariable(0.5, 0.0, 1.0));
			problem.evaluate(solution);
		}
	}

	@Test(expected = ProblemException.class)
	public void testErrorParsingPermutation() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(4, new RealVariable(0.5, 0.0, 1.0));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingSubset() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(5, new RealVariable(0.5, 0.0, 1.0));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorParsingReal() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(1, new Permutation(3));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewVariables() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(4, 2, 1);
			copy(solution, problem.newSolution(), 4);
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooManyVariables() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(6, 2, 1);
			copy(solution, problem.newSolution(), 5);
			solution.setVariable(5, new RealVariable(0.5, 0.0, 1.0));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewObjectives() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(5, 1, 1);
			copy(solution, problem.newSolution(), 5);
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooManyObjectives() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(5, 3, 1);
			copy(solution, problem.newSolution(), 5);
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooFewConstraints() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(5, 2, 0);
			copy(solution, problem.newSolution(), 5);
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorTooManyConstraints() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(5, 2, 2);
			copy(solution, problem.newSolution(), 5);
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorPermutationTooShort() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(4, new Permutation(2));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorSubsetTooShort() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(5, new Subset(0, 0));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorPermutationTooLong() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(4, new Permutation(4));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorSubsetTooLong() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(5, new Subset(4, 4));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorBinaryTooShort() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(2, new BinaryVariable(4));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testErrorBinaryTooLong() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = problem.newSolution();
			solution.setVariable(2, new BinaryVariable(6));
			problem.evaluate(solution);
		}
	}
	
	@Test(expected = ProblemException.class)
	public void testUnsupportedVariableType() {
		try (TestExternalProblem problem = new TestExternalProblem(createBuilder())) {
			Solution solution = new Solution(5, 2, 1);
			copy(solution, problem.newSolution(), 4);
			solution.setVariable(4, new MockUnsupportedVariable());
			problem.evaluate(solution);
		}
	}
	
	protected void copy(Solution s1, Solution s2, int size) {
		for (int i=0; i<size; i++) {
			s1.setVariable(i, s2.getVariable(i));
		}
	}
	
	protected void validateClose(TestExternalProblem problem) {
		Process process = problem.getInstance().getProcess();
		Socket socket = problem.getInstance().getSocket();
		
		if (process != null) {
			Assert.assertTrue(process.isAlive());
		}
		
		if (socket != null) {
			Assert.assertTrue(socket.isConnected());
			Assert.assertFalse(socket.isClosed());
		}
		
		problem.close();
		
		if (process != null) {
			Assert.assertFalse(process.isAlive());
		}
		
		if (socket != null) {
			Assert.assertTrue(socket.isClosed());
		}
	}
	
	protected class TestExternalProblem extends ExternalProblem {
		
		public TestExternalProblem(Builder builder) {
			super(builder);
		}

		@Override
		public String getName() {
			return "Test";
		}

		@Override
		public int getNumberOfVariables() {
			return 6;
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
			Solution solution = new Solution(6, 2, 1);
			solution.setVariable(0, new RealVariable(0.0, 1.0));
			solution.setVariable(1, new RealVariable(-1e26, 1e26));
			solution.setVariable(2, new BinaryVariable(5));
			solution.setVariable(3, new BinaryIntegerVariable(5, 20));
			solution.setVariable(4, new Permutation(3));
			solution.setVariable(5, new Subset(1, 3, 5));
			return solution;
		}

		public Instance getInstance() {
			return instance;
		}

	}

}
