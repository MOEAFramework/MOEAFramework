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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link ExternalProblem} class without the need for an external
 * executable.  No error conditions are tested.
 */
public class ExternalProblemTest {
	
	private ExternalProblem problem;
	
	private PipedInputStream i1;
	private PipedOutputStream o1;
	
	private PipedInputStream i2;
	private PipedOutputStream o2;
	
	@Before
	public void setUp() throws IOException {
		i1 = new PipedInputStream();
		o1 = new PipedOutputStream();
		i1.connect(o1);
		
		i2 = new PipedInputStream();
		o2 = new PipedOutputStream();
		i2.connect(o2);
		
		problem = new ExternalProblem(i1, o2) {

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
				solution.setVariable(1, new RealVariable(0.5, 0.0, 1.0));
				solution.setVariable(2, new BinaryVariable(5));
				solution.setVariable(3, new Permutation(3));
				return solution;
			}
			
		};
	}
	
	@After
	public void tearDown() {
		i1 = null;
		o1 = null;
		i2 = null;
		o2 = null;
		problem = null;
	}
	
	@Test
	public void testNormalUse() throws Exception {
		run(new Thread() {
			public void run() {
				BufferedReader reader = null;
				PrintStream writer = null;
				
				try {
					String line = null;
					reader = new BufferedReader(new InputStreamReader(i2));
					writer = new PrintStream(o1);
	
					while ((line = reader.readLine()) != null) {
						String[] tokens = line.split("\\s+");
	
						Assert.assertEquals(4, tokens.length);
	
						writer.println("0.2 0.8 0.5");
						writer.flush();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
						
						if (writer != null) {
							writer.close();
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
	}
	
	public void run(Thread consumerThread) throws Exception {
		Thread producerThread = new Thread() {
			public void run() {
				for (int i=0; i<100; i++) {
					Solution solution = problem.newSolution();
					problem.evaluate(solution);
					Assert.assertEquals(0.2, solution.getObjective(0), Settings.EPS);
					Assert.assertEquals(0.8, solution.getObjective(1), Settings.EPS);
					Assert.assertEquals(0.5, solution.getConstraint(0), Settings.EPS);
				}
				
				problem.close();
			}
		};
		
		producerThread.start();
		consumerThread.start();
		
		Thread.sleep(5000);
		
		Assert.assertFalse(producerThread.isAlive());
		Assert.assertFalse(consumerThread.isAlive());
	}

}
