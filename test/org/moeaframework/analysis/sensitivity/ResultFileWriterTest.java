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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.UnsupportedVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the {@link ResultFileWriter} class.
 */
public class ResultFileWriterTest {
	
	/**
	 * The problem used for testing.
	 */
	private Problem problem;
	
	/**
	 * A feasible solution.
	 */
	private Solution solution1;
	
	/**
	 * Another feasible solution.
	 */
	private Solution solution2;
	
	/**
	 * A solution violating its constraints.
	 */
	private Solution solution3;

	/**
	 * Creates the problem used for testing.
	 */
	@Before
	public void setUp() {
		problem = new AbstractProblem(3, 2, 1) {
			
			@Override
			public void evaluate(Solution solution) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(3, 2, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				solution.setVariable(1, new BinaryVariable(5));
				solution.setVariable(2, new Permutation(3));
				return solution;
			}
			
		};
		
		solution1 = problem.newSolution();
		((RealVariable)solution1.getVariable(0)).setValue(0.0);
		((BinaryVariable)solution1.getVariable(1)).set(2, true);
		((Permutation)solution1.getVariable(2)).swap(0, 2);
		solution1.setObjectives(new double[] { 0.0, 1.0 });
		
		solution2 = problem.newSolution();
		((RealVariable)solution1.getVariable(0)).setValue(1.0);
		((BinaryVariable)solution1.getVariable(1)).set(1, true);
		((Permutation)solution1.getVariable(2)).swap(0, 1);
		solution2.setObjectives(new double[] { 1.0, 0.0 });
		
		solution3 = problem.newSolution();
		((RealVariable)solution1.getVariable(0)).setValue(0.5);
		((BinaryVariable)solution1.getVariable(1)).set(1, true);
		((Permutation)solution1.getVariable(2)).swap(1, 2);
		solution3.setObjectives(new double[] { 0.5, 0.5 });
		solution3.setConstraints(new double[] { -1.0 });
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		problem = null;
		solution1 = null;
		solution2 = null;
		solution3 = null;
	}
	
