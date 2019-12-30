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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Tests the {@link ResultFileReader} class.
 */
public class ResultFileReaderTest {
	
	/**
	 * A valid result file.
	 */
	public static final String COMPLETE = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" + 
			"#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file with extra whitespace on lines.
	 */
	public static final String COMPLETE_WHITESPACE = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"  0.0 00100 2,1,0 0.0 1.0\n" + 
			"\t\t1.0 01000 1,0,2 1.0 0.0\n" + 
			"#\n" + 
			"0.0 00100    2,1,0 \t 0.0 1.0\n" + 
			"\t 1.0 01000 1,0,2 1.0 0.0 \t\n" +
			"#\n";
	
	/**
	 * A valid result file but without decision variables.
	 */
	public static final String COMPLETE_NOVARIABLES = 
			"# Problem = Test\n" +
			"# Objectives = 2\n" + 
			"0.0 1.0\n" + 
			"1.0 0.0\n" + 
			"#\n" + 
			"0.0 1.0\n" + 
			"1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file but without the header.
	 */
	public static final String COMPLETE_NOHEADER = 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file with multiple pound characters separating entries
	 */
	public static final String MULTIPOUND = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" + 
			"#\n#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n#\n";
	
	/**
	 * A valid result file with properties.
	 */
	public static final String COMPLETE_PROPERTIES = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"//foo=bar\n" +
			"//answer=42\n" +
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file with no properties.
	 */
	public static final String NO_PROPERTIES = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file with empty properties.
	 */
	public static final String EMPTY_PROPERTIES = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" +
			"//\n" +
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";
	
	/**
	 * A valid result file with the old style properties.
	 */
	public static final String OLDSTYLE_PROPERTIES = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"// foo\n" +
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n";

	/**
	 * An empty, but valid, result file.
	 */
	public static final String EMPTY = "";

	/**
	 * A valid result file with empty entries.
	 */
	public static final String EMPTY_ENTRY = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n" + 
			"//\n" + 
			"#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#";

	/**
	 * A valid result file containing just the header and no entries.
	 */
	public static final String ONLY_HEADER = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n";

	/**
	 * An incomplete result file, missing the trailing # character.
	 */
	public static final String INCOMPLETE1 = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n";

	/**
	 * An incomplete result file, containing an empty line.
	 */
	public static final String INCOMPLETE2 = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"\n" +
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#";

	/**
	 * An incomplete result file, containing an incomplete entry.
	 */
	public static final String INCOMPLETE3 = 
			"# Problem = Test\n" +
			"# Variables = 3\n" +
			"# Objectives = 2\n" + 
			"0.0 00100 2,1,0 0.0 1.0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#\n" + 
			"0.0 00100 2,1,0\n" + 
			"1.0 01000 1,0,2 1.0 0.0\n" +
			"#";

	/**
	 * An incomplete result file, containing unparseable data.
	 */
	public static final String INCOMPLETE4 = 
		"# Problem = Test\n" +
		"# Variables = 3\n" +
		"# Objectives = 2\n" + 
		"0.0 00100 2,1,0 0.0 1.0\n" + 
		"1.0 01000 1,0,2 1.0 0.0\n" +
		"#\n" + 
		"0.0 00100 2,1,0 0.0foo 1.0\n" + 
		"1.0 01000 1,0,2 1.0 0.0\n" +
		"#";
	
	/**
	 * The problem used for testing.
	 */
	private Problem problem;
	
