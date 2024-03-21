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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests basic functionality without requiring an executable.
 */
public class ExternalProblemTest {
	
	private static final int TIMEOUT = 5000;
	
	private List<Exception> exceptions;
	
	@Before
	public void setUp() throws IOException {		
		exceptions = Collections.synchronizedList(new ArrayList<Exception>());
	}
	
	@After
	public void tearDown() {
		exceptions = null;
	}
	
	@Test
	public void testValidResponse() throws Exception {
		run(s -> "0.2 0.8 0.5");
	}
	
	@Test(expected = ProblemException.class)
	public void testTooFewResponses() throws Exception {
		run(s -> "0.2 0.8");
	}
	
	@Test(expected = ProblemException.class)
	public void testTooManyResponses() throws Exception {
		run(s -> "0.2 0.8 0.5 0.1");
	}
	
	@Test(expected = ProblemException.class)
	public void testUnparseableResponse() throws Exception {
		run(s -> "0.2 0.8foo 0.5");
	}
	
	@Test(expected = ProblemException.class)
	public void testEmptyResponse() throws Exception {
		run(s -> "");
	}
	
	@Test(expected = ProblemException.class)
	public void testNoResponse() throws Exception {
		run(s -> {
			throw new FrameworkException("test close with no response");
		});
	}
	
	private void run(Function<String, String> callback) throws Exception {
		try (PipedInputStream input1 = new PipedInputStream();
				PipedOutputStream output1 = new PipedOutputStream();
				PipedInputStream input2 = new PipedInputStream();
				PipedOutputStream output2 = new PipedOutputStream();
				ExternalProblem problem = createProblem(input1, output2)) {
			input1.connect(output1);
			input2.connect(output2);
			
			Thread producerThread = createProducerThread(problem);
			Thread consumerThread = createConsumerThread(input2, output1, callback);
			
			producerThread.start();
			consumerThread.start();
			
			producerThread.join(TIMEOUT);
			consumerThread.join(TIMEOUT);
			
			Assert.assertFalse(producerThread.isAlive());
			Assert.assertFalse(consumerThread.isAlive());
			
			// rethrow the first exception, if any, occuring in either thread
			if (!exceptions.isEmpty()) {
				throw exceptions.get(0);
			}
		}
	}
	
	private Thread createProducerThread(ExternalProblem problem) {
		return new Thread() {
			public void run() {
				try {
					for (int i=0; i<100; i++) {
						Solution solution = problem.newSolution();
						problem.evaluate(solution);
						Assert.assertEquals(0.2, solution.getObjective(0), Settings.EPS);
						Assert.assertEquals(0.8, solution.getObjective(1), Settings.EPS);
						Assert.assertEquals(0.5, solution.getConstraint(0), Settings.EPS);
					}
				} catch (Exception e) {
					exceptions.add(e);
				} finally {
					problem.close();
				}
			}
		};
	}
	
	private Thread createConsumerThread(PipedInputStream input, PipedOutputStream output, 
			Function<String, String> callback) {
		return new Thread() {
			public void run() {
				Pattern pattern = Pattern.compile("^[0-9]+\\.[0-9]+\\s+[0-9]+\\s+[0-1]{5}\\s+[0-9,]+$");
				
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						PrintStream writer = new PrintStream(output)) {
					String line = null;

					while ((line = reader.readLine()) != null) {
						Assert.assertTrue(pattern.matcher(line).matches());

						writer.println(callback.apply(line));
						writer.flush();
					}
				} catch (Exception e) {
					// suppress any exceptions
				}
			}
		};
	}
	
	private ExternalProblem createProblem(PipedInputStream input, PipedOutputStream output) {
		return new ExternalProblem(input, output) {

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
				solution.setVariable(0, new RealVariable(0.5, 0.0, 1.0));
				solution.setVariable(1, new BinaryIntegerVariable(5, 0, 10));
				solution.setVariable(2, new BinaryVariable(5));
				solution.setVariable(3, new Permutation(3));
				return solution;
			}
			
		};
	}

}
