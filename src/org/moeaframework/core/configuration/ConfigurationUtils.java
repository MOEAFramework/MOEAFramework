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
package org.moeaframework.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.WordUtils;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.operator.real.SelfAdaptiveNormalVariation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.problem.Problem;

/**
 * Utility methods for scanning classes to identify properties, reading the configured values, or setting new values.
 */
public class ConfigurationUtils {
	
	private ConfigurationUtils() {
		super();
	}
	
	/**
	 * Updates the properties in the configurable object (and all nested configurable objects recursively).
	 * 
	 * @param properties the new properties
	 * @param object the configurable object
	 */
	public static void applyConfiguration(TypedProperties properties, Configurable object) {
		Problem problem = null;
		
		if (object instanceof Algorithm algorithm) {
			problem = algorithm.getProblem();
		}
		
		applyConfiguration(properties, object, problem);
	}
	
	/**
	 * Updates the properties in the configurable object (and all nested configurable objects recursively).
	 * 
	 * @param properties the new properties
	 * @param object the configurable object
	 * @param problem the problem instance, which is needed if constructing any variation or mutation operators
	 */
	public static void applyConfiguration(TypedProperties properties, Configurable object, Problem problem) {
		Class<?> type = object.getClass();
		Prefix prefix = type.getAnnotation(Prefix.class);

		for (Method method : type.getMethods()) {
			String methodName = method.getName();
			Property property = MethodUtils.getAnnotation(method, Property.class, true, false);
			
			if (property != null) {
				String propertyName = property.value().isEmpty() ?
						WordUtils.uncapitalize(methodName.substring(3)) :
						property.value();
				
				if (isSetter(method)) {
					ConfigurationUtils.applyValue(properties, prefix, propertyName, property.alias(), method,
							object, problem);
				} else if (isGetter(method, Object.class) || property.readOnly()) {
					if (properties.contains(propertyName) && Settings.isVerbose()) {
						System.err.println("Skipping read-only property '" + propertyName + "'");
					}
				} else {
					throw ConfigurationException.invalidAnnotation(object, methodName);
				}
			}
			
			if (isGetter(method, Configurable.class)) {
				Configurable nestedObject = safeInvokeGetter(method, object, Configurable.class);
					
				if (nestedObject != null) {
					nestedObject.applyConfiguration(properties);
				}
			}
		}
	}
	
	/**
	 * Reads and returns all properties used by the configurable object.
	 * 
	 * @param object the configurable object
	 * @return the properties
	 */
	public static TypedProperties getConfiguration(Configurable object) {
		TypedProperties properties = new TypedProperties();
		Class<?> type = object.getClass();
		Prefix prefix = type.getAnnotation(Prefix.class);

		for (Method method : type.getMethods()) {
			String methodName = method.getName();
			Property property = MethodUtils.getAnnotation(method, Property.class, true, false);
						
			if (property != null) {
				String propertyName = property.value().isEmpty() ?
						WordUtils.uncapitalize(methodName.substring(3)) :
						property.value();
				
				propertyName = prefixName(prefix, propertyName);
	
				if (isSetter(method)) {
					Method getter = MethodUtils.getAccessibleMethod(type, "get" + methodName.substring(3));
					
					if (getter == null && boolean.class.isAssignableFrom(method.getParameterTypes()[0])) {
						getter = MethodUtils.getAccessibleMethod(type, "is" + methodName.substring(3));
					}
					
					if (getter == null) {
						throw ConfigurationException.noGetterFound(object, propertyName);
					}
					
					ConfigurationUtils.extractValue(properties, propertyName, getter, object);
				} else if (isGetter(method, Object.class)) {
					ConfigurationUtils.extractValue(properties, propertyName, method, object);
	 			} else {
					throw ConfigurationException.invalidAnnotation(object, methodName);
				}
			}
			
			if (isGetter(method, Configurable.class)) {
				Configurable nestedObject = safeInvokeGetter(method, object, Configurable.class);
					
				if (nestedObject != null) {
					properties.addAll(nestedObject.getConfiguration());
				}
			}
		}
		
		return properties;
	}
	
