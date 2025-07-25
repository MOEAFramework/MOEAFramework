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
package org.moeaframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.text.StringSubstitutor;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/**
 * Stores a collection of key-value pairs similar to {@link Properties} but has support for reading and writing
 * primitive types.  Internally, this handles converting specific types to a string representation that can be saved
 * and read from files.  As such, there is some leniency in how type conversion is handled.  For instance, the value
 * {@code "0.1"} can be read as a string, float, double, or an single-valued array of these types.
 * <p>
 * In addition to primitive types, arrays of those primitives are also supported using either the default
 * "{@value #DEFAULT_SEPARATOR}" separator or a user-configurable character.  Escaping is automatically performed
 * when using one of the array setters.  Leading and trailing whitespace is also trimmed from each entry.
 * <p>
 * Keys are case-insensitive.
 */
public class TypedProperties implements Formattable<Entry<String, String>>, Copyable<TypedProperties> {
	
	private static final Tokenizer PROEPRTY_TOKENIZER;
	
	static {
		PROEPRTY_TOKENIZER = new Tokenizer();
		PROEPRTY_TOKENIZER.escapeChar("=", "\\=");
		PROEPRTY_TOKENIZER.escapeChar(":", "\\:");
		PROEPRTY_TOKENIZER.escapeChar("#", "\\#");
		PROEPRTY_TOKENIZER.escapeChar("!", "\\!");
		PROEPRTY_TOKENIZER.setDelimiter('=');
	}

	/**
	 * The default separator for arrays.
	 */
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * The tokenizer for parsing arrays.
	 */
	private final Tokenizer tokenizer;

	/**
	 * Storage of the key-value pairs.
	 */
	private final Map<String, String> properties;
	
	/**
	 * The keys that were accessed.
	 */
	private final Set<String> accessedKeys;
	
	/**
	 * Creates a new, empty instance of this class.
	 */
	public TypedProperties() {
		this(DEFAULT_SEPARATOR, false);
	}
	
	/**
	 * Creates a new typed properties instance initialized with the content of the properties.
	 * 
	 * @param properties the existing {@code Properties} object
	 */
	public TypedProperties(Properties properties) {
		this(DEFAULT_SEPARATOR, false);
		addAll(properties);
	}
	
	/**
	 * Creates a new, empty instance of this class using the given separator for arrays.
	 * 
	 * @param separator the separator or delimiter for arrays
	 * @param threadSafe if {@code true}, the constructed instance will be thread-safe
	 */
	protected TypedProperties(char separator, boolean threadSafe) {
		super();
		
		tokenizer = new Tokenizer();
		tokenizer.reset();
		tokenizer.setDelimiter(separator);
		
		Map<String, String> tempProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		Set<String> tempAccessedKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		
		if (threadSafe) {
			tempProperties = Collections.synchronizedMap(tempProperties);
			tempAccessedKeys = Collections.synchronizedSet(tempAccessedKeys);
		}
		
		this.properties = tempProperties;
		this.accessedKeys = tempAccessedKeys;
	}
	
	/**
	 * Returns the set of keys contained in this properties object.  The set is backed by this properties object, so
	 * changes to the set, such as removing a key, will also remove the corresponding property.
	 * 
	 * @return the keys
	 */
	public Set<String> keySet() {
		return properties.keySet();
	}
	
	/**
	 * Returns {@code true} if the specified key is contained in this properties object; {@code false} otherwise.
	 * 
	 * @param key the property name
	 * @return {@code true} if the specified key is contained in this properties object; {@code false} otherwise
	 */
	public boolean contains(String key) {
		accessedKeys.add(key);
		return properties.containsKey(key);
	}

	/**
	 * Returns the value of the property with the specified name as a string; or {@code defaultValue} if no property
	 * with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a string; or {@code defaultValue} if no property
	 *         with the specified name exists
	 */
	public String getString(String key, String defaultValue) {
		String value = properties.get(key);
		accessedKeys.add(key);

		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}
	
