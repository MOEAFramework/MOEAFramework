/* Copyright 2009-2025 David Hadka
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

import java.awt.Component;
import java.awt.Container;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.MenuElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.LineReader;

public class Assert extends org.junit.Assert {

	private Assert() {
		super();
	}
	
	public static void assertBothNullOrNotNull(Object expected, Object actual) {
		Assert.assertFalse("Expected both values to either be null or non-null",
				expected != null ^ actual != null);
	}
	
	public static <T> void assertCopy(T expected, T actual) {
		try {
			Assert.assertNotSame(expected, actual);
			Assert.assertEquals(expected.getClass(), actual.getClass());
			
			Class<?> type = expected.getClass();
			
			for (Field field : FieldUtils.getAllFields(type)) {
				field.setAccessible(true);
				
				Object expectedValue = field.get(expected);
				Object actualValue = field.get(actual);
				
				if (field.getType().isArray()) {
					Assert.assertBothNullOrNotNull(expectedValue, actualValue);
					Assert.assertTrue("expected arrays in copied objects to be copies", expectedValue != actualValue);
					
					Assert.assertEquals("expected arrays to have identical length",
							Array.getLength(expectedValue), Array.getLength(actualValue));
					
					for (int i = 0; i < Array.getLength(expectedValue); i++) {
						Assert.assertEquals(Array.get(expectedValue, i), Array.get(actualValue, i));
					}
				} else {
					Assert.assertEquals(expectedValue, actualValue);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AssertionError("Unable to compare copies", e);
		}
	}

	public static void assertEqualsNormalized(String expected, String actual) {
		assertEquals("The strings differ after normalization:",
				StringUtils.normalizeSpace(expected), StringUtils.normalizeSpace(actual));
	}
	
	public static void assertEquals(Double expected, Double actual) {
		assertEquals(expected.doubleValue(), actual.doubleValue());
	}
	
	public static void assertEquals(double expected, Double actual) {
		assertEquals(expected, actual.doubleValue());
	}
	
	public static void assertEquals(Double expected, double actual) {
		assertEquals(expected.doubleValue(), actual);
	}
	
	public static void assertEquals(double expected, double actual) {
		assertEquals(expected, actual, TestEnvironment.HIGH_PRECISION);
	}
	
	public static void assertEquals(Integer expected, Integer actual) {
		assertEquals(expected.intValue(), actual.intValue());
	}
	
	public static void assertEquals(int expected, Integer actual) {
		assertEquals(expected, actual.intValue());
	}
	
	public static void assertEquals(Integer expected, int actual) {
		assertEquals(expected.intValue(), actual);
	}
	
	public static void assertEquals(Long expected, Long actual) {
		assertEquals(expected.longValue(), actual.longValue());
	}
	
	public static void assertEquals(long expected, Long actual) {
		assertEquals(expected, actual.longValue());
	}
	
	public static void assertEquals(Long expected, long actual) {
		assertEquals(expected.longValue(), actual);
	}
	
	public static void assertEquals(TypedProperties expected, TypedProperties actual) {
		if (!expected.equals(actual)) {
			Set<String> keys = new HashSet<>();
			keys.addAll(expected.keySet());
			keys.addAll(actual.keySet());
			
			for (String key : keys) {
				if (!actual.contains(key)) {
					Assert.fail("Expected key '" + key + "' missing from properties");
				}
				
				if (!expected.contains(key)) {
					Assert.fail("Unexpected key '" + key + "' found in properties");
				}
				
				if (!expected.getString(key).equals(actual.getString(key))) {
					Assert.fail("Values for key '" + key + "' differ, expected '" + expected.getString(key) +
							"' but was '" + actual.getString(key) + "'");
				}
			}
			
			Assert.fail("Properties are different");
		}
	}
	
	public static void assertEquals(Objective expected, Objective actual, double epsilon) {
		assertTrue(expected.getClass().isInstance(actual) || actual.getClass().isInstance(expected));
		assertEquals(expected.getValue(), actual.getValue(), epsilon);
	}
	
	public static void assertEquals(Constraint expected, Constraint actual, double epsilon) {
		assertTrue(expected.getClass().isInstance(actual) || actual.getClass().isInstance(expected));
		assertEquals(expected.getValue(), actual.getValue(), epsilon);
	}
	
	public static void assertEquals(String message, Objective expected, Objective actual, double epsilon) {
		assertTrue(message, expected.getClass().isInstance(actual) || actual.getClass().isInstance(expected));
		assertEquals(message, expected.getValue(), actual.getValue(), epsilon);
	}
	
	public static void assertEquals(String message, Constraint expected, Constraint actual, double epsilon) {
		assertTrue(message, expected.getClass().isInstance(actual) || actual.getClass().isInstance(expected));
		assertEquals(message, expected.getValue(), actual.getValue(), epsilon);
	}
	
	public static void assertEquals(double[][] expected, double[][] actual) {
		assertEquals(new Array2DRowRealMatrix(expected), new Array2DRowRealMatrix(actual));
	}
	
	public static void assertEquals(RealMatrix expected, RealMatrix actual) {
		assertEquals(expected, actual, TestEnvironment.HIGH_PRECISION);
	}

	public static void assertEquals(RealMatrix expected, RealMatrix actual, double epsilon) {
		assertEquals("The matrices have different number of rows:",
				expected.getRowDimension(), actual.getRowDimension());
		
		assertEquals("The matrices have different number of columns:",
				expected.getColumnDimension(), actual.getColumnDimension());
		
		for (int i = 0; i < expected.getRowDimension(); i++) {
			for (int j = 0; j < expected.getColumnDimension(); j++) {
				assertEquals(expected.getEntry(i, j), actual.getEntry(i, j), epsilon);
			}
		}
	}
	
	public static void assertEquals(Population expected, Population actual) {
		assertEquals(expected, actual, false);
	}

	public static void assertEquals(Population expected, Population actual, boolean includeAttributes) {
		org.junit.Assert.assertEquals("The populations have different sizes:", expected.size(), actual.size());

		BitSet expectedMatches = new BitSet(expected.size());
		BitSet actualMatches = new BitSet(actual.size());

		for (int i = 0; i < expected.size(); i++) {
			for (int j = 0; j < actual.size(); j++) {
				try {
					assertEquals(expected.get(i), actual.get(j), includeAttributes);
					expectedMatches.set(i);
					actualMatches.set(j);
				} catch (AssertionError e) {
					// do nothing, solutions are not equal
				}
			}
		}
		
		if ((expected.size() != expectedMatches.cardinality()) || (actual.size() != actualMatches.cardinality())) {
			System.out.println("Expected:");
			expected.display();
			
			System.out.println("Actual:");
			actual.display();
			
			Assert.fail("The content of the two polutions differs");
		}
	}
	
	public static void assertNoNaN(Solution solution) {
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			if (solution.getVariable(i) instanceof RealVariable realVariable && Double.isNaN(realVariable.getValue())) {
				fail("Solution has NaN for decision variable at index " + i);
			}
		}
		
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			if (Double.isNaN(solution.getObjectiveValue(i))) {
				fail("Solution has NaN for objective value at index " + i);
			}
		}
		
		for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
			if (Double.isNaN(solution.getConstraintValue(i))) {
				fail("Solution has NaN for constraint value at index " + i);
			}
		}
	}
	
	public static void assertEquals(Solution expected, Solution actual) {
		assertEquals(expected, actual, false);
	}
	
	public static void assertEquals(Solution expected, Solution actual, boolean includeAttributes) {
		assertEquals("Solutions have different number of variables:",
				expected.getNumberOfVariables(), actual.getNumberOfVariables());

		assertEquals("Solutions have different number of objectives:",
				expected.getNumberOfObjectives(), actual.getNumberOfObjectives());
		
		assertEquals("Solutions have different number of constraints:",
				expected.getNumberOfConstraints(), actual.getNumberOfConstraints());

		for (int i = 0; i < expected.getNumberOfVariables(); i++) {
			if (expected.getVariable(i) instanceof Program p1 && actual.getVariable(i) instanceof Program p2) {
				// Programs do not implement equals, so check their encoding instead
				assertEquals("Solutions have different variables at index " + i + ":", p1.encode(), p2.encode());
			} else {
				assertEquals("Solutions have different variables at index " + i + ":",
						expected.getVariable(i), actual.getVariable(i));
			}
		}

		for (int i = 0; i < expected.getNumberOfObjectives(); i++) {
			assertEquals("Solutions have different objective values at index " + i + ":",
					expected.getObjective(i), actual.getObjective(i), TestEnvironment.LOW_PRECISION);
		}

		for (int i = 0; i < expected.getNumberOfConstraints(); i++) {
			assertEquals("Solutions have different constraint values at index " + i + ":",
					expected.getConstraint(i), actual.getConstraint(i), TestEnvironment.LOW_PRECISION);
		}
		
		if (includeAttributes) {
			assertAttributeEquality(expected, actual);
		}
	}
	
	public static void assertAttributeEquality(Solution expected, Solution actual) {
		Map<String, Serializable> expectedAttributes = expected.getAttributes();
		Map<String, Serializable> actualAttributes = actual.getAttributes();
		
		assertEquals("Solutions contain different attribute keys:",
				expectedAttributes.keySet(), actualAttributes.keySet());
		
		for (String key : expectedAttributes.keySet()) {
			Serializable expectedValue = expectedAttributes.get(key);
			Serializable actualValue = actualAttributes.get(key);
			
			assertEquals("Attribute " + key + " has different types:",
					expectedValue.getClass(), actualValue.getClass());
			
			Method method = null;
			
			try {
				method = expectedValue.getClass().getMethod("equals", Object.class);
			} catch (NoSuchMethodException e) {
				throw new AssertionError("Unable to locate equals method", e);
			}
			
			if (method.getDeclaringClass().equals(Object.class)) {
				// Object#equals performs an identity check, which can't detect two different objects with the same
				// value.  Instead, we check if the serialized values are equal.
				try (ByteArrayOutputStream expectedBytes = new ByteArrayOutputStream();
						ByteArrayOutputStream actualBytes = new ByteArrayOutputStream();
						ObjectOutputStream expectedOut = new ObjectOutputStream(expectedBytes);
						ObjectOutputStream actualOut = new ObjectOutputStream(actualBytes)) {
					expectedOut.writeObject(expected);
					actualOut.writeObject(actual);
					
					expectedOut.close();
					actualOut.close();
					
					assertArrayEquals("Attribute " + key + " has different serialized values:",
							expectedBytes.toByteArray(), actualBytes.toByteArray());
				} catch (IOException e) {
					throw new AssertionError("Unable to compare serializable objects", e);
				}
			} else {
				assertEquals("Attribute " + key + " has different values:", expectedValue, actualValue);
			}
		}
	}
	
	public static <T extends Comparable<T>> void assertGreaterThan(T lhs, T rhs) {
		MatcherAssert.assertThat(lhs, Matchers.greaterThan(rhs));
	}
	
	public static <T extends Comparable<T>> void assertGreaterThanOrEqual(T lhs, T rhs) {
		MatcherAssert.assertThat(lhs, Matchers.greaterThanOrEqualTo(rhs));
	}
	
	public static <T extends Comparable<T>> void assertLessThan(T lhs, T rhs) {
		MatcherAssert.assertThat(lhs, Matchers.lessThan(rhs));
	}
	
	public static <T extends Comparable<T>> void assertLessThanOrEqual(T lhs, T rhs) {
		MatcherAssert.assertThat(lhs, Matchers.lessThanOrEqualTo(rhs));
	}
	
	public static <T extends Comparable<T>> void assertBetween(T lowerBound, T upperBound, T value) {
		MatcherAssert.assertThat(value, Matchers.allOf(Matchers.greaterThanOrEqualTo(lowerBound),
				Matchers.lessThanOrEqualTo(upperBound)));
	}
	
	public static void assertBlank(String line) {
		assertTrue("Line is not blank: '" + line + "'", line.isBlank());
	}
	
	public static void assertStringContains(String line, String substring) {
		assertTrue("Line does not contain expected substring: '" + line + "'", line.contains(substring));
	}
	
	public static void assertStringNotContains(String line, String substring) {
		assertFalse("Line contains substring when it should not: '" + line + "'", line.contains(substring));
	}
	
	public static void assertStringMatches(String line, String regex) {
		assertTrue("Line does not match regex: '" + line + "'", line.matches(regex));
	}
	
	public static void assertStringMatches(String line, Pattern regex) {
		assertTrue("Line does not match regex: '" + line + "'", regex.matcher(line).matches());
	}
	
	public static void assertStringStartsWith(String line, String substring) {
		assertTrue("Line does not start with expected text: '" + line + "'", line.startsWith(substring));
	}
	
	public static void assertStringEndsWith(String line, String substring) {
		assertTrue("Line does not end with expected text: '" + line + "'", line.endsWith(substring));
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
		double epsilon = (max - min) * TestEnvironment.LOW_PRECISION;
		
		assertEquals((min + max) / 2.0, statistics.getMean(), epsilon);
		assertEquals(Math.pow(max - min, 2.0) / 12.0, statistics.getVariance(), epsilon);
		assertEquals(0.0, statistics.getSkewness(), epsilon);
		assertEquals(-6.0 / 5.0, statistics.getKurtosis(), epsilon);
		assertEquals(min, statistics.getMin(), epsilon);
		assertEquals(max, statistics.getMax(), epsilon);
	}

	// Note: It's important to use the integer version for discrete values to ensure we offset the range by 1.
	public static void assertUniformDistribution(int min, int max, DescriptiveStatistics statistics) {
		double epsilon = (max - min) * TestEnvironment.LOW_PRECISION;
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
		
		try (LineReader lineReader = LineReader.wrap(new FileReader(file)).skipComments()) {
			for (String line : lineReader) {
				assertStringMatches(line, regex);
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
		try (FileReader reader = new FileReader(file)) {
			assertLineCount("File '" + file + "' contains unexpected line count", expected, reader);
		}
	}
	
	public static void assertLineCount(int expected, String content) throws IOException {
		try (StringReader reader = new StringReader(content)) {
			assertLineCount("Content contains unexpected line count", expected, reader);
		}
	}
	
	private static void assertLineCount(String message, int expected, Reader reader) throws IOException {
		LineReader lineReader = LineReader.wrap(reader);
		int actual = 0;
		
		while (lineReader.readLine() != null) {
			actual++;
		}
		
		assertEquals(message, expected, actual);
	}
	
	public static <T> T assertInstanceOf(Class<T> cls, Object object) {
		assertTrue("Expected object of type " + cls.getName() + " but was " + object.getClass().getName(),
				cls.isInstance(object));
		
		return cls.cast(object);
	}
	
	public static void assertEmpty(Collection<?> collection) {
		assertTrue("Expected empty collection but has size " + collection.size(), collection.isEmpty());
	}
	
	public static void assertEmpty(Iterable<?> iterable) {
		assertFalse("Expected empty collection but contained elements", iterable.iterator().hasNext());
	}
	
	public static void assertEmpty(TypedProperties properties) {
		assertTrue("Expected empty properties but has size " + properties.size(), properties.isEmpty());
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
	
	public static void assertFeasible(Solution solution) {
		assertTrue("Solution is not feasible", solution.isFeasible());
	}
	
	public static void assertLocalized(Component component) {
		if (component instanceof JComponent jComponent) {
			Assert.assertTrue("Tooltip is not localized", isLocalized(jComponent.getToolTipText()));
		}
		
		if (component instanceof JFrame jFrame) {
			Assert.assertTrue("Frame title is not localized", isLocalized(jFrame.getTitle()));
		} else if (component instanceof JDialog jDialog) {
			Assert.assertTrue("Dialog title is not localized", isLocalized(jDialog.getTitle()));
		} else if (component instanceof AbstractButton button) {
			Assert.assertTrue("Button text is not localized", isLocalized(button.getText()));
			
			Action action = button.getAction();
			
			if (action != null) {
				Assert.assertTrue("Action name is not localized", isLocalized((String)action.getValue(Action.NAME)));
				Assert.assertTrue("Action description is not localized", isLocalized((String)action.getValue(Action.SHORT_DESCRIPTION)));
			}
		} else if (component instanceof JLabel label) {
			Assert.assertTrue("Label text is not localized", isLocalized(label.getText()));
		}
	}
	
	public static boolean isLocalized(String text) {
		return text == null || text.contains(" ") || text.endsWith("...") || !text.contains(".");
	}
	
	public static void walkUI(Component component, Consumer<Component> assertion) {
		assertion.accept(component);
		
		if (component instanceof MenuElement menuElement) {
			for (MenuElement nestedMenuElement : menuElement.getSubElements()) {
				walkUI(nestedMenuElement.getComponent(), assertion);
			}
		}
		
		if (component instanceof Container container) {
			for (Component nestedComponent : container.getComponents()) {
				walkUI(nestedComponent, assertion);
			}
		}
	}
	
	public static Component findComponent(Component component, Predicate<Component> matcher) {
		List<Component> matches = new ArrayList<>();
		
		walkUI(component, (c) -> {
			if (matcher.test(c)) {
				matches.add(c);
			}
		});
		
		if (matches.isEmpty()) {
			Assert.fail("Failed to find matching component");
		}
		
		if (matches.size() > 1) {
			Assert.fail("Found multiple components matching the predicate");
		}
		
		return matches.get(0);
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