	/**
	 * Updates the value of a single property.
	 * 
	 * @param properties the properties and their values
	 * @param prefix the prefix or {@code null} if no prefix is set
	 * @param propertyName the property name
	 * @param aliases any aliases or alternate names used by the property
	 * @param method the method to call to set the property value
	 * @param object the configurable object defining this property
	 * @param problem the problem instance
	 */
	@SuppressWarnings("unchecked")
	private static void applyValue(TypedProperties properties, Prefix prefix, String propertyName, String[] aliases,
			Method method, Configurable object, Problem problem) {
		propertyName = findPropertyName(properties, prefix, propertyName, aliases);

		if (propertyName == null) {
			return;
		}
		
		Class<?> parameterType = method.getParameterTypes()[0];
		Object value = null;
		
		// order widest to shortest
		if (ClassUtils.isAssignable(double.class, parameterType)) {
			value = properties.getDouble(propertyName);
		} else if (ClassUtils.isAssignable(float.class, parameterType)) {
			value = properties.getFloat(propertyName);
		} else if (ClassUtils.isAssignable(long.class, parameterType)) {
			value = properties.getLong(propertyName);
		} else if (ClassUtils.isAssignable(int.class, parameterType)) {
			value = properties.getTruncatedInt(propertyName);
		} else if (ClassUtils.isAssignable(short.class, parameterType)) {
			value = properties.getShort(propertyName);
		} else if (ClassUtils.isAssignable(byte.class, parameterType)) {
			value = properties.getByte(propertyName);
		} else if (ClassUtils.isAssignable(boolean.class, parameterType)) {
			value = properties.getBoolean(propertyName);
		} else if (ClassUtils.isAssignable(parameterType, Enum.class)) {
			value = properties.getEnum(propertyName, (Class<? extends Enum<?>>)parameterType);
		} else if (ClassUtils.isAssignable(String.class, parameterType)) {
			value = properties.getString(propertyName);
		} else if (ClassUtils.isAssignable(Variation.class, parameterType) ||
				ClassUtils.isAssignable(DifferentialEvolutionVariation.class, parameterType)) {
			if (problem == null) {
				throw new ConfigurationException("Must provide problem if setting variation operator");
			}
						
			String operator = properties.getString(propertyName);
			value = OperatorFactory.getInstance().getVariation(operator, properties, problem);
		} else if (ClassUtils.isAssignable(Mutation.class, parameterType) ||
				ClassUtils.isAssignable(SelfAdaptiveNormalVariation.class, parameterType)) {
			if (problem == null) {
				throw new ConfigurationException("Must provide problem if setting mutation operator");
			}
						
			String operator = properties.getString(propertyName);
			value = OperatorFactory.getInstance().getMutation(operator, properties, problem);
		} else {
			throw ConfigurationException.unsupportedType(parameterType, propertyName);
		}
		
		try {
			method.invoke(object, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ConfigurationException("Failed to apply property " + propertyName, e);
		}
	}
	
	/**
	 * Reads the value for a single property.
	 * 
	 * @param properties the collection of properties read from the object
	 * @param propertyName the property name
	 * @param method the method for reading the property value
	 * @param object the configurable object defining the property
	 */
	private static void extractValue(TypedProperties properties, String propertyName, Method method,
			Configurable object) {
		Class<?> parameterType = method.getReturnType();

		try {
			Object value = method.invoke(object);
			
			if (value == null) {
				return;
			}

			// order shortest to widest
			if (ClassUtils.isAssignable(parameterType, boolean.class)) {
				properties.setBoolean(propertyName, (boolean)value);
			} else if (ClassUtils.isAssignable(parameterType, byte.class)) {
				properties.setByte(propertyName, (byte)value);
			} else if (ClassUtils.isAssignable(parameterType, short.class)) {
				properties.setShort(propertyName, (short)value);
			} else if (ClassUtils.isAssignable(parameterType, int.class)) {
				properties.setInt(propertyName, (int)value);
			} else if (ClassUtils.isAssignable(parameterType, long.class)) {
				properties.setLong(propertyName, (long)value);
			} else if (ClassUtils.isAssignable(parameterType, float.class)) {
				properties.setDouble(propertyName, (float)value);
			} else if (ClassUtils.isAssignable(parameterType, double.class)) {
				properties.setDouble(propertyName, (double)value);
			} else if (ClassUtils.isAssignable(parameterType, Enum.class)) {
				properties.setEnum(propertyName, (Enum<?>)value);
			} else if (ClassUtils.isAssignable(parameterType, String.class)) {
				properties.setString(propertyName, (String)value);
			} else if (ClassUtils.isAssignable(parameterType, Variation.class)) {
				properties.setString(propertyName, ((Variation)value).getName());
			} else if (ClassUtils.isAssignable(parameterType, Problem.class)) {
				properties.setString(propertyName, ((Problem)value).getName());
			} else {
				throw ConfigurationException.unsupportedType(parameterType, propertyName);
			}
			
			if (value instanceof Configurable configurable) {
				TypedProperties valueProperties = configurable.getConfiguration();
				properties.addAll(valueProperties);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ConfigurationException("Failed to read property " + propertyName, e);
		}
	}
	
	/**
	 * Locates and returns the property name.  This checks if the properties contain the primary name or an alias,
	 * in the order given, and returns the matching name.
	 * 
	 * @param properties the collection of all configured properties
	 * @param prefix the prefix or {@code null} if none is set
	 * @param propertyName the property name
	 * @param aliases any aliases or alternate names
	 * @return the property name that was found or {@code null}
	 */
	private static String findPropertyName(TypedProperties properties, Prefix prefix, String propertyName,
			String[] aliases) {
		String prefixedName = prefixName(prefix, propertyName);
		
		if (properties.contains(prefixedName)) {
			return prefixedName;
		}
		
		for (String alias : aliases) {
			prefixedName = prefixName(prefix, alias);
					
			if (properties.contains(prefixedName)) {
				return prefixedName;
			}
		}
				
		return null;
	}
	
	/**
	 * Adds the prefix to the property name.
	 * 
	 * @param prefix the prefix or {@code null} if no prefix was set
	 * @param propertyName the property name
	 * @return the prefix added to the property name
	 */
	private static String prefixName(Prefix prefix, String propertyName) {
		if (prefix != null && !prefix.value().isEmpty()) {
			propertyName = prefix.value() + "." + propertyName;
		}
		
		return propertyName;
	}
	
	/**
	 * Returns {@code true} if the given method is a getter following JavaBean conventions.
	 * 
	 * @param method the method to check
	 * @param returnType the expected return type
	 * @return {@code true} if this is a getter method for the given return type; {@code false} otherwise
	 */
	private static boolean isGetter(Method method, Class<?> returnType) {
		return method.getName().startsWith("get") && method.getParameterCount() == 0 &&
				ClassUtils.isAssignable(method.getReturnType(), returnType);
	}
	
	/**
	 * Returns {@code true} if the given method is a setter following JavaBean conventions.
	 * 
	 * @param method the method to check
	 * @return {@code true} if this is a setter method; {@code false} otherwise
	 */
	private static boolean isSetter(Method method) {
		return method.getName().startsWith("set") && method.getParameterCount() == 1;
	}
	
	/**
	 * Safely invoke the getter method and cast the returned object to the given type.  Any exceptions thrown while
	 * calling the method are wrapped in a {@link ConfigurationException}.
	 * 
	 * @param <T> the type of the return object
	 * @param method the getter method
	 * @param object the object to call the getter method on
	 * @param returnType the type of the return object
	 * @return the returned value
	 * @throws ConfigurationException if an exception was thrown while calling the getter
	 */
	private static <T> T safeInvokeGetter(Method method, Configurable object, Class<T> returnType) {
		try {
			return returnType.cast(method.invoke(object));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ConfigurationException("Failed to call " + method.getName(), e);
		}
	}

}
