/* Copyright 2009-2018 David Hadka
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

import java.lang.reflect.Array;
import java.util.Properties;

/**
 * Wrapper for {@link Properties} providing getters for reading specific
 * primitive types. For primitive arrays, either the default "," separator or a
 * custom separator is used for splitting the string into individual components;
 * the formatting of this separator is defined in {@link String#split}. In
 * addition, whitespace surrounding each array entry is trimmed.
 */
public class TypedProperties {

	/**
	 * The default separator for arrays.
	 */
	public static final String DEFAULT_SEPARATOR = ",";

	/**
	 * Regular expression for the array separator, as used by the
	 * {@link String#split} methods.
	 */
	private final String separator;

	/**
	 * The {@code Properties} object storing the actual key/value pairs.
	 */
	private final Properties properties;
	
	/**
	 * Decorates an empty {@code Properties} object to provide type-safe access
	 * using the default "," separator for arrays.
	 */
	public TypedProperties() {
		this(new Properties());
	}

	/**
	 * Decorates a {@code Properties} object to provide type-safe access using 
	 * the default "," separator for arrays.
	 * 
	 * @param properties the existing {@code Properties} object
	 */
	public TypedProperties(Properties properties) {
		this(properties, DEFAULT_SEPARATOR);
	}

	/**
	 * Decorates a {@code Properties} object using the specified separator for 
	 * arrays.
	 * 
	 * @param properties the existing {@code Properties} object
	 * @param separator the separator for arrays
	 */
	public TypedProperties(Properties properties, String separator) {
		this.properties = properties;
		this.separator = separator;
	}
	
	/**
	 * Convenience method to quickly construct a typed properties instance with
	 * a single key-value pair.  This is particularly useful for parsing,
	 * for instance, command line arguments:
	 * <p>
	 * {@code TypedProperties.withProperty("epsilon", 
	 * commandLine.getOptionValue("epsilon")).getDoubleArray("epsilon", null);}
	 *   
	 * @param key the key
	 * @param value the value assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties withProperty(String key, String value) {
		Properties properties = new Properties();
		properties.setProperty(key, value);
		
		return new TypedProperties(properties);
	}
	
	/**
	 * Returns {@code true} if the specified key is contained in this
	 * properties object; {@code false} otherwise.
	 * 
	 * @param key the property name
	 * @return {@code true} if the specified key is contained in this
	 *         properties object; {@code false} otherwise
	 */
	public boolean contains(String key) {
		return properties.containsKey(key);
	}