	/**
	 * The expected result from reading a complete input.
	 */
	private Population population;

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

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		problem = null;
		population = null;
	}

	/**
	 * Tests if a valid result file is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderComplete() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					COMPLETE));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with extra whitespace on lines is read
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderCompleteWhitespace() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					COMPLETE_WHITESPACE));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file containing no decision variables is read 
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderCompleteNoVariables() throws IOException {
		ResultFileReader reader = null;
		

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					COMPLETE_NOVARIABLES));
			validateCompleteNoVariables(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file containing no header lines is read 
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderCompleteNoHeader() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					COMPLETE_NOHEADER));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with multiple {@code #} characters
	 * separating entries is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderMultipound() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					MULTIPOUND));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with properties is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderCompleteProperties() throws IOException {
		ResultFileReader reader = null;
		
		Properties properties = new Properties();
		properties.setProperty("foo", "bar");
		properties.setProperty("answer", "42");

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					COMPLETE_PROPERTIES));
			validateProperties(reader, properties);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with no properties is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderNoProperties() throws IOException {
		ResultFileReader reader = null;
		
		Properties properties = new Properties();

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					NO_PROPERTIES));
			validateProperties(reader, properties);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with empty properties is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderEmptyProperties() throws IOException {
		ResultFileReader reader = null;
		
		Properties properties = new Properties();

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					EMPTY_PROPERTIES));
			validateProperties(reader, properties);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if a valid result file with the old style comment is read
	 * correctly.  The old comment is meaningless, but this is to test
	 * backwards compatibility to ensure the remainder is still processed.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderOldStyleProperties() throws IOException {
		ResultFileReader reader = null;
		
		Properties properties = new Properties();
		properties.setProperty("foo", "");

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					OLDSTYLE_PROPERTIES));
			validateProperties(reader, properties);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if a valid result file containing no content is read correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderEmpty() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					EMPTY));
			validateEmpty(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if a valid result file with an empty entry is handled correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderEmptyEntry() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					EMPTY_ENTRY));
			validateEmptyEntry(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if a valid result file containing only the header is read
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderOnlyHeader() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					ONLY_HEADER));
			validateEmpty(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if an incomplete result file missing the trailing {@code #}
	 * character is handled correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderIncomplete1() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					INCOMPLETE1));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if an invalid result file containing a blank line is handled
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderIncomplete2() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					INCOMPLETE2));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests if an invalid result file with a partial line is handled correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderIncomplete3() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					INCOMPLETE3));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Tests if an invalid result file containing unparseable data is handled
	 * correctly.
	 * 
	 * @throws IOException should not occur
	 */
	@Test
	public void testReaderIncomplete4() throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, TestUtils.createTempFile(
					INCOMPLETE4));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Validates a complete result file.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateComplete(ResultFileReader reader) throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}
	
	/**
	 * Validates a complete result file that does not contain decision 
	 * variables.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateCompleteNoVariables(ResultFileReader reader) 
	throws IOException {
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

	/**
	 * Validates an empty result file.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateEmpty(ResultFileReader reader) throws IOException {
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	/**
	 * Validates a result file missing an entry.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateEmptyEntry(ResultFileReader reader) 
	throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertTrue(reader.hasNext());
		Assert.assertEquals(0, reader.next().getPopulation().size());
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	/**
	 * Validates an incomplete result file.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateIncomplete(ResultFileReader reader) 
	throws IOException {
		Assert.assertTrue(reader.hasNext());
		TestUtils.assertEquals(population, reader.next().getPopulation());
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}
	
	/**
	 * Validates properties.
	 * 
	 * @param reader the result reader
	 * @throws IOException should not occur
	 */
	private void validateProperties(ResultFileReader reader, 
			Properties properties) throws IOException {
		while (reader.hasNext()) {
			Assert.assertEquals(properties, reader.next().getProperties());
		}
	}
	
	@Test
	public void testDecode() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);
			
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
			
			//grammars technically have valid encodings, but this tests the
			//unsupported decision variable type entry
			Grammar g = new Grammar(5);
			reader.decode(g, "-");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = NumberFormatException.class)
	public void testDecodeInvalidReal() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);
			
			RealVariable rv = new RealVariable(0.0, 1.0);
			reader.decode(rv, "0.5foo");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testDecodeInvalidBinary1() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);

			BinaryVariable bv = new BinaryVariable(5);
			reader.decode(bv, "001");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testDecodeInvalidBinary2() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);

			BinaryVariable bv = new BinaryVariable(5);
			reader.decode(bv, "00200");
			Assert.assertEquals(1, bv.cardinality());
			Assert.assertTrue(bv.get(2));
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testDecodeInvalidPermutation1() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);

			Permutation p = new Permutation(5);
			reader.decode(p, "2,0,1");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testDecodeInvalidPermutation2() throws IOException {
		File file = TestUtils.createTempFile();
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(problem, file);

			Permutation p = new Permutation(5);
			reader.decode(p, "2,0,1,5,3");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
}