	/**
	 * Returns the value of the property as a string, or throws an exception.  One should either use the variant that
	 * takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public String getString(String key) {
		String value = getString(key, null);
		
		if (value == null) {
			throw new PropertyNotFoundException(key);
		} else {
			return value;
		}
	}

	/**
	 * Returns the value of the property with the specified name as a {@code double}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code double}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable {@code double}
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
	 * Returns the value of the property as a {@code double}, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public double getDouble(String key) {
		return Double.parseDouble(getString(key));
	}

	/**
	 * Returns the value of the property with the specified name as a {@code float}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code float}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable {@code float}
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
	 * Returns the value of the property as a {@code float}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}

	/**
	 * Returns the value of the property with the specified name as a {@code long}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code long}; or {@code defaultValue} if no
	 * property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable {@code long}
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
	 * Returns the value of the property as a {@code long}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public long getLong(String key) {
		return Long.parseLong(getString(key));
	}

	/**
	 * Returns the value of the property with the specified name as an {@code int}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as an {@code int}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable integer
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
	 * Returns the value of the property as a {@code int}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	
	// The following methods allow truncating the stored value when converting real-valued numbers to integers with
	// the following rules:
	//
	//   1. If no truncation is required, the number is parsed as-is.
	//   2. If the truncation alters the value more than the defined machine precision, Settings.EPS,
	//      then a warning is optionally displayed.
	//   3. If the truncation would alter the value more than 1.0, an exception is thrown.  This includes
	//      trying to parse a value that exceeds the maximum or minimum value that fits within a type.
	
	/**
	 * Returns the value of the property with the specified name as an {@code int}; or {@code defaultValue} if no
	 * property with the specified name exists.  Any decimal places will be truncated.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as an {@code int}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable integer
	 */
	public int getTruncatedInt(String key, int defaultValue) {
		try {
			return getInt(key, defaultValue);
		} catch (NumberFormatException e) {
			if (contains(key)) {
				try {
					return truncateInt(key, getLong(key));
				} catch (NumberFormatException e2) {
					return truncateInt(key, getDouble(key));
				}
			} else {
				return defaultValue;
			}
		}
	}
	
	/**
	 * Returns the value of the property as a {@code int}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.  Any decimal places
	 * will be truncated.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public int getTruncatedInt(String key) {
		try {
			return getInt(key);
		} catch (NumberFormatException e) {
			try {
				return truncateInt(key, getLong(key));
			} catch (NumberFormatException e2) {
				return truncateInt(key, getDouble(key));
			}
		}
	}
	
	/**
	 * Returns the value of the property with the specified name as a {@code long}; or {@code defaultValue} if no
	 * property with the specified name exists.  Any decimal places will be truncated.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code long}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable long
	 */
	public long getTruncatedLong(String key, long defaultValue) {
		try {
			return getLong(key, defaultValue);
		} catch (NumberFormatException e) {
			if (contains(key)) {
				return truncateLong(key, getDouble(key));
			} else {
				return defaultValue;
			}
		}
	}
	
	/**
	 * Returns the value of the property as a {@code long}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.  Any decimal places
	 * will be truncated.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public long getTruncatedLong(String key) {
		try {
			return getLong(key);
		} catch (NumberFormatException e) {
			return truncateLong(key, getDouble(key));
		}
	}
	
	/**
	 * Convert a double value to an integer with truncation.
	 * 
	 * @param key the property name
	 * @param originalValue the original floating-point value
	 * @return the integer value
	 */
	private int truncateInt(String key, double originalValue) {
		int truncatedValue = (int)originalValue;
		double diff = Math.abs(originalValue - truncatedValue);
		
		if (diff >= 1.0) {
			throw new InvalidPropertyException(key, "Truncating " + originalValue +
					" to an integer would result in a rounding error of " + diff);
		}
		
		if (diff > Settings.EPS && Settings.isVerbose()) {
			System.err.println(key + " given as real-valued number but expected an integer, converting " +
					originalValue + " to " + truncatedValue);
		}
		
		return truncatedValue;
	}
	
	/**
	 * Convert a long value to an integer with truncation.
	 * 
	 * @param key the property name
	 * @param originalValue the original long value
	 * @return the integer value
	 */
	private int truncateInt(String key, long originalValue) {
		int truncatedValue = (int)originalValue;
		
		if (truncatedValue != originalValue) {
			throw new InvalidPropertyException(key, "Truncating " + originalValue +
					" to an integer would result in a rounding error of " + Math.abs(truncatedValue - originalValue));
		}
		
		return truncatedValue;
	}
	
