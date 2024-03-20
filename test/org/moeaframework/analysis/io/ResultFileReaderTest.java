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
package org.moeaframework.analysis.io;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.TypedProperties;

public class ResultFileReaderTest {
	
	public static final String COMPLETE = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String COMPLETE_WHITESPACE = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			  0.0 00100 2,1,0 0.0 1.0
			\t\t1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100    2,1,0 \t 0.0 1.0
			\t 1.0 01000 1,0,2 1.0 0.0 \t
			#
			""";
	
	public static final String COMPLETE_NOVARIABLES = """
			# Problem = Test
			# Objectives = 2
			0.0 1.0
			1.0 0.0
			#
			0.0 1.0 
			1.0 0.0
			#
			""";
	
	public static final String COMPLETE_NOHEADER = """
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String MULTIPOUND = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			#
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			#
			""";
	
	public static final String COMPLETE_PROPERTIES = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			//foo=bar
			//answer=42
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";
	
	public static final String NO_PROPERTIES = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";
	
	public static final String EMPTY_PROPERTIES = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			//
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String OLDSTYLE_PROPERTIES = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			// foo
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String EMPTY = "";

	public static final String EMPTY_ENTRY = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			//
			#
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String ONLY_HEADER = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			""";

	public static final String INCOMPLETE_MISSING_POUND = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100 2,1,0 0.0 1.0
			""";

	public static final String INCOMPLETE_EMPTY_LINE = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100 2,1,0 0.0 1.0
			
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String INCOMPLETE_MISSING_DATA = """
			# Problem = Test
			# Variables = 3
			# Objectives = 2
			0.0 00100 2,1,0 0.0 1.0
			1.0 01000 1,0,2 1.0 0.0
			#
			0.0 00100 2,1,0
			1.0 01000 1,0,2 1.0 0.0
			#
			""";

	public static final String INCOMPLETE_UNPARSEABLE = """
		# Problem = Test
		# Variables = 3
		# Objectives = 2
		0.0 00100 2,1,0 0.0 1.0
		1.0 01000 1,0,2 1.0 0.0
		#
		0.0 00100 2,1,0 0.0foo 1.0
		1.0 01000 1,0,2 1.0 0.0
		#
		""";
	
	private Problem problem;
	private Population population;

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
		
		Solution solution1 = problem.newSolution();
		((RealVariable)solution1.getVariable(0)).setValue(0.0);
		((BinaryVariable)solution1.getVariable(1)).set(2, true);
		((Permutation)solution1.getVariable(2)).swap(0, 2);
		solution1.setObjectives(new double[] { 0.0, 1.0 });
		
		Solution solution2 = problem.newSolution();
		((RealVariable)solution2.getVariable(0)).setValue(1.0);
		((BinaryVariable)solution2.getVariable(1)).set(1, true);
		((Permutation)solution2.getVariable(2)).swap(0, 1);
		solution2.setObjectives(new double[] { 1.0, 0.0 });
		
		population = new Population();
		population.add(solution1);
		population.add(solution2);
	}

	@After
	public void tearDown() {
		problem = null;
		population = null;
	}

	@Test
	public void testReaderComplete() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(COMPLETE))) {
			validateComplete(reader);
		}
	}
	
	@Test
	public void testReaderCompleteWhitespace() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(COMPLETE_WHITESPACE))) {
			validateComplete(reader);
		}
	}
	
	@Test
	public void testReaderCompleteNoVariables() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(COMPLETE_NOVARIABLES))) {
			validateCompleteNoVariables(reader);
		}
	}

	@Test
	public void testReaderCompleteNoHeader() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(COMPLETE_NOHEADER))) {
			validateComplete(reader);
		}
	}
	
	@Test
	public void testReaderMultipound() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(MULTIPOUND))) {
			validateComplete(reader);
		}
	}
	
	@Test
	public void testReaderCompleteProperties() throws IOException {
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		properties.setString("answer", "42");

		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(COMPLETE_PROPERTIES))) {
			validateProperties(reader, properties);
		}
	}
	
	@Test
	public void testReaderNoProperties() throws IOException {
		TypedProperties properties = new TypedProperties();

		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(NO_PROPERTIES))) {
			validateProperties(reader, properties);
		}
	}

	@Test
	public void testReaderEmptyProperties() throws IOException {
		TypedProperties properties = new TypedProperties();

		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(EMPTY_PROPERTIES))) {
			validateProperties(reader, properties);
		}
	}
	
	@Test
	public void testReaderOldStyleProperties() throws IOException {
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "");

		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(OLDSTYLE_PROPERTIES))) {
			validateProperties(reader, properties);
		}
	}

	@Test
	public void testReaderEmptyFile() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(EMPTY))) {
			validateEmpty(reader);
		}
	}

	@Test
	public void testReaderEmptyEntry() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(EMPTY_ENTRY))) {
			validateEmptyEntry(reader);
		}
	}

	@Test
	public void testReaderOnlyHeader() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(ONLY_HEADER))) {
			validateEmpty(reader);
		}
	}

	@Test
	public void testReaderMissingPound() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(INCOMPLETE_MISSING_POUND))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testReaderEmptyLine() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(INCOMPLETE_EMPTY_LINE))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testReaderMissingData() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(INCOMPLETE_MISSING_DATA))) {
			validateIncomplete(reader);
		}
	}
	
	@Test
	public void testReaderUnparseable() throws IOException {
		try (ResultFileReader reader = new ResultFileReader(problem, TestUtils.createTempFile(INCOMPLETE_UNPARSEABLE))) {
			validateIncomplete(reader);
		}
	}

	private void validateComplete(ResultFileReader reader) throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}
	
	private void validateCompleteNoVariables(ResultFileReader reader) throws IOException {
		population.clear();
		population.add(new Solution(new double[] { 0.0, 1.0 }));
		population.add(new Solution(new double[] { 1.0, 0.0 }));
		
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateEmpty(ResultFileReader reader) throws IOException {
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateEmptyEntry(ResultFileReader reader) throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertTrue(reader.hasNext());
		Assert.assertEquals(0, reader.next().getPopulation().size());
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateIncomplete(ResultFileReader reader) throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateProperties(ResultFileReader reader, TypedProperties properties) throws IOException {
		while (reader.hasNext()) {
			Assert.assertEquals(properties, reader.next().getProperties());
		}
	}
	
	@Test
	public void testDecode() throws IOException {
		File file = TestUtils.createTempFile();
		
		try (ResultFileReader reader = new ResultFileReader(problem, file)) {
			RealVariable rv = new RealVariable(0.0, 1.0);
			reader.decode(rv, "0.5");
			Assert.assertEquals(0.5, rv.getValue(), Settings.EPS);
			
			BinaryVariable bv = new BinaryVariable(5);
			reader.decode(bv, "00100");
			Assert.assertEquals(1, bv.cardinality());
			Assert.assertTrue(bv.get(2));
			
			Permutation p = new Permutation(5);
			reader.decode(p, "2,0,1,4,3");
			Assert.assertArrayEquals(new int[] { 2, 0, 1, 4, 3 }, p.toArray());
			
			//grammars technically have valid encodings, but this tests the unsupported decision variable type entry
			Grammar g = new Grammar(5);
			reader.decode(g, "-");
		}
	}
	
}
