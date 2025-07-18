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

import java.lang.reflect.InvocationTargetException;

import org.moeaframework.util.ReflectionUtils;
import org.moeaframework.util.io.Tokenizer;

/**
 * Interface for objects that can be defined and reconstructed using a string representation.  The string
 * representation mimics a Java constructor call, such as:
 * <pre>
 *   "Minimize"
 *   "org.moeaframework.core.objective.Minimize"
 *   "LessThan(2.0)"
 *   "org.moeaframework.core.constraint.LessThan(2.0)"
 * </pre>
 * There are a few key differences, including:
 * <ol>
 *   <li>Either the class' simple name or fully-qualified name that includes the package is permitted.  The class
 *       must reside in the same package as the return type in order to use the shorter simple name.
 *   <li>Parenthesis can be omitted if using the no-arg constructor.
 *   <li>Duck typing is used, meaning the string {@code "2"} could be considered a string, an integer, or a double.
 *       Therefore, avoid defining multiple constructors with indistinguishable representations.
 * </ol>
 * While we recommend only using primitive types for the constructor arguments, any type implementing {@code toString()}
 * and a static {@code valueOf(String)} method is supported.  These types should throw {@link IllegalArgumentException}
 * if the supplied string is not compatible with the type.
 */
public interface Defined {

	/**
	 * Returns the string representation, or definition, of this object.
	 * 
	 * @return the definition
	 */
	public String getDefinition();
	
	/**
	 * Trims off the package name if the instance is contained within the same package as the parent.
	 * 
	 * @param <T> the type
	 * @param parentType the parent type, which if {@code null} will always produce a fully-qualified class name
	 * @param instanceType the instance type
	 * @return the relative class name
	 */
	private static <T> String toClassName(Class<T> parentType, Class<? extends T> instanceType) {
		String name = instanceType.getName();
		String prefix = parentType == null ? "" : parentType.getPackageName() + ".";
		
		if (name.startsWith(prefix)) {
			name = name.substring(prefix.length());
		}
				
		return name;
	}
	
	/**
	 * Creates a string representation the displays the type but indicates it is not supported.
	 * 
	 * @param instanceType the instance type
	 * @return the string representation
	 */
	public static String createUnsupportedDefinition(Class<?> instanceType) {
		return createUnsupportedDefinition(null, instanceType);
	}
	
	/**
	 * Creates a string representation the displays the type but indicates it is not supported.
	 * 
	 * @param <T> the type
	 * @param parentType the parent type, which if {@code null} will always produce a fully-qualified class name
	 * @param instanceType the instance type
	 * @return the string representation
	 */
	public static <T> String createUnsupportedDefinition(Class<T> parentType, Class<? extends T> instanceType) {
		return "!" + toClassName(parentType, instanceType);
	}
	
	/**
	 * Creates a string representation based on the given parent and instance type along with the constructor
	 * arguments.
	 * 
	 * @param instanceType the instance type
	 * @param arguments the arguments, if any, passed to the constructor
	 * @return the string representation
	 */
	public static String createDefinition(Class<?> instanceType, Object... arguments) {
		return createDefinition(null, instanceType, arguments);
	}
	
	/**
	 * Creates a string representation based on the given parent and instance type along with the constructor
	 * arguments.
	 * 
	 * @param <T> the type
	 * @param parentType the parent type, which if {@code null} will always produce a fully-qualified class name
	 * @param instanceType the instance type
	 * @param arguments the arguments, if any, passed to the constructor
	 * @return the string representation
	 */
	public static <T> String createDefinition(Class<T> parentType, Class<? extends T> instanceType,
			Object... arguments) {
		StringBuilder sb = new StringBuilder();
		
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setDelimiter(',');
		
		sb.append(toClassName(parentType, instanceType));
		
		if (arguments != null && arguments.length > 0) {			
			sb.append("(");
			
			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					sb.append(tokenizer.getDelimiter());
				}
				
				if (Settings.isVerbose() && !ReflectionUtils.isImplemented(arguments[i].getClass(), "toString")) {
					System.err.println("WARNING: " + arguments[i].getClass() + " does not implement toString()");
				}
				
				String token = tokenizer.escape(arguments[i].toString());
				
				if (arguments[i] instanceof String) {
					token = "\"" + token + "\"";
				}
				
				sb.append(token);
			}
			
			sb.append(")");
		}
		
		return sb.toString();
	}

	/**
	 * Reconstructs the object using its string representation produced by {@link #getDefinition()}.
	 * 
	 * @param <T> the type
	 * @param returnType the return type
	 * @param definition the string representation of the object
	 * @return the reconstructed object, or {@code null} if reconstruction is not supported
	 */
	public static <T> T createInstance(Class<T> returnType, String definition) {
		// split out arguments
		String[] arguments = new String[0];
		int startIndex = definition.indexOf('(');
		int endIndex = definition.lastIndexOf(')');

		if (startIndex > 0 && endIndex > startIndex) {
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.setDelimiter(',');
			arguments = tokenizer.decodeToArray(definition.substring(startIndex + 1, endIndex));
			
			definition = definition.substring(0, startIndex);
		}
		
		if (definition.startsWith("!")) {
			return null;
		}

		// convert to a fully-qualified class name
		if (!definition.contains(".")) {
			definition = returnType.getPackageName() + "." + definition;
		}
		
		// strip off any quotes
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].startsWith("\"") && arguments[i].endsWith("\"")) {
				arguments[i] = arguments[i].substring(1, arguments[i].length() - 1);
			}
		}

		// locate and instantiate the class
		try {
			Class<?> definitionClass = Class.forName(definition);
			return returnType.cast(ReflectionUtils.invokeConstructor(definitionClass, (Object[])arguments));
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			throw new FrameworkException("Failed to create " + definition, e);
		}
	}

}