	/**
	 * Tests if special characters are escaped correctly when writing property
	 * files.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testSpecialCharactersInProperties() throws IOException {
		File file = TestUtils.createTempFile();
		
		NondominatedPopulation population = new NondominatedPopulation();

		TypedProperties properties = new TypedProperties();
		properties.setString("\"'!@#$=:%^&*()\\\r\n//\t ", "\"'!@#$=:%^&*()\\\r\n//\t ");
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, properties));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			Assert.assertEquals(properties, reader.next().getProperties());
		}
	}
	
	/**
	 * Tests if {@code null} properties are written correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testNullProperties() throws IOException {
		File file = TestUtils.createTempFile();
		
		NondominatedPopulation population = new NondominatedPopulation();
		TypedProperties properties = new TypedProperties();
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, (TypedProperties)null));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			Assert.assertEquals(properties, reader.next().getProperties());
		}
	}
	
	/**
	 * Tests if empty properties are written correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testEmptyProperties() throws IOException {
		File file = TestUtils.createTempFile();
		
		NondominatedPopulation population = new NondominatedPopulation();
		TypedProperties properties = new TypedProperties();
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, properties));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			Assert.assertEquals(properties, reader.next().getProperties());
		}
	}
	
	/**
	 * Tests if the population and properties are written correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testNormal() throws IOException {
		File file = TestUtils.createTempFile();

		NondominatedPopulation population = new NondominatedPopulation();
		population.add(solution1);
		population.add(solution2);
		
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, properties));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			ResultEntry entry = reader.next();
			TestUtils.assertEquals(population, entry.getPopulation());
			Assert.assertEquals(properties, entry.getProperties());
		}
	}
	
	/**
	 * Tests if the population and properties are written correctly when
	 * writing decision variables is disabled.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testNoVariables() throws IOException {
		File file = TestUtils.createTempFile();

		NondominatedPopulation population = new NondominatedPopulation();
		population.add(solution1);
		population.add(solution2);
		
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file, false)) {
			writer.append(new ResultEntry(population, properties));
		}
		
		population.clear();
		population.add(new Solution(solution1.getObjectives()));
		population.add(new Solution(solution2.getObjectives()));
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			ResultEntry entry = reader.next();
			TestUtils.assertEquals(population, entry.getPopulation());
			Assert.assertEquals(properties, entry.getProperties());
		}
	}
	
	/**
	 * Tests if constraint violating solutions are not written, and that
	 * empty populations are written correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testConstrainedSolution() throws IOException {
		File file = TestUtils.createTempFile();

		NondominatedPopulation population = new NondominatedPopulation();
		population.add(solution3);
		
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, properties));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			ResultEntry entry = reader.next();
			Assert.assertEquals(0, entry.getPopulation().size());
			Assert.assertEquals(properties, entry.getProperties());
		}
	}

	/**
	 * Tests if result files with multiple entries are written correctly, and
	 * that writing can be resumed correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testResume() throws IOException {
		File file = TestUtils.createTempFile();

		NondominatedPopulation population = new NondominatedPopulation();
		population.add(solution1);
		population.add(solution2);
		
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");

		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			Assert.assertEquals(0, writer.getNumberOfEntries());
			writer.append(new ResultEntry(population, properties));
			writer.append(new ResultEntry(population, properties));
			Assert.assertEquals(2, writer.getNumberOfEntries());
		}

		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			Assert.assertEquals(2, writer.getNumberOfEntries());
			writer.append(new ResultEntry(population, properties));
			Assert.assertEquals(3, writer.getNumberOfEntries());
		}

		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			ResultEntry entry = null;

			Assert.assertTrue(reader.hasNext());
			entry = reader.next();
			TestUtils.assertEquals(population, entry.getPopulation());
			Assert.assertEquals(properties, entry.getProperties());
			
			Assert.assertTrue(reader.hasNext());
			entry = reader.next();
			TestUtils.assertEquals(population, entry.getPopulation());
			Assert.assertEquals(properties, entry.getProperties());
			
			Assert.assertTrue(reader.hasNext());
			entry = reader.next();
			TestUtils.assertEquals(population, entry.getPopulation());
			Assert.assertEquals(properties, entry.getProperties());
			
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testUnsupportedDecisionVariable() throws IOException {
		File file = TestUtils.createTempFile();
		
		problem = new AbstractProblem(2, 2, 1) {

			@Override
			public void evaluate(Solution solution) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(2, 2, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				solution.setVariable(1, new UnsupportedVariable());
				return solution;
			}
			
		};

		NondominatedPopulation population = new NondominatedPopulation();
		
		Solution solution = problem.newSolution();
		((RealVariable)solution.getVariable(0)).setValue(0.5);
		solution.setObjectives(new double[] { 0.0, 1.0 });
		population.add(solution);
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			writer.append(new ResultEntry(population, null));
		}
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			ResultEntry entry = reader.next();
			Assert.assertEquals(1, entry.getPopulation().size());
			Assert.assertArrayEquals(solution.getObjectives(), 
					entry.getPopulation().get(0).getObjectives(), Settings.EPS);
			Assert.assertEquals(solution.getVariable(0), 
					entry.getPopulation().get(0).getVariable(0));
		}
	}
	
	@Test
	public void testEncode() throws IOException {
		File file = TestUtils.createTempFile();
		
		try (ResultFileWriter writer = new ResultFileWriter(problem, file)) {
			RealVariable rv = new RealVariable(0.5, 0.0, 1.0);
			Assert.assertEquals("0.5", writer.encode(rv));
			Assert.assertFalse(writer.encode(rv).matches(".*\\s.*"));
			
			BinaryVariable bv = new BinaryVariable(5);
			bv.set(2, true);
			Assert.assertEquals("00100", writer.encode(bv));
			Assert.assertFalse(writer.encode(bv).matches(".*\\s.*"));
			
			Permutation p = new Permutation(5);
			Assert.assertEquals("0,1,2,3,4", writer.encode(p));
			Assert.assertFalse(writer.encode(p).matches(".*\\s.*"));
			
			Grammar g = new Grammar(5);
			//Assert.assertEquals("-", writer.encode(g));
			Assert.assertFalse(writer.encode(g).matches(".*\\s.*"));
		}
	}
	
}
