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
package org.moeaframework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.CommentedLineReader;

public class Assert extends org.junit.Assert {

	private Assert() {
		super();
	}

	public static void assertEqualsNormalized(String expected, String actual) {
		assertEquals("The strings differ after normalization:",
				StringUtils.normalizeSpace(expected), StringUtils.normalizeSpace(actual));
	}
	
	public static void assertEquals(double[][] expected, double[][] actual) {
		assertEquals(new Array2DRowRealMatrix(expected), new Array2DRowRealMatrix(actual));
	}
	
	public static void assertEquals(RealMatrix expected, RealMatrix actual) {
		assertEquals(expected, actual, new AbsoluteError(TestThresholds.HIGH_PRECISION));
	}
	
	public static void assertEquals(double expected, double actual) {
		assertEquals(expected, actual, TestThresholds.HIGH_PRECISION);
	}
	
	public static void assertEquals(double expected, double actual, FloatingPointError epsilon) {
		epsilon.assertEquals(expected, actual);
	}

	public static void assertEquals(RealMatrix expected, RealMatrix actual, FloatingPointError epsilon) {
		assertEquals("The matrices have different number of rows:",
				expected.getRowDimension(), actual.getRowDimension());
		
		assertEquals("The matrices have different number of columns:",
				expected.getColumnDimension(), actual.getColumnDimension());
		
		for (int i = 0; i < expected.getRowDimension(); i++) {
			for (int j = 0; j < expected.getColumnDimension(); j++) {
				epsilon.assertEquals(expected.getEntry(i, j), actual.getEntry(i, j));
			}
		}
	}

	public static void assertEquals(Population expected, Population actual) {
		org.junit.Assert.assertEquals("The populations have different sizes:", expected.size(), actual.size());

		BitSet expectedMatches = new BitSet(expected.size());
		BitSet actualMatches = new BitSet(actual.size());

		for (int i = 0; i < expected.size(); i++) {
			for (int j = 0; j < actual.size(); j++) {
				try {
					assertEquals(expected.get(i), actual.get(j));
					expectedMatches.set(i);
					actualMatches.set(j);
				} catch (AssertionError e) {
					// do nothing, solutions are not equal
				}
			}
		}
				
		assertEquals("The expected population contains solutions not found in the actual population:",
				expected.size(), expectedMatches.cardinality());
		
		assertEquals("The actual population contains solutions not found in the expected population:",
				actual.size(), actualMatches.cardinality());
	}
	
