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
package org.moeaframework.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.configuration.ConfigurationException;

public class TypedPropertiesTest {
	
	public static final String SPECIAL_CHARACTERS = "\"'!@#$=:%^&*()\\\r\n//\t ";

	private TypedProperties properties;

	@Before
	public void setUp() {
		Properties p = new Properties();
		p.setProperty("float_invalid_array", "2.71,");
		p.setProperty("integer_invalid_array", "42,");

		properties = new TypedProperties(p);
	}

	@After
	public void tearDown() {
		properties = null;
	}
	
	@Test
	public void testStaticConstructor() {
		double[] values = TypedProperties.of("epsilon", "0.01,0.01").getDoubleArray("epsilon", null);
		
		Assert.assertArrayEquals(new double[] { 0.01, 0.01 }, values, TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testPrimitivesDefaultValue() {
		Assert.assertEquals("foo", properties.getString("-", "foo"));
		Assert.assertEquals(2.71, properties.getDouble("-", 2.71), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.71f, properties.getFloat("-", 2.71f), (float)TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(42, properties.getInt("-", 42));
		Assert.assertEquals(42, properties.getLong("-", 42));
		Assert.assertEquals(42, properties.getShort("-", (short)42));
		Assert.assertEquals(42, properties.getByte("-", (byte)42));
		Assert.assertFalse(properties.getBoolean("-", false));
		Assert.assertTrue(properties.getBoolean("-", true));
	}

	@Test
	public void testStringArrays() {
		// strings support empty entries
		Assert.assertArrayEquals(new String[] { "2.71", "" }, properties.getStringArray("float_invalid_array", null));
		Assert.assertArrayEquals(new String[] { "42", "" }, properties.getStringArray("integer_invalid_array", null));
	}

	@Test
	public void testArraysDefaultValue() {
		Assert.assertArrayEquals(new String[] { "hello", "world!" }, properties.getStringArray("-", new String[] { "hello", "world!" }));
		Assert.assertArrayEquals(new double[] { 2.71 }, properties.getDoubleArray("-", new double[] { 2.71 }), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new float[] { 2.71f }, properties.getFloatArray("-", new float[] { 2.71f }), (float)TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new int[] { 42, 12 }, properties.getIntArray("-", new int[] { 42, 12 }));
		Assert.assertArrayEquals(new long[] { 42, 12 }, properties.getLongArray("-", new long[] { 42, 12 }));
		Assert.assertArrayEquals(new short[] { 42, 12 }, properties.getShortArray("-", new short[] { 42, 12 }));
		Assert.assertArrayEquals(new byte[] { 42, 12 }, properties.getByteArray("-", new byte[] { 42, 12 }));
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryDouble() {
		properties.getDoubleArray("float_invalid_array", null);
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryFloat() {
		properties.getFloatArray("float_invalid_array", null);
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryInt() {
		properties.getIntArray("integer_invalid_array", null);
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryLong() {
		properties.getLongArray("integer_invalid_array", null);
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryShort() {
		properties.getShortArray("integer_invalid_array", null);
	}

	@Test(expected = NumberFormatException.class)
	public void testMissingEntryByte() {
		properties.getByteArray("integer_invalid_array", null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetNullArrayDouble() {
		properties.setDoubleArray("double_null", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullArrayFloat() {
		properties.setFloatArray("float_null", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullArrayInt() {
		properties.setIntArray("int_null", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullArrayLong() {
		properties.setLongArray("long_null", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullArrayShort() {
		properties.setShortArray("short_null", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullArrayByte() {
		properties.setByteArray("byte_null", null);
	}
	
	@Test
	public void testEnum() {
		properties.setEnum("enum", TestEnum.FOO);
		Assert.assertEquals(TestEnum.FOO, properties.getEnum("enum", TestEnum.class));
		
		properties.setString("enum_string", "bar");
		Assert.assertEquals(TestEnum.BAR, properties.getEnum("enum_string", TestEnum.class));
		
		Assert.assertEquals(TestEnum.FOO, properties.getEnum("missing_enum", TestEnum.class));
		Assert.assertEquals(TestEnum.BAR, properties.getEnum("missing_enum", TestEnum.class, TestEnum.BAR));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidEnum() {
		properties.setString("enum_invalid", "baz");
		Assert.assertEquals(TestEnum.BAR, properties.getEnum("enum_invalid", TestEnum.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetNullEnum() {
		properties.setEnum("enum_null", null);
	}
	
	@Test
	public void testEnumPartialMatching() {
		Assert.assertEquals(TestEnum.FOO, TypedProperties.getEnumFromPartialString(TestEnum.class, "f"));
		Assert.assertEquals(TestEnum.FOO, TypedProperties.getEnumFromPartialString(TestEnum.class, "foo"));
		
		Assert.assertThrows(IllegalArgumentException.class,
				() -> TypedProperties.getEnumFromPartialString(TestEnum.class, ""));
		Assert.assertThrows(IllegalArgumentException.class,
				() -> TypedProperties.getEnumFromPartialString(TestEnum.class, "undefined"));
		Assert.assertThrows(IllegalArgumentException.class,
				() -> TypedProperties.getEnumFromPartialString(TestEnum.class, "foobar"));
	}
	
	@Test
	public void testPrimitives() {
		properties.setString("string", "foo,bar");
		properties.setDouble("double", 2.71);
		properties.setFloat("float", 2.71f);
		properties.setInt("int", 42);
		properties.setLong("long", 42);
		properties.setShort("short", (short)42);
		properties.setByte("byte", (byte)42);
		properties.setBoolean("boolean_true", true);
		properties.setBoolean("boolean_false", false);
		
		Assert.assertEquals("foo,bar", properties.getString("string", null));
		Assert.assertEquals(2.71, properties.getDouble("double", 0.0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.71f, properties.getFloat("float", 0.0f), (float)TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(42, properties.getInt("int", 0));
		Assert.assertEquals(42, properties.getLong("long", 0));
		Assert.assertEquals(42, properties.getShort("short", (short)0));
		Assert.assertEquals(42, properties.getByte("byte", (byte)0));
		Assert.assertTrue(properties.getBoolean("boolean_true", false));
		Assert.assertFalse(properties.getBoolean("boolean_false", true));
	}
	
	@Test
	public void testArrays() {
		properties.setStringArray("string_array", new String[] { "foo", "bar" });
		properties.setDoubleArray("double_array", new double[] { 2.71, 1.44 });
		properties.setFloatArray("float_array", new float[] { 2.71f, 1.44f });
		properties.setIntArray("int_array", new int[] { 42, 12 });
		properties.setLongArray("long_array", new long[] { 42, 12 });
		properties.setShortArray("short_array", new short[] { 42, 12 });
		properties.setByteArray("byte_array", new byte[] { 42, 12 });
		
		Assert.assertArrayEquals(new String[] { "foo", "bar" }, properties.getStringArray("string_array", null));
		Assert.assertArrayEquals(new double[] { 2.71, 1.44 }, properties.getDoubleArray("double_array", null), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new float[] { 2.71f, 1.44f }, properties.getFloatArray("float_array", null), (float)TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new int[] { 42, 12 }, properties.getIntArray("int_array", null));
		Assert.assertArrayEquals(new long[] { 42, 12 }, properties.getLongArray("long_array", null));
		Assert.assertArrayEquals(new short[] { 42, 12 }, properties.getShortArray("short_array", null));
		Assert.assertArrayEquals(new byte[] { 42, 12 }, properties.getByteArray("byte_array", null));
	}
	
	@Test
	public void testEmptyArrays() {
		properties.setStringArray("string_array_empty", new String[0]);
		properties.setDoubleArray("double_array_empty", new double[0]);
		properties.setFloatArray("float_array_empty", new float[0]);
		properties.setIntArray("int_array_empty", new int[0]);
		properties.setLongArray("long_array_empty", new long[0]);
		properties.setShortArray("short_array_empty", new short[0]);
		properties.setByteArray("byte_array_empty", new byte[0]);

		Assert.assertArrayEquals(new String[0], properties.getStringArray("string_array_empty", null));
		Assert.assertArrayEquals(new double[0], properties.getDoubleArray("double_array_empty", null), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new float[0], properties.getFloatArray("float_array_empty", null), (float)TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new int[0], properties.getIntArray("int_array_empty", null));
		Assert.assertArrayEquals(new long[0], properties.getLongArray("long_array_empty", null));
		Assert.assertArrayEquals(new short[0], properties.getShortArray("short_array_empty", null));
		Assert.assertArrayEquals(new byte[0], properties.getByteArray("byte_array_empty", null));
	}
	
	@Test
	public void testGetKeys() {
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		
		Set<String> keys = properties.keySet();
		Assert.assertEquals(1, keys.size());
		Assert.assertContains(keys, "foo");
		
		keys.remove("foo");
		Assert.assertFalse(properties.contains("foo"));
	}
	
	@Test
	public void testAccessedProperties() {
		TypedProperties properties = new TypedProperties();
		Assert.assertTrue(properties.getAccessedProperties().isEmpty());
		Assert.assertTrue(properties.getUnaccessedProperties().isEmpty());
		
		properties.setString("foo", "bar");
		properties.getString("baz", null);
		Assert.assertTrue(properties.getAccessedProperties().contains("baz"));
		Assert.assertTrue(properties.getUnaccessedProperties().contains("foo"));
		
		properties.getString("foo", null);
		Assert.assertTrue(properties.getAccessedProperties().contains("baz"));
		Assert.assertTrue(properties.getAccessedProperties().contains("foo"));
		Assert.assertTrue(properties.getUnaccessedProperties().isEmpty());
		
		properties.clearAccessedProperties();
		Assert.assertTrue(properties.getAccessedProperties().isEmpty());
		Assert.assertTrue(properties.getUnaccessedProperties().contains("foo"));
	}
	
	@Test
	public void testCaseInsensitive() {
		TypedProperties properties = new TypedProperties();
		properties.setString("foo", "bar");
		
		Assert.assertTrue(properties.contains("Foo"));
		Assert.assertTrue(properties.contains("FOO"));
		
		Assert.assertEquals("bar", properties.getString("Foo", null));
		Assert.assertEquals("bar", properties.getString("FOO", null));
		
		Assert.assertTrue(properties.getAccessedProperties().contains("Foo"));
		Assert.assertTrue(properties.getAccessedProperties().contains("FOO"));
		Assert.assertTrue(properties.getUnaccessedProperties().isEmpty());
		
		properties.setString("baz", "value");
		Assert.assertFalse(properties.getUnaccessedProperties().isEmpty());
		Assert.assertTrue(properties.getUnaccessedProperties().contains("Baz"));
		Assert.assertTrue(properties.getUnaccessedProperties().contains("BAZ"));
	}
	
	@Test
	public void testTruncation() {
		int intValue = Integer.MAX_VALUE;
		long longValue = Long.MAX_VALUE;
		
		TypedProperties properties = new TypedProperties();
		properties.setDouble("double", 2.71);
		properties.setInt("int", intValue);
		properties.setLong("long", longValue);
		properties.setDouble("max_double", Double.MAX_VALUE);
		properties.setDouble("min_double", -Double.MAX_VALUE);
		
		Assert.assertEquals(intValue, properties.getInt("int"));
		Assert.assertEquals(intValue, properties.getTruncatedInt("int"));
		
		Assert.assertEquals(intValue, properties.getInt("missing", intValue));
		Assert.assertEquals(intValue, properties.getTruncatedInt("missing", intValue));
		
		Assert.assertThrows(NumberFormatException.class, () -> properties.getInt("double"));
		Assert.assertEquals(2, properties.getTruncatedInt("double"));
		Assert.assertEquals(2, properties.getTruncatedInt("double", intValue));
		
		Assert.assertEquals(longValue, properties.getLong("long"));
		Assert.assertEquals(longValue, properties.getTruncatedLong("long"));
		
		Assert.assertEquals(longValue, properties.getLong("missing", longValue));
		Assert.assertEquals(longValue, properties.getTruncatedLong("missing", longValue));
		
		Assert.assertThrows(NumberFormatException.class, () -> properties.getLong("double"));
		Assert.assertEquals(2, properties.getTruncatedLong("double"));
		Assert.assertEquals(2, properties.getTruncatedLong("double", longValue));
		
		// if the truncation would change the value by more than 1, throw instead of warn
		Assert.assertThrows(NumberFormatException.class, () -> properties.getInt("long"));
		Assert.assertThrows(FrameworkException.class, () -> properties.getTruncatedInt("long"));
		Assert.assertThrows(FrameworkException.class, () -> properties.getTruncatedInt("max_double"));
		Assert.assertThrows(FrameworkException.class, () -> properties.getTruncatedLong("max_double"));
		Assert.assertThrows(FrameworkException.class, () -> properties.getTruncatedInt("min_double"));
		Assert.assertThrows(FrameworkException.class, () -> properties.getTruncatedLong("min_double"));
	}
	
	// Ensures the largest and smallest integers can be stored as doubles without altering the value.
	// Note this only works with doubles; floats do not have the range to store all integers.
	@Test
	public void testIntegerRange() {
		TypedProperties properties = new TypedProperties();
		
		properties.setDouble("value", Integer.MAX_VALUE);
		Assert.assertEquals(Integer.MAX_VALUE, properties.getTruncatedInt("value"));
		
		properties.setDouble("value", Integer.MIN_VALUE);
		Assert.assertEquals(Integer.MIN_VALUE, properties.getTruncatedInt("value"));
	}
	
	@Test
	public void testBuildProperties() throws IOException {
		TypedProperties properties = TypedProperties.loadBuildProperties();
		
		Assert.assertTrue(properties.contains("name"));
		Assert.assertTrue(properties.contains("version"));
		
		properties.clearAccessedProperties();
		
		for (String key : properties.getUnaccessedProperties()) {
			Assert.assertFalse(properties.getString(key).contains("${"));
		}
	}
	
	@Test
	public void testThrowsIfMissing() {
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getString("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getBoolean("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getByte("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getShort("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getInt("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getLong("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getFloat("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getDouble("missing"));
		
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getStringArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getByteArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getShortArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getIntArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getLongArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getFloatArray("missing"));
		Assert.assertThrows(PropertyNotFoundException.class, () -> properties.getDoubleArray("missing"));
	}
	
	@Test(expected = ConfigurationException.class)
	public void testThrowsIfUnaccessed() {
		properties.clearAccessedProperties();
		properties.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testEquals() {
		TypedProperties clone = new TypedProperties();
		clone.addAll(properties);
		
		Assert.assertEquals(clone, properties);
		Assert.assertEquals(clone.hashCode(), properties.hashCode());
		
		clone.setString("foo", "bar");
		
		Assert.assertNotEquals(clone, properties);
	}
	
	@Test
	public void testLocale() {
		properties.setString("\u0130", "found");
		Assert.assertEquals("found", properties.getString("\u0069"));
		Assert.assertEquals("found", properties.getString("\u0131"));
	}
	
	@Test
	public void testStoreLoad() throws IOException {
		try (StringWriter writer = new StringWriter()) {
			properties.store(writer);
			
			try (StringReader reader = new StringReader(writer.toString())) {
				TypedProperties copy = new TypedProperties();
				copy.load(reader);
				
				Assert.assertEquals(properties, copy);
			}
		}
	}
	
	@Test
	public void testStoreNoComments() throws IOException {
		try (StringWriter writer = new StringWriter()) {
			properties.store(writer);
			
			try (BufferedReader reader = new BufferedReader(new StringReader(writer.toString()))) {
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					Assert.assertFalse(line.startsWith("#") || line.startsWith("!"));
				}
			}
		}
	}
	
	@Test
	public void testSpecialCharacters() throws IOException {
		TypedProperties properties = new TypedProperties();
		properties.setString(SPECIAL_CHARACTERS, SPECIAL_CHARACTERS);
		
		try (StringWriter writer = new StringWriter()) {
			properties.store(writer);
			
			try (StringReader reader = new StringReader(writer.toString())) {
				TypedProperties copy = new TypedProperties();
				copy.load(reader);
				
				Assert.assertEquals(properties, copy);
			}
		}
	}
	
	@Test
	public void testEmptyValue() throws IOException {
		String input = "foo=";
				
		TypedProperties expected = new TypedProperties();
		expected.setString("foo", "");
		
		try (StringReader reader = new StringReader(input)) {
			TypedProperties properties = new TypedProperties();
			properties.load(reader);
			
			Assert.assertEquals(expected, properties);
		}
	}
	
	@Test
	public void testBlankValue() throws IOException {
		String input = "foo=   ";
				
		TypedProperties expected = new TypedProperties();
		expected.setString("foo", "");
		
		try (StringReader reader = new StringReader(input)) {
			TypedProperties properties = new TypedProperties();
			properties.load(reader);
			
			Assert.assertEquals(expected, properties);
		}
	}
	
	@Test
	public void testSpaceValue() throws IOException {
		String input = "foo= \\ ";
				
		TypedProperties expected = new TypedProperties();
		expected.setString("foo", " ");
		
		try (StringReader reader = new StringReader(input)) {
			TypedProperties properties = new TypedProperties();
			properties.load(reader);
			
			Assert.assertEquals(expected, properties);
		}
	}
	
	@Test
	public void testWhitespace() throws IOException {
		// This differs slightly from Properties by removing the trailing whitespace in the value
		String input = " foo = bar ";
				
		TypedProperties expected = new TypedProperties();
		expected.setString("foo", "bar");
		
		try (StringReader reader = new StringReader(input)) {
			TypedProperties properties = new TypedProperties();
			properties.load(reader);
			
			Assert.assertEquals(expected, properties);
		}
	}
	
	@Test
	public void testWhitespaceEscaped() throws IOException {
		String input = "\\ foo\\ =\\ bar\\ ";
				
		TypedProperties expected = new TypedProperties();
		expected.setString(" foo ", " bar ");
		
		try (StringReader reader = new StringReader(input)) {
			TypedProperties properties = new TypedProperties();
			properties.load(reader);
			
			Assert.assertEquals(expected, properties);
		}
	}
	
	@Test
	public void testAlphabeticalOrdering() {
		TypedProperties properties = new TypedProperties();
		properties.setString("z", "val");
		properties.setString("a", "val");
		properties.setString("m", "val");
		
		Iterator<String> keyIterator = properties.keySet().iterator();
		Assert.assertEquals("a", keyIterator.next());
		Assert.assertEquals("m", keyIterator.next());
		Assert.assertEquals("z", keyIterator.next());
	}
	
	@Test
	public void testInsertionOrdering() {
		TypedProperties properties = TypedProperties.newInsertionOrderInstance();
		properties.setString("z", "val");
		properties.setString("a", "val");
		properties.setString("m", "val");
		
		Iterator<String> keyIterator = properties.keySet().iterator();
		Assert.assertEquals("z", keyIterator.next());
		Assert.assertEquals("a", keyIterator.next());
		Assert.assertEquals("m", keyIterator.next());
	}

	private enum TestEnum {
		FOO,
		BAR
	}

}
