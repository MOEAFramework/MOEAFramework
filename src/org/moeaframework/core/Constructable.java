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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringTokenizer;
import org.moeaframework.util.Iterators;

/**
 * Interface for objects that can be constructed from a string representation.  The string representation mimics a Java
 * constructor call, such as:
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
 *   <li>The first constructor that matches the supplied arguments is called; therefore, avoid defining multiple
 *       constructors with overlapping types (e.g., int and double).
 * </ol>
 * While we recommend only using primitive types for the constructor arguments, any type implementing {@code toString()}
 * and a static {@code valueOf(String)} method is supported.  These types should throw {@link IllegalArgumentException}
 * if the supplied string is not compatible with the type.
 */
public interface Constructable {

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
	 * @param <T> the type
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
	 * @param <T> the type
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
		sb.append(toClassName(parentType, instanceType));
		
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
			definition = returnType.getPackageName() + "." + definition;
		}

		// locate the class
		try {
			Class<?> definitionClass = Class.forName(definition);

			outer: for (Constructor<?> constructor : getOrderedConstructors(definitionClass)) {
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
					return returnType.cast(ConstructorUtils.invokeConstructor(definitionClass, castArguments));
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
	
	private static List<Constructor<?>> getOrderedConstructors(Class<?> type) {
		List<Constructor<?>> result = new ArrayList<Constructor<?>>();
		
		for (Constructor<?> constructor : type.getConstructors()) {
			result.add(constructor);
		}
		
		result.sort(new ConstructorComparator());
		
		return result;
	}
	
	static class ConstructorComparator implements Comparator<Constructor<?>> {
		
		private static final Map<Class<?>, Integer> ORDER;
		
		private static final int LOWEST_ORDER = 999;
		
		static {
			ORDER = new HashMap<Class<?>, Integer>();
			ORDER.put(byte.class, 0);
			ORDER.put(Byte.class, 0);
			ORDER.put(char.class, 1);
			ORDER.put(Character.class, 1);
			ORDER.put(short.class, 2);
			ORDER.put(Short.class, 2);
			ORDER.put(int.class, 3);
			ORDER.put(Integer.class, 3);
			ORDER.put(long.class, 4);
			ORDER.put(Long.class, 4);
			ORDER.put(float.class, 5);
			ORDER.put(Float.class, 5);
			ORDER.put(double.class, 6);
			ORDER.put(Double.class, 6);
			ORDER.put(String.class, 7);
			ORDER.put(Object.class, LOWEST_ORDER);
		}

		@Override
		public int compare(Constructor<?> c1, Constructor<?> c2) {
			if (c1.getParameterCount() != c2.getParameterCount()) {
				return Integer.compare(c1.getParameterCount(), c2.getParameterCount());
			}
			
			for (Pair<Parameter, Parameter> pair : Iterators.zip(c1.getParameters(), c2.getParameters())) {
				if (pair.getLeft().getType() != pair.getRight().getType()) {
					return Integer.compare(
							ORDER.getOrDefault(pair.getLeft().getType(), LOWEST_ORDER),
							ORDER.getOrDefault(pair.getRight().getType(), LOWEST_ORDER));
				}
			}
			
			return 0;
		}
		
	}

}
