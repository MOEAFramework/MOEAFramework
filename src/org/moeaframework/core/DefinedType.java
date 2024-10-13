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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.StringTokenizer;

/**
 * Interface for objects that can be reconstructed from a string representation.
 * <p>
 * The string representation mimics a Java constructor call, with a few differences.  It begins with the class' simple
 * name or fully-qualified name (i.e., including the package), followed by an optional list of arguments in
 * parenthesis.  For example, all of the following are valid representations:
 * <pre>
 *   "Minimize"
 *   "org.moeaframework.core.objective.Minimize"
 *   "LessThan(2.0)"
 *   "org.moeaframework.core.constraint.LessThan(2.0)"
 * </pre>
 * <p>
 * The {@link #createInstance(Class, String)} method is used to reconstruct the class from this string representation.
 * Each constructor is checked to see if the given arguments can be converted into the required parameter type.  The
 * type must implement a static {@code valueOf(String)} method, which includes all primitives.
 * <p>
 * While this shares some similarities with {@link java.io.Serializable}, the two serve different purposes.
 * Serialization produces a binary encoding of the entire state of an object, whereas this class produces a
 * human-readable string that uses constructors to recreate the object.
 */
public interface DefinedType {

	/**
	 * Returns the string representation, or definition, of this object.
	 * 
	 * @return the definition
	 */
	public String getDefinition();
	
	private static <T> String getRelativeClassPath(Class<T> parentType, Class<? extends T> instanceType) {
		String name = instanceType.getName();
		String prefix = parentType.getPackageName() + ".";
		
		if (name.startsWith(prefix)) {
			name = name.substring(prefix.length());
		}
		
		return name;
	}
	
	/**
	 * Creates a string representation the displays the type but indicates it is not supported.
	 * 
	 * @param <T> the expected type
	 * @param parentType the parent type
	 * @param instanceType the instance type
	 * @return the string representation
	 */
	public static <T> String createUnsupportedDefinition(Class<T> parentType, Class<? extends T> instanceType) {
		return "!" + getRelativeClassPath(parentType, instanceType);
	}
	
	/**
	 * Creates a string representation based on the given parent and instance type along with the constructor
	 * arguments.
	 * 
	 * @param <T> the expected type
	 * @param parentType the parent type
	 * @param instanceType the instance type
	 * @param arguments the arguments, if any, passed to the constructor
	 * @return the string representation
	 */
	public static <T> String createDefinition(Class<T> parentType, Class<? extends T> instanceType,
			Object... arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append(getRelativeClassPath(parentType, instanceType));
		
		if (arguments != null && arguments.length > 0) {
			sb.append("(");
			
			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					sb.append(",");
				}
				
				if (MethodUtils.getMatchingMethod(arguments[i].getClass(), "toString").getDeclaringClass() == Object.class) {
					System.err.println(instanceType + " does not override toString()");
				}
				
				String str = arguments[i].toString();
				
				if (str.contains(",")) {
					str = '"' + str + '"';
				}
				
				sb.append(str);
			}
			
			sb.append(")");
		}
		
		return sb.toString();
	}

	/**
	 * Reconstructs the object using its string representation produced by {@link #getDefinition()}.
	 * 
	 * @param <T> the expected type
	 * @param type the class of the type
	 * @param definition the string representation of the object
	 * @return the reconstructed object, or {@code null} if reconstruction is not supported
	 */
	public static <T> T createInstance(Class<T> type, String definition) {
		// split out arguments
		String[] arguments = new String[0];
		int startIndex = definition.indexOf('(');
		int endIndex = definition.lastIndexOf(')');

		if (startIndex > 0 && endIndex > startIndex) {
			StringTokenizer tokenizer = new StringTokenizer(definition.substring(startIndex + 1, endIndex));
			tokenizer.setDelimiterChar(',');
			tokenizer.setQuoteChar('"');
			arguments = tokenizer.getTokenArray();
			
			definition = definition.substring(0, startIndex);
		}
		
		if (definition.startsWith("!")) {
			return null;
		}

		// convert to a fully-qualified class name
		if (!definition.contains(".")) {
			definition = type.getPackageName() + "." + definition;
		}

		// locate the class
		try {
			Class<?> definitionClass = Class.forName(definition);

			outer: for (Constructor<?> constructor : definitionClass.getConstructors()) {
				if (constructor.getParameterCount() != arguments.length) {
					continue;
				}
				
				Parameter[] parameters = constructor.getParameters();
				Object[] castArguments = new Object[arguments.length];

				for (int i = 0; i < arguments.length; i++) {
					Class<?> parameterType = parameters[i].getType();
					
					if (parameterType.isPrimitive()) {
						parameterType = ClassUtils.primitiveToWrapper(parameterType);
					}

					try {
						castArguments[i] = MethodUtils.invokeStaticMethod(parameterType, "valueOf", arguments[i]);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
							NoSuchMethodException | SecurityException e) {
						continue outer;
					}
				}
				
				try {
					return type.cast(ConstructorUtils.invokeConstructor(definitionClass, castArguments));
				} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | 
						InvocationTargetException | NoSuchMethodException e) {
					continue;
				}
			}
		} catch (ClassNotFoundException | SecurityException e) {
			throw new FrameworkException(definition + " is not a recognized or accessible class", e);
		}
		
		throw new FrameworkException("unable to create " + definition + ", no constructor found matching arguments " +
				Arrays.toString(arguments));
	}

}