	public static void assertNoNaN(Solution solution) {
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			if (solution.getVariable(i) instanceof RealVariable realVariable && Double.isNaN(realVariable.getValue())) {
				fail("Solution has NaN for decision variable at index " + i);
			}
		}
		
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			if (Double.isNaN(solution.getObjective(i))) {
				fail("Solution has NaN for objective value at index " + i);
			}
		}
		
		for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
			if (Double.isNaN(solution.getConstraint(i))) {
				fail("Solution has NaN for constraint value at index " + i);
			}
		}
	}
	
	public static void assertEquals(Solution expected, Solution actual) {
		assertEquals("Solutions have different number of variables:",
				expected.getNumberOfVariables(), actual.getNumberOfVariables());

		assertEquals("Solutions have different number of objectives:",
				expected.getNumberOfObjectives(), actual.getNumberOfObjectives());
		
		assertEquals("Solutions have different number of constraints:",
				expected.getNumberOfConstraints(), actual.getNumberOfConstraints());

		for (int i = 0; i < expected.getNumberOfVariables(); i++) {
			assertEquals("Solutions have different variables at index " + i + ":",
					expected.getVariable(i), actual.getVariable(i));
		}

		for (int i = 0; i < expected.getNumberOfObjectives(); i++) {
			assertEquals("Solutions have different objective values at index " + i + ":",
					expected.getObjective(i), actual.getObjective(i), TestThresholds.LOW_PRECISION);
		}

		for (int i = 0; i < expected.getNumberOfConstraints(); i++) {
			assertEquals("Solutions have different constraint values at index " + i + ":",
					expected.getConstraint(i), actual.getConstraint(i), TestThresholds.LOW_PRECISION);
		}
		
		// We do not check attributes here because (1) attributes are generally specific to a solution and can differ
		// even if all other values are identical; and (2) identity checks will fail if arrays or other objects are
		// stored as attributes.
	}
	
	public static void assertGreaterThan(double lhs, double rhs) {
		MatcherAssert.assertThat(lhs, Matchers.greaterThan(rhs));
	}
	
	public static void assertGreaterThanOrEqual(double lhs, double rhs) {
		MatcherAssert.assertThat(lhs, Matchers.greaterThanOrEqualTo(rhs));
	}
	
	public static void assertLessThan(double lhs, double rhs) {
		MatcherAssert.assertThat(lhs, Matchers.lessThan(rhs));
	}
	
	public static void assertLessThanOrEqual(double lhs, double rhs) {
		MatcherAssert.assertThat(lhs, Matchers.lessThanOrEqualTo(rhs));
	}
	
	public static void assertBetween(double lowerBound, double upperBound, double value) {
		MatcherAssert.assertThat(value, Matchers.allOf(Matchers.greaterThanOrEqualTo(lowerBound),
				Matchers.lessThanOrEqualTo(upperBound)));
	}
	
	public static void assertBlank(String line) {
		assertTrue("Line is not blank: '" + line + "'", line.isBlank());
	}
	
	public static void assertMatches(String line, String regex) {
		assertTrue("Line does not match regex: '" + line + "'", line.matches(regex));
	}
	
	public static void assertMatches(String line, Pattern regex) {
		assertTrue("Line does not match regex: '" + line + "'", regex.matcher(line).matches());
	}
	
	public static <T> void assertContains(Collection<? extends T> collection, T expected) {
		assertTrue("The collection did not contain " + expected, collection.contains(expected));
	}
	
	public static void assertContains(Solution[] solutions, Solution expected) {
		assertContains(new Population(solutions), expected);
	}
	
	public static void assertContains(Population population, Solution expected) {
		for (Solution actual : population) {
			try {
				assertEquals(actual, expected);
				return;
			} catch (AssertionError e) {
				// solutions do not match
			}
		}
		
		fail("The collection did not contain the expected solution");
	}
	
	public static void assertNotContains(Solution[] solutions, Solution expected) {
		assertNotContains(Arrays.asList(solutions), expected);
	}
	
	public static void assertNotContains(Iterable<? extends Solution> iterable, Solution expected) {
		for (Solution actual : iterable) {
			try {
				assertEquals(actual, expected);
				fail("The collection contained the solution when it shouldn't");
			} catch (AssertionError e) {
				// solutions do not match
			}
		}
	}
	
	public static void assertUniformDistribution(double min, double max, DescriptiveStatistics statistics) {
		FloatingPointError epsilon = new RelativeError(0.05);
		
		assertEquals((min + max) / 2.0, statistics.getMean(), epsilon);
		assertEquals(Math.pow(max - min, 2.0) / 12.0, statistics.getVariance(), epsilon);
		assertEquals(0.0, statistics.getSkewness(), epsilon);
		assertEquals(-6.0 / 5.0, statistics.getKurtosis(), epsilon);
		assertEquals(min, statistics.getMin(), epsilon);
		assertEquals(max, statistics.getMax(), epsilon);
	}

	// Note: It's important to use the integer version for discrete values to ensure we offset the range by 1.
	public static void assertUniformDistribution(int min, int max, DescriptiveStatistics statistics) {
		FloatingPointError epsilon = new RelativeError(0.05);
		int n = max - min + 1;
		int nn = n * n;

		assertEquals((min + max) / 2.0, statistics.getMean(), epsilon);
		assertEquals((nn - 1) / 12.0, statistics.getVariance(), epsilon);
		assertEquals(0.0, statistics.getSkewness(), epsilon);
		assertEquals(-(6.0 * (nn + 1)) / (5.0 * (nn - 1)), statistics.getKurtosis(), epsilon);
		assertEquals(min, statistics.getMin(), epsilon);
		assertEquals(max, statistics.getMax(), epsilon);
	}
	
	public static void assertLinePattern(File file, String regex) throws IOException {
		assertFileExists(file);
		
		try (CommentedLineReader reader = new CommentedLineReader(new FileReader(file))) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				assertMatches(line, regex);
			}
		}
	}
	
	public static void assertFileWithContent(File file) {
		assertFileExists(file);
		assertTrue("Expected file with content but was empty", file.length() > 0);
	}
	
	public static void assertFileWithContent(String expected, File file) throws IOException {
		assertFileExists(file);
		assertEquals("File '" + file + "' contains unexpected content",
				expected, Files.readString(file.toPath(), StandardCharsets.UTF_8));
	}
	
	public static void assertFileExists(File file) {
		assertTrue("Expected file '" + file + "' but file was not found", file.exists());
	}
	
	public static void assertFileNotExists(File file) {
		assertFalse("Found file '" + file + "' when it shouldn't exist", file.exists());
	}
	
	public static void assertLineCount(int expected, File file) throws IOException {
		int actual = 0;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (reader.readLine() != null) {
				actual++;
			}
		}
		
		assertEquals("File '" + file + "' contains unexpected line count", expected, actual);
	}
	
	public static void assertInstanceOf(Class<?> type, Object object) {
		assertTrue("Expected object of type " + type.getName() + " but was " + object.getClass().getName(),
				type.isInstance(object));
	}
	
	public static void assertEmpty(Collection<?> collection) {
		assertTrue("Expected empty collection but has size " + collection.size(), collection.isEmpty());
	}
	
	public static void assertEmpty(Iterable<?> iterable) {
		assertFalse("Expected empty collection but contained elements", iterable.iterator().hasNext());
	}
	
	public static void assertEmpty(Population population) {
		assertTrue("Expected empty population but has size " + population.size(), population.isEmpty());
	}
	
	public static void assertNotEmpty(Collection<?> collection) {
		assertTrue("Expected non-empty collection but was empty", !collection.isEmpty());
	}
	
	public static void assertNotEmpty(Iterable<?> iterable) {
		assertTrue("Expected non-empty collection but was empty", iterable.iterator().hasNext());
	}
	
	public static void assertNotEmpty(Population population) {
		assertTrue("Expected non-empty population but was empty", !population.isEmpty());
	}
	
	public static void assertSize(int expected, Collection<?> collection) {
		assertEquals("Collection not the expected size:", expected, collection.size());
	}
	
	public static void assertSize(int expected, Iterable<?> iterable) {
		int size = 0;
		Iterator<?> iterator = iterable.iterator();
		
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		
		assertEquals("Collection not the expected size:", expected, size);
	}
	
	public static void assertSize(int expected, Population population) {
		assertEquals("Population not the expected size:", expected, population.size());
	}
	
	public static void any(Runnable... assertions) {
		AssertionError error = null;
		
		for (Runnable assertion : assertions) {
			try {
				assertion.run();
				return;
			} catch (AssertionError e) {
				error = e;
			}
		}
		
		throw error;
	}
	
	/**
	 * Returns the regular expression pattern for detecting any number of numeric values separated by whitespace.
	 * 
	 * @param n the number of numeric values to detect
	 * @return the regular expression pattern for detecting any number of numeric values separated by whitespace
	 */
	public static String getSpaceSeparatedNumericPattern(int n) {
		return "(-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?\\b\\s*){" + n + "}";
	}
	
	/**
	 * Returns the regular expression pattern for detecting any number of numeric values separated by a comma.
	 * 
	 * @param n the number of numeric values to detect
	 * @return the regular expression pattern for detecting any number of numeric values separated by a comma
	 */
	public static String getCommaSeparatedNumericPattern(int n) {
		String pattern = "";
		
		if (n > 0) {
			pattern = "(-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?)";
		}
		
		if (n > 1) {
			pattern += "(\\s*,\\s*-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?){" + (n-1) + "}";
		}
		
		return pattern;
	}

}