	/**
	 * Returns the internal {@code Properties} object storing the actual 
	 * key/value pairs.
	 * 
	 * @return the internal {@code Properties} object storing the actual 
	 *         key/value pairs 
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the value of the property with the specified name as a string; or
	 * {@code defaultValue} if no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a string; or
	 *         {@code defaultValue} if no property with the specified name
	 *         exists
	 */
	public String getString(String key, String defaultValue) {
		String value = properties.getProperty(key);

		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code double}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code double}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         {@code double}
	 */
	public double getDouble(String key, double defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code float}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code float}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         {@code float}
	 */
	public float getFloat(String key, float defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Float.parseFloat(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code long}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code long}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         {@code long}
	 */
	public long getLong(String key, long defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as an
	 * {@code int}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as an
	 *         {@code int}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         integer
	 */
	public int getInt(String key, int defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code short}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code short}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         {@code short}
	 */
	public short getShort(String key, short defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Short.parseShort(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code byte}; or {@code defaultValue} if no property with the specified
	 * name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code byte}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 * @throws NumberFormatException if the property value is not a parseable
	 *         {@code byte}
	 */
	public byte getByte(String key, byte defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Byte.parseByte(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code boolean}; or {@code defaultValue} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a
	 *         {@code boolean}; or {@code defaultValue} if no property with the
	 *         specified name exists
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code String} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code String} array; or {@code defaultValues} if no property
	 *         with the specified name exists
	 */
	public String[] getStringArray(String key, String[] defaultValues) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValues;
		} else if (value.isEmpty()) {
			return new String[0];
		} else {
			String[] tokens = value.split(separator, -1);

			for (int i = 0; i < tokens.length; i++) {
				tokens[i] = tokens[i].trim();
			}

			return tokens;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code double} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code double} array; or {@code defaultValues} if no property
	 *         with the  specified name exists
	 */
	public double[] getDoubleArray(String key, double[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			double[] result = new double[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Double.parseDouble(values[i]);
			}

			return result;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code float} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code float} array; or {@code defaultValues} if no property with
	 *         the specified name exists
	 */
	public float[] getFloatArray(String key, float[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			float[] result = new float[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Float.parseFloat(values[i]);
			}

			return result;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code long} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code long} array; or {@code defaultValues} if no property with
	 *         the specified name exists
	 */
	public long[] getLongArray(String key, long[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			long[] result = new long[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Long.parseLong(values[i]);
			}

			return result;
		}
	}

	/**
	 * Returns the value of the property with the specified name as an
	 * {@code int} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as an
	 *         {@code int} array; or {@code defaultValues} if no property with
	 *         the specified name exists
	 */
	public int[] getIntArray(String key, int[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			int[] result = new int[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Integer.parseInt(values[i]);
			}

			return result;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code short} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code short} array; or {@code defaultValues} if no property with
	 *         the specified name exists
	 */
	public short[] getShortArray(String key, short[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			short[] result = new short[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Short.parseShort(values[i]);
			}

			return result;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a
	 * {@code byte} array; or {@code defaultValues} if no property with the
	 * specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a
	 *         {@code byte} array; or {@code defaultValues} if no property with
	 *         the specified name exists
	 */
	public byte[] getByteArray(String key, byte[] defaultValues) {
		String[] values = getStringArray(key, null);

		if (values == null) {
			return defaultValues;
		} else {
			byte[] result = new byte[values.length];

			for (int i = 0; i < values.length; i++) {
				result[i] = Byte.parseByte(values[i]);
			}

			return result;
		}
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code String}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setString(String key, String value) {
		properties.setProperty(key, value);
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code float}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setFloat(String key, float value) {
		setString(key, Float.toString(value));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code double}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setDouble(String key, double value) {
		setString(key, Double.toString(value));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code byte}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setByte(String key, byte value) {
		setString(key, Byte.toString(value));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code short}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setShort(String key, short value) {
		setString(key, Short.toString(value));
	}

	/**
	 * Sets the value of the property with the specified name as an
	 * {@code int}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setInt(String key, int value) {
		setString(key, Integer.toString(value));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code long}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setLong(String key, long value) {
		setString(key, Long.toString(value));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code boolean}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setBoolean(String key, boolean value) {
		setString(key, Boolean.toString(value));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code String} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setStringArray(String key, String[] values) {
		setString(key, arrayToString(values));
	}
	
	/**
	 * Sets the value of the property with the specified name as a
	 * {@code float} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setFloatArray(String key, float[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code double} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setDoubleArray(String key, double[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code byte} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setByteArray(String key, byte[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code short} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setShortArray(String key, short[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code int} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setIntArray(String key, int[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a
	 * {@code long} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setLongArray(String key, long[] values) {
		setString(key, arrayToString(values));
	}
	
	/**
	 * Clears all properties.
	 */
	public void clear() {
		properties.clear();
	}
	
	/**
	 * Removes the property with the specified name.
	 * 
	 * @param key the property name
	 */
	public void remove(String key) {
		properties.remove(key);
	}
	
	/**
	 * Adds all properties from the specified properties object.
	 * 
	 * @param properties the properties
	 */
	public void addAll(Properties properties) {
		this.properties.putAll(properties);
	}
	
	/**
	 * Adds all properties from the specified properties object.
	 * 
	 * @param properties the properties
	 */
	public void addAll(TypedProperties properties) {
		addAll(properties.getProperties());
	}
	
	/**
	 * Returns a string representation of the specified array that can be
	 * parsed by {@link TypedProperties}.  Returns {@code null} if the array
	 * is {@code null}.
	 * 
	 * @param array the array
	 * @return a string representation of the specified array that can be
	 *         parsed by {@code TypedProperties}
	 */
	private String arrayToString(Object array) {
		StringBuilder sb = new StringBuilder();
		Class<?> type = array.getClass();
		
		if (!type.isArray()) {
			throw new IllegalArgumentException("not an array");
		}
		
		for (int i=0; i<Array.getLength(array); i++) {
			if (i > 0) {
				sb.append(separator);
			}
			
			sb.append(Array.get(array, i));
		}
		
		return sb.toString();
	}

}
