/* Copyright 2009-2022 David Hadka
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

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.WordUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

public class ConfigurationUtils {
	
	private ConfigurationUtils() {
		super();
	}
	
	public static void applyConfiguration(TypedProperties properties, Configurable object) {
		Problem problem = null;
		
		if (object instanceof Algorithm) {
			problem = ((Algorithm)object).getProblem();
		}
		
		applyConfiguration(properties, object, problem);
	}
	
	public static void applyConfiguration(TypedProperties properties, Configurable object, Problem problem) {
		Class<?> type = object.getClass();
		Prefix prefix = type.getAnnotation(Prefix.class);

		// process properties defined in this class
		for (Method method : MethodUtils.getMethodsWithAnnotation(type, Property.class, false, false)) {
			String methodName = method.getName();
			Property property = method.getAnnotation(Property.class);
			
			if (isSetter(method)) {
				String propertyName = property.value().isEmpty() ?
						WordUtils.uncapitalize(methodName.substring(3)) :
						property.value();
				
				if (prefix != null && !prefix.value().isEmpty()) {
					propertyName = prefix.value() + "." + propertyName;
				}

				ConfigurationUtils.applyValue(properties, propertyName, property.synonym(), method, object, problem);
			} else {
				System.err.println("found @Property annotation on non-setter method " + methodName + " in class " +
					type.getSimpleName() + ", ignoring");
			}
		}
		
		// process any configurable objects referenced by this class
		for (Method method : type.getMethods()) {
			if (isGetter(method, Configurable.class)) {
				Configurable nestedObject = safeInvokeGetter(method, object, Configurable.class);
					
				if (nestedObject != null) {
					nestedObject.applyConfiguration(properties);
				}
			}
		}
	}
	
	public static TypedProperties getConfiguration(Configurable object) {
		TypedProperties properties = new TypedProperties();
		Class<?> type = object.getClass();
		Prefix prefix = type.getAnnotation(Prefix.class);

		// process properties defined in this class
		for (Method method : MethodUtils.getMethodsWithAnnotation(type, Property.class, false, false)) {
			String methodName = method.getName();
			Property property = method.getAnnotation(Property.class);

			if (isSetter(method)) {
				String propertyName = property.value().isEmpty() ?
						WordUtils.uncapitalize(methodName.substring(3)) :
						property.value();
				
				if (prefix != null && !prefix.value().isEmpty()) {
					propertyName = prefix.value() + "." + propertyName;
				}
				
				Method getter = MethodUtils.getAccessibleMethod(type, "get" + methodName.substring(3));
				
				if (getter == null && boolean.class.isAssignableFrom(method.getParameterTypes()[0])) {
					getter = MethodUtils.getAccessibleMethod(type, "is" + methodName.substring(3));
				}
				
				if (getter != null) {
					ConfigurationUtils.extractValue(properties, propertyName, getter, object);
				} else {
					System.err.println("no getter method found for property " + propertyName);
				}
			} else {
				System.err.println("found @Property annotation on non-setter method " + methodName + " in class " +
					type.getSimpleName() + ", ignoring");
			}
		}
		
		// process any configurable objects referenced by this class
		for (Method method : type.getMethods()) {
			if (isGetter(method, Configurable.class)) {
				Configurable nestedObject = safeInvokeGetter(method, object, Configurable.class);
					
				if (nestedObject != null) {
					properties.addAll(nestedObject.getConfiguration());
				}
			}
		}
		
		return properties;
	}
	
	private static void applyValue(TypedProperties properties, String propertyName, String[] synonyms,
			Method method, Configurable object, Problem problem) {
		propertyName = findPropertyName(properties, propertyName, synonyms);
		
		if (propertyName == null) {
			return;
		}
		
		Class<?> parameterType = method.getParameterTypes()[0];
		Object value = null;

		if (parameterType.isAssignableFrom(double.class)) {
			value = properties.getDouble(propertyName, 0.0);
		} else if (parameterType.isAssignableFrom(int.class)) {
			try {
				value = properties.getInt(propertyName, 0);
			} catch (NumberFormatException e) {
				// TODO: In the past, we have allowed reading most int parameters as doubles.  This handles that
				// conversion, but I would like to discontinue this practice.
				value = (int)properties.getDouble(propertyName, 0.0);
				
				System.err.println(propertyName + " given as floating-point but expected an int, converting " +
						properties.getString(propertyName, "") + " to " + value);
			}
		} else if (parameterType.isAssignableFrom(boolean.class)) {
			value = properties.getBoolean(propertyName, false);
		} else if (parameterType.isAssignableFrom(String.class)) {
			value = properties.getString(propertyName, null);
		} else if (parameterType.isAssignableFrom(Variation.class)) {
			if (problem == null) {
				throw new ConfigurationException("must provide problem if setting variation operator");
			}
			
			String operator = properties.getString(propertyName, null);
			value = OperatorFactory.getInstance().getVariation(operator, properties, problem);
		} else if (parameterType.isAssignableFrom(Mutation.class)) {
			if (problem == null) {
				throw new ConfigurationException("must provide problem if setting mutation operator");
			}
			
			String operator = properties.getString(propertyName, null);
			value = OperatorFactory.getInstance().getMutation(operator, properties, problem);
		} else {
			throw new ConfigurationException("unsupported type " + parameterType + " for property " + propertyName);
		}
		
		try {
			method.invoke(object, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new FrameworkException("failed to apply property " + propertyName, e);
		}
	}
	
	private static void extractValue(TypedProperties properties, String propertyName, Method method,
			Configurable object) {
		Class<?> parameterType = method.getReturnType();

		try {
			Object value = method.invoke(object);
			
			if (double.class.isAssignableFrom(parameterType)) {
				properties.setDouble(propertyName, (double)value);
			} else if (int.class.isAssignableFrom(parameterType)) {
				properties.setInt(propertyName, (int)value);
			} else if (boolean.class.isAssignableFrom(parameterType)) {
				properties.setBoolean(propertyName, (boolean)value);
			} else if (String.class.isAssignableFrom(parameterType)) {
				properties.setString(propertyName, (String)value);
			} else if (Variation.class.isAssignableFrom(parameterType)) {
				properties.setString(propertyName, ((Variation)value).getName());
			} else {
				throw new ConfigurationException("unsupported type " + parameterType + " for property " + propertyName);
			}
			
			if (value instanceof Configurable) {
				TypedProperties valueProperties = ((Configurable)value).getConfiguration();
				properties.addAll(valueProperties);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ConfigurationException("failed to read property " + propertyName, e);
		}
	}
	
	private static String findPropertyName(TypedProperties properties, String propertyName, String[] synonyms) {
		if (properties.contains(propertyName)) {
			return propertyName;
		}
		
		for (String synonym : synonyms) {
			if (properties.contains(synonym)) {
				return synonym;
			}
		}
				
		return null;
	}
	
	private static boolean isGetter(Method method, Class<?> returnType) {
		return method.getName().startsWith("get") && method.getParameterCount() == 0 &&
				returnType.isAssignableFrom(method.getReturnType());
	}
	
	private static boolean isSetter(Method method) {
		return method.getName().startsWith("set") && method.getParameterCount() == 1;
	}
	
	private static <T> T safeInvokeGetter(Method method, Configurable object, Class<T> returnType) {
		try {
			return returnType.cast(method.invoke(object));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ConfigurationException("failed to call " + method.getName(), e);
		}
	}

}
