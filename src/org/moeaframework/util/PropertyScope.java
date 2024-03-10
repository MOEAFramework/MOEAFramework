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
package org.moeaframework.util;

/**
 * Creates a scope for a {@link TypedProperties}, allowing the properties to be temporarily overridden
 * within the scope.  These should typically be used within try-with-resources so the scope is automatically
 * closed when exiting the block.
 * <pre>
 *   try (PropertyScope scope = properties.createScope()) {
 *       // temporarily override properties within scope
 *       properties.setInt("populationSize", 500);
 *   }
 * </pre>
 */
public class PropertyScope implements AutoCloseable {
	
	/**
	 * The properties that are covered by this scope.
	 */
	private final TypedProperties properties;
	
	/**
	 * The original values that will be restored when closing this scope.
	 */
	private final TypedProperties originalValues;

	/**
	 * Creates a new scope for the properties.
	 * 
	 * @param properties the properties
	 */
	public PropertyScope(TypedProperties properties) {
		super();
		this.properties = properties;
		
		originalValues = new TypedProperties();
		originalValues.addAll(properties);
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, double value) {
		properties.setDouble(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, float value) {
		properties.setFloat(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, long value) {
		properties.setLong(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, int value) {
		properties.setInt(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, short value) {
		properties.setShort(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, byte value) {
		properties.setByte(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, boolean value) {
		properties.setBoolean(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @param <T> the enumeration type
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public <T extends Enum<?>> PropertyScope with(String key, T value) {
		properties.setEnum(key, value);
		return this;
	}
	
	/**
	 * Convenience method for setting a property within the scope.
	 * 
	 * @param key the property key
	 * @param value the property value
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope with(String key, String value) {
		properties.setString(key, value);
		return this;
	}
	
	/**
	 * Convenience method for removing a property within the scope.
	 * 
	 * @param key the property key
	 * @return a reference to this scope for chaining multiple calls together
	 */
	public PropertyScope without(String key) {
		properties.remove(key);
		return this;
	}

	@Override
	public void close() {
		properties.clear();
		properties.addAll(originalValues);
	}

}
