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
package org.moeaframework.util;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link TypedProperties} class.
 */
public class TypedPropertiesTest {

	/**
	 * The typed properties instance used for testing.
	 */
	private TypedProperties properties;

	/**
	 * Sets up the typed properties instance for testing, filled with various
	 * default values.
	 */
	@Before
	public void setUp() {
		Properties p = new Properties();
		p.setProperty("float_invalid_array", "2.71,");
		p.setProperty("integer_invalid_array", "42,");

		properties = new TypedProperties(p);
	}

	/**
	 * Removes references to any shared objects for garbage collection.
	 */
	@After
	public void tearDown() {
		properties = null;
	}
	
	@Test
	public void testStaticConstructor() {
		double[] values = TypedProperties.withProperty("epsilon", "0.01,0.01")
				.getDoubleArray("epsilon", null);
		
		Assert.assertArrayEquals(new double[] { 0.01, 0.01 }, values, 
				Settings.EPS);
	}

	@Test
	public void testPrimitivesDefaultValue() {
		Assert.assertEquals("foo", properties.getString("-", "foo"));
		Assert.assertEquals(2.71, properties.getDouble("-", 2.71), 
				Settings.EPS);
		Assert.assertEquals(2.71f, properties.getFloat("-", 2.71f),
				(float)Settings.EPS);
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
		Assert.assertArrayEquals(new String[] { "2.71", "" }, properties
				.getStringArray("float_invalid_array", null));
		Assert.assertArrayEquals(new String[] { "42", "" }, properties
				.getStringArray("integer_invalid_array", null));
	}

	@Test
	public void testArraysDefaultValue() {
		Assert.assertArrayEquals(new String[] { "hello", "world!" }, properties
				.getStringArray("-", new String[] { "hello", "world!" }));
		Assert.assertArrayEquals(new double[] { 2.71 }, properties
				.getDoubleArray("-", new double[] { 2.71 }), Settings.EPS);
		Assert.assertArrayEquals(new float[] { 2.71f }, properties
				.getFloatArray("-", new float[] { 2.71f }), 
				(float)Settings.EPS);
		Assert.assertArrayEquals(new int[] { 42, 12 }, properties.getIntArray(
				"-", new int[] { 42, 12 }));
		Assert.assertArrayEquals(new long[] { 42, 12 }, properties
				.getLongArray("-", new long[] { 42, 12 }));
		Assert.assertArrayEquals(new short[] { 42, 12 }, properties
				.getShortArray("-", new short[] { 42, 12 }));
		Assert.assertArrayEquals(new byte[] { 42, 12 }, properties
				.getByteArray("-", new byte[] { 42, 12 }));
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
		Assert.assertEquals(2.71, properties.getDouble("double", 0.0),
				Settings.EPS);
		Assert.assertEquals(2.71f, properties.getFloat("float", 0.0f),
				(float)Settings.EPS);
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
		
		Assert.assertArrayEquals(new String[] { "foo", "bar" }, properties
				.getStringArray("string_array", null));
		Assert.assertArrayEquals(new double[] { 2.71, 1.44 }, properties
				.getDoubleArray("double_array", null), Settings.EPS);
		Assert.assertArrayEquals(new float[] { 2.71f, 1.44f }, properties
				.getFloatArray("float_array", null), (float)Settings.EPS);
		Assert.assertArrayEquals(new int[] { 42, 12 }, properties.getIntArray(
				"int_array", null));
		Assert.assertArrayEquals(new long[] { 42, 12 }, properties
				.getLongArray("long_array", null));
		Assert.assertArrayEquals(new short[] { 42, 12 }, properties
				.getShortArray("short_array", null));
		Assert.assertArrayEquals(new byte[] { 42, 12 }, properties
				.getByteArray("byte_array", null));
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

		Assert.assertArrayEquals(new String[0], properties.getStringArray(
				"string_array_empty", null));
		Assert.assertArrayEquals(new double[0], properties.getDoubleArray(
				"double_array_empty", null), Settings.EPS);
		Assert.assertArrayEquals(new float[0], properties.getFloatArray(
				"float_array_empty", null), (float)Settings.EPS);
		Assert.assertArrayEquals(new int[0], properties.getIntArray(
				"int_array_empty", null));
		Assert.assertArrayEquals(new long[0], properties.getLongArray(
				"long_array_empty", null));
		Assert.assertArrayEquals(new short[0], properties.getShortArray(
				"short_array_empty", null));
		Assert.assertArrayEquals(new byte[0], properties.getByteArray(
				"byte_array_empty", null));
	}

}