	/**
	 * Convert a double value to a long with truncation.
	 * 
	 * @param key the property name
	 * @param originalValue the original floating-point value
	 * @return the long value
	 */
	private long truncateLong(String key, double originalValue) {
		long truncatedValue = (long)originalValue;
		double diff = Math.abs(originalValue - truncatedValue);
		
		if (diff >= 1.0) {
			throw new InvalidPropertyException(key, "Truncating " + originalValue +
					" to an integer would result in a rounding error of " + diff);
		}
		
		if (diff > Settings.EPS && Settings.isVerbose()) {
			System.err.println(key + " given as real-valued number but expected a long, converting " +
					originalValue + " to " + truncatedValue);
		}
		
		return truncatedValue;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code short}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code short}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable {@code short}
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
	 * Returns the value of the property as a {@code short}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public short getShort(String key) {
		return Short.parseShort(getString(key));
	}

	/**
	 * Returns the value of the property with the specified name as a {@code byte}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code byte}; or {@code defaultValue} if no
	 *         property with the specified name exists
	 * @throws NumberFormatException if the property value is not a parseable {@code byte}
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
	 * Returns the value of the property as a {@code byte}, or throws an exception.  One should either use the variant
	 * that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public byte getByte(String key) {
		return Byte.parseByte(getString(key));
	}

	/**
	 * Returns the value of the property with the specified name as a {@code boolean}; or {@code defaultValue} if no
	 * property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as a {@code boolean}; or {@code defaultValue} if no
	 *         property with the specified name exists
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
	 * Returns the value of the property as a {@code boolean}, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}
	
	/**
	 * Returns the value of the property with the specified name as an Enum.  If no such property is set, returns the
	 * default Enum value.
	 * 
	 * @param <T> the Enum type
	 * @param key the property name
	 * @param enumType the Enum class
	 * @return the value of the property with the specified name as an Enum
	 */
	public <T extends Enum<?>> T getEnum(String key, Class<T> enumType) {
		return getEnum(key, enumType, enumType.getEnumConstants()[0]);
	}
	
	/**
	 * Returns the value of the property with the specified name as an Enum; or {@code defaultValue} if no property
	 * with the specified name exists.
	 * 
	 * @param <T> the Enum type
	 * @param key the property name
	 * @param enumType the Enum class
	 * @param defaultValue the default value
	 * @return the value of the property with the specified name as an Enum
	 */
	public <T extends Enum<?>> T getEnum(String key, Class<T> enumType, T defaultValue) {
		String value = getString(key, null);
		
		if (value == null) {
			return defaultValue;
		} else {
			return getEnumFromString(enumType, value);
		}
	}
	
	/**
	 * Converts the value into the matching Enum constant.  Unlike using {@link Enum#valueOf(Class, String)}, this
	 * version is case-insensitive.
	 * 
	 * @param <T> the Enum type
	 * @param enumType the Enum class
	 * @param value the value as a string
	 * @return the Enum value
	 * @throws IllegalArgumentException if the value does not match any enumeration constant
	 */
	public static <T extends Enum<?>> T getEnumFromString(Class<T> enumType, String value) {
		for (T enumConstant : enumType.getEnumConstants()) {
			if (enumConstant.name().equalsIgnoreCase(value)) {
				return enumConstant;
			}
		}
			
		return Validate.that("value", value).failUnsupportedOption(enumType);
	}
	
	/**
	 * Converts the value into the matching Enum constant using an {@link OptionCompleter} to allow partial string
	 * matching.
	 * 
	 * @param <T> the Enum type
	 * @param enumType the Enum class
	 * @param value the value as a string
	 * @return the Enum value
	 * @throws IllegalArgumentException if the value does not match any enumeration constant
	 */
	public static <T extends Enum<?>> T getEnumFromPartialString(Class<T> enumType, String value) {
		return getEnumFromString(enumType, new OptionCompleter(enumType).getOrThrow("value", value));
	}
	
	/**
	 * Returns the value of the property with the specified name as a {@code String} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code String} array; or {@code defaultValues} if
	 *         no property with the specified name exists
	 */
	public String[] getStringArray(String key, String[] defaultValues) {
		String value = getString(key, null);

		if (value == null) {
			return defaultValues;
		} else if (value.isBlank()) {
			return new String[0];
		} else {
			return tokenizer.decodeToArray(value);
		}
	}
	
	/**
	 * Returns the value of the property as a {@code String} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public String[] getStringArray(String key) {
		String[] values = getStringArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code double} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code double} array; or {@code defaultValues} if
	 *         no property with the  specified name exists
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
	 * Returns the value of the property as a {@code double} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public double[] getDoubleArray(String key) {
		double[] values = getDoubleArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code float} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code float} array; or {@code defaultValues} if
	 *         no property with the specified name exists
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
	 * Returns the value of the property as a {@code float} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public float[] getFloatArray(String key) {
		float[] values = getFloatArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code long} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code long} array; or {@code defaultValues} if
	 *         no property with the specified name exists
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
	 * Returns the value of the property as a {@code long} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public long[] getLongArray(String key) {
		long[] values = getLongArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as an {@code int} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as an {@code int} array; or {@code defaultValues} if
	 *         no property with the specified name exists
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
	 * Returns the value of the property as a {@code int} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public int[] getIntArray(String key) {
		int[] values = getIntArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code short} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code short} array; or {@code defaultValues} if
	 *         no property with the specified name exists
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
	 * Returns the value of the property as a {@code short} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public short[] getShortArray(String key) {
		short[] values = getShortArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}

	/**
	 * Returns the value of the property with the specified name as a {@code byte} array; or {@code defaultValues} if
	 * no property with the specified name exists.
	 * 
	 * @param key the property name
	 * @param defaultValues the default values
	 * @return the value of the property with the specified name as a {@code byte} array; or {@code defaultValues} if
	 *         no property with the specified name exists
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
	 * Returns the value of the property as a {@code byte} array, or throws an exception.  One should either use the
	 * variant that takes a default value or check {@link #contains(String)} to ensure the property exists.
	 * 
	 * @param key the property name
	 * @return the value of the property as an array
	 * @throws PropertyNotFoundException if the property was not found
	 */
	public byte[] getByteArray(String key) {
		byte[] values = getByteArray(key, null);
		
		if (values == null) {
			throw new PropertyNotFoundException(key);
		}
		
		return values;
	}
	
	/**
	 * Sets the value of the property to the given {@code String}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setString(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Sets the value of the property to the given {@code float}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setFloat(String key, float value) {
		setString(key, Float.toString(value));
	}
	
	/**
	 * Sets the value of the property to the given {@code double}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setDouble(String key, double value) {
		setString(key, Double.toString(value));
	}
	
	/**
	 * Sets the value of the property to the given {@code byte}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setByte(String key, byte value) {
		setString(key, Byte.toString(value));
	}
	
	/**
	 * Sets the value of the property to the given {@code short}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setShort(String key, short value) {
		setString(key, Short.toString(value));
	}

	/**
	 * Sets the value of the property to the given {@code int}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setInt(String key, int value) {
		setString(key, Integer.toString(value));
	}
	
	/**
	 * Sets the value of the property to the given {@code long}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setLong(String key, long value) {
		setString(key, Long.toString(value));
	}

	/**
	 * Sets the value of the property to the given {@code boolean}.
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	public void setBoolean(String key, boolean value) {
		setString(key, Boolean.toString(value));
	}
	
	/**
	 * Sets the value of the property to the given enum value.
	 * 
	 * @param <T> the type of the enum
	 * @param key the property name
	 * @param value the property value
	 */
	public <T extends Enum<?>> void setEnum(String key, T value) {
		setString(key, value.name());
	}
	
	/**
	 * Sets the value of the property with the specified name as a {@code String} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setStringArray(String key, String[] values) {
		setString(key, arrayToString(values));
	}
	
	/**
	 * Sets the value of the property with the specified name as a {@code float} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setFloatArray(String key, float[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a {@code double} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setDoubleArray(String key, double[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a {@code byte} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setByteArray(String key, byte[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a {@code short} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setShortArray(String key, short[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a {@code int} array.
	 * 
	 * @param key the property name
	 * @param values the property value
	 */
	public void setIntArray(String key, int[] values) {
		setString(key, arrayToString(values));
	}

	/**
	 * Sets the value of the property with the specified name as a {@code long} array.
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
		accessedKeys.clear();
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
	 * Adds all properties from the specified {@link Properties} object.  Use caution as the inserted values are
	 * represented as strings and might not be formatted properly for the given types.
	 * 
	 * @param properties the properties
	 */
	public void addAll(Properties properties) {
		for (String key : properties.stringPropertyNames()) {
			this.properties.put(key, properties.getProperty(key));
		}
	}
	
	/**
	 * Adds all properties from the specified properties object.
	 * 
	 * @param properties the properties
	 */
	public void addAll(TypedProperties properties) {
		this.properties.putAll(properties.properties);
	}
	
	/**
	 * Returns a string representation of the specified array that can be parsed by {@link TypedProperties}.  Returns
	 * {@code null} if the array is {@code null}.
	 * 
	 * @param array the array
	 * @return a string representation of the specified array that can be parsed by {@code TypedProperties}
	 */
	private String arrayToString(Object array) {
		StringBuilder sb = new StringBuilder();
		Class<?> type = array.getClass();

		if (!type.isArray()) {
			Validate.that("array", array).fails("Not an array");
		}
		
		for (int i=0; i<Array.getLength(array); i++) {
			if (i > 0) {
				sb.append(tokenizer.getDelimiter());
			}
			
			sb.append(tokenizer.escape(Array.get(array, i).toString()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the number of properties that are defined.
	 * 
	 * @return the number of properties
	 */
	public int size() {
		return properties.size();
	}
	
	/**
	 * Returns {@code true} if there are no properties set.
	 * 
	 * @return {@code true} if no properties are set; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return properties.isEmpty();
	}
	
	@Override
	public TypedProperties copy() {
		TypedProperties copy = new TypedProperties();
		copy.addAll(this);
		return copy;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(properties)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			TypedProperties rhs = (TypedProperties)obj;
			
			return new EqualsBuilder()
					.append(properties, rhs.properties)
					.isEquals();
		}
	}

	/**
	 * Loads the properties from a reader.
	 * 
	 * @param reader the reader
	 * @throws IOException if an I/O error occurred
	 */
	public void load(Reader reader) throws IOException {
		LineReader lineReader = LineReader.wrap(reader).skipComments().skipBlanks();
		
		for (String line : lineReader) {
			String[] tokens = PROEPRTY_TOKENIZER.decodeToArray(line);
			properties.put(tokens[0], tokens.length > 1 ? tokens[1] : "");
		}
	}
	
	/**
	 * Writes the properties to a writer.
	 * 
	 * @param writer the writer
	 * @throws IOException if an I/O error occurred
	 */
	public void save(Writer writer) throws IOException {
		save(writer, null);
	}
	
	/**
	 * Writes the properties to a writer, with an optional {@link Comparator} for specifying the ordering.
	 * 
	 * @param writer the writer
	 * @param order controls the ordering of keys, or {@code null} for the default
	 * @throws IOException if an I/O error occurred
	 */
	public void save(Writer writer, Comparator<String> order) throws IOException {
		List<String> keys = new ArrayList<>();
		keys.addAll(properties.keySet());
		
		if (order != null) {
			keys.sort(order);
		}
		
		for (String key : keys) {
			writer.write(PROEPRTY_TOKENIZER.encode(List.of(key, properties.get(key))));
			writer.write(System.lineSeparator());
		}
	}
	
	@Override
	public TabularData<Entry<String, String>> asTabularData() {
		TabularData<Entry<String, String>> table = new TabularData<>(properties.entrySet());
		table.addColumn(new Column<>("Property", Entry::getKey));
		table.addColumn(new Column<>("Value", Entry::getValue));
		return table;
	}
	
	/**
	 * Clears the tracking information for properties that have been accessed.
	 */
	public void clearAccessedProperties() {
		accessedKeys.clear();
	}
	
	/**
	 * Returns the properties that were accessed since the last call to {@link #clearAccessedProperties()} or
	 * {@link #clear()}.
	 * 
	 * @return the accessed properties
	 */
	public Set<String> getAccessedProperties() {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		result.addAll(accessedKeys);
		return result;
	}
	
	/**
	 * Returns the properties that were never accessed since the last call to {@link #clearAccessedProperties()} or
	 * {@link #clear()}
	 * 
	 * @return the unaccessed or orphaned properties
	 */
	public Set<String> getUnaccessedProperties() {
		Set<String> orphanedProperties = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		orphanedProperties.addAll(properties.keySet());
		orphanedProperties.removeAll(accessedKeys);
		return orphanedProperties;
	}
	
	/**
	 * Prints a warning if any properties were not accessed.  This only considers properties at the time this method
	 * is invoked, so a removed but unaccessed property would not cause a warning.  For example:
	 * <pre>
	 *     TypedProperties properties = new TypedProperties();
	 *     ... write properties ...
	 * 
	 *     properties.clearAccessedProperties();
	 *     ... read properties ...
	 *     properties.warnIfUnaccessedProperties();
	 * </pre>
	 */
	public void warnIfUnaccessedProperties() {
		Set<String> orphanedProperties = getUnaccessedProperties();
		
		if (!orphanedProperties.isEmpty()) {
			System.err.println("Properties not accessed: " + String.join(", ", orphanedProperties));
		}
	}
	
	/**
	 * Similar to {@link #warnIfUnaccessedProperties()}, except throws a {@link ConfigurationException} if any
	 * properties were not accessed.
	 * 
	 * @throws ConfigurationException if at least one property was not accessed
	 */
	public void throwIfUnaccessedProperties() {
		Set<String> orphanedProperties = getUnaccessedProperties();
		
		if (!orphanedProperties.isEmpty()) {
			throw new ConfigurationException("Properties not accessed: " + String.join(", ", orphanedProperties));
		}
	}
	
	/**
	 * Creates a new scope that allows making temporary changes to the properties.  When the scope is closed, the
	 * original properties are restored.  Typically, you should create scopes within try-with-resources blocks so they
	 * are automatically closed.
	 * 
	 * @return the scope
	 */
	public PropertyScope createScope() {
		return new PropertyScope(this);
	}
	
	/**
	 * Creates and returns an empty properties object that is thread-safe.  This is useful when needing thread-safe
	 * access to a shared properties object.
	 * 
	 * @return the typed properties instance
	 */
	public static TypedProperties newThreadSafeInstance() {
		return new TypedProperties(DEFAULT_SEPARATOR, true);
	}
	
	/**
	 * Convenience method to construct an empty typed properties instance.  The returned instance is mutable
	 * and can be modified by the caller.
	 * 
	 * @return the typed properties instance
	 */
	public static TypedProperties of() {
		return new TypedProperties();
	}
	
	/**
	 * Convenience method to construct a typed properties instance containing the same data as a {@link Properties}
	 * object.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param properties the properties
	 * @return the typed properties instance
	 */
	public static TypedProperties of(Properties properties) {
		return new TypedProperties(properties);
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, byte... values) {
		TypedProperties properties = new TypedProperties();
		properties.setByteArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, short... values) {
		TypedProperties properties = new TypedProperties();
		properties.setShortArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, int... values) {
		TypedProperties properties = new TypedProperties();
		properties.setIntArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, long... values) {
		TypedProperties properties = new TypedProperties();
		properties.setLongArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, float... values) {
		TypedProperties properties = new TypedProperties();
		properties.setFloatArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  The value can either
	 * be a single value or an array.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param values the value(s) assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, double... values) {
		TypedProperties properties = new TypedProperties();
		properties.setDoubleArray(key, values);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.  While the value is
	 * a single string, its interpretation depends on the method
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param value the value assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, String value) {
		TypedProperties properties = new TypedProperties();
		properties.setString(key, value);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param key the key
	 * @param value the value assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static TypedProperties of(String key, boolean value) {
		TypedProperties properties = new TypedProperties();
		properties.setBoolean(key, value);
		return properties;
	}
	
	/**
	 * Convenience method to construct a typed properties instance with a single key-value pair.
	 * <p>
	 * The returned instance is mutable and can be modified by the caller.
	 * 
	 * @param <T> the enum type
	 * @param key the key
	 * @param value the value assigned to the key
	 * @return a typed properties instance with the specified key-value pair
	 */
	public static <T extends Enum<?>> TypedProperties of(String key, T value) {
		TypedProperties properties = new TypedProperties();
		properties.setEnum(key, value);
		return properties;
	}
		
	/**
	 * Loads the contents of {@code META-INF/build.properties} and evaluates any string substitutions in the form
	 * {@code ${token}}.
	 * 
	 * @return the build properties
	 * @throws IOException if an error occurred loading the file
	 */
	public static TypedProperties loadBuildProperties() throws IOException {
		Properties rawProperties = new Properties();
		
		try (InputStream stream = Resources.asStream(Settings.class, "/META-INF/build.properties")) {
			if (stream != null) {
				rawProperties.load(stream);
			}
		}
		
		Map<String, Object> mappings = new HashMap<>();
		
		for (Entry<Object, Object> entry : rawProperties.entrySet()) {
			mappings.put(entry.getKey().toString(), entry.getValue());
		}
		
		StringSubstitutor substitutor = new StringSubstitutor(mappings);
		substitutor.setEnableSubstitutionInVariables(true);
		
		TypedProperties result = new TypedProperties();
		
		for (Entry<Object, Object> entry : rawProperties.entrySet()) {
			result.setString(entry.getKey().toString(), substitutor.replace(entry.getValue()));
		}
		
		return result;
	}

}
