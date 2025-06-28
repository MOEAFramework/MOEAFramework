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
package org.moeaframework.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.util.validate.Validate;

/**
 * Collection of utility methods using Java's reflection API to provide dynamic type conversion and method invocation.
 * <p>
 * <strong>Caution:</strong> this provides broader type conversions beyond what is defined in the Java Language
 * Specification, which has limited widening and autoboxing rules.
 * <p>
 * Specifically, this allows:
 * <ol>
 *   <li>Widening numbers (e.g., Integer => Long or Float => Double)
 *   <li>Shortening numbers (e.g., Long => Integer or Double => Float), when the value fits within the type bounds
 *   <li>Floating-point expansion (e.g., Integer => Double)
 *   <li>String conversion for numeric types (e.g., "5.0" => Double)
 *   <li>ValueOf conversion for classes implementing a static {@code valueOf} method
 * </ol>
 * This can result in ambiguity regarding which method or constructor matches a given set of arguments.  For instance,
 * the string {@code "5"} can be converted into any numeric type.  Therefore, a heuristic in the form of a "conversion
 * distance" is calculated, whereby candidates with smaller distances are preferred.
 */
public class ReflectionUtils {
	
	private ReflectionUtils() {
		super();
	}

	/**
	 * Invokes the class constructor matching the given arguments.
	 * 
	 * @param <T> the class type
	 * @param cls the class
	 * @param args the arguments passed to the constructor
	 * @return the new instance
	 * @throws NoSuchMethodException if no constructor matched the arguments
	 * @throws InstantiationException if the class could not be instantiated
	 * @throws InvocationTargetException if an exception was thrown by the constructor
	 */
	public static <T> T invokeConstructor(Class<T> cls, Object... args) throws NoSuchMethodException,
	InstantiationException, InvocationTargetException {
		MatchedConstructor<T> constructor = MatchedConstructor.of(cls, args);

		if (constructor == null) {
			throw new NoSuchMethodException("No constructor found in class " +
					cls.getName() + " with parameters matching " + Arrays.toString(args));
		}

		return constructor.invoke();
	}

	/**
	 * Invokes an instance method matching the given arguments.
	 * 
	 * @param instance the instance
	 * @param methodName the method name
	 * @param args the arguments passed to the method
	 * @return the return value of the method, or {@code null} for void
	 * @throws NoSuchMethodException if no method matched the name or arguments
	 * @throws InvocationTargetException if an exception was thrown by the method
	 */
	public static Object invokeMethod(Object instance, String methodName, Object... args) throws NoSuchMethodException,
	InvocationTargetException {
		MatchedMethod<?> method = MatchedMethod.of(instance.getClass(), instance, methodName, args);

		if (method == null) {
			throw new NoSuchMethodException("No method '" + methodName + "' found in class " +
					instance.getClass().getName() + " with parameters matching " + Arrays.toString(args));
		}

		return method.invoke();
	}

	/**
	 * Invokes a static method matching the given arguments.
	 * 
	 * @param cls the class
	 * @param methodName the method name
	 * @param args the arguments passed to the method
	 * @return the return value of the method, or {@code null} for void
	 * @throws NoSuchMethodException if no static method matched the name or arguments
	 * @throws InvocationTargetException if an exception was thrown by the method
	 */
	public static Object invokeStaticMethod(Class<?> cls, String methodName, Object... args) throws
	NoSuchMethodException, InvocationTargetException {
		MatchedMethod<?> method = MatchedMethod.of(cls, null, methodName, args);

		if (method == null) {
			throw new NoSuchMethodException("No static method '" + methodName + "' found in class " +
					cls.getName() + " with parameters matching " + Arrays.toString(args));
		}

		return method.invoke();
	}
	
	static class MatchedConstructor<T> {
		
		private final Class<T> cls;
				
		private final Constructor<?> constructor;
		
		private final MatchedParameter<?, ?>[] parameters;
		
		public MatchedConstructor(Class<T> cls, Constructor<?> constructor, MatchedParameter<?, ?>[] parameters) {
			super();
			this.cls = cls;
			this.constructor = constructor;
			this.parameters = parameters;
		}

		public Constructor<?> getConstructor() {
			return constructor;
		}
		
		public int getDistance() {
			return Stream.of(parameters).mapToInt(MatchedParameter::getDistance).sum();
		}
		
		public T invoke() throws InstantiationException, InvocationTargetException {
			try {
				return cls.cast(constructor.newInstance(Stream.of(parameters).map(p -> p.getCastValue()).toArray()));
			} catch (IllegalAccessException e) {
				// Should not occur since we filter by accessibility?
				throw new FrameworkException("Unable to reflexively access constructor", e);
			}
		}
		
		private static <T> MatchedConstructor<T> tryMatch(Class<T> cls, Constructor<?> constructor, Object... args) {
			Parameter[] parameters = constructor.getParameters();
			MatchedParameter<?, ?>[] matchedParameters = new MatchedParameter<?, ?>[parameters.length];
			
			if (parameters.length != args.length) {
				return null;
			}
			
			for (int i = 0; i < parameters.length; i++) {
				MatchedParameter<?, ?> matchedParameter = MatchedParameter.of(args[i],
						ClassUtils.primitiveToWrapper(parameters[i].getType()));
						
				if (matchedParameter == null) {
					return null;
				}
						
				matchedParameters[i] = matchedParameter;
			}
					
			return new MatchedConstructor<>(cls, constructor, matchedParameters);
		}
		
		public static <T> MatchedConstructor<T> of(Class<T> cls, Object... args) {
			// Typing here is a bit odd since getDeclaredConstructors() returns the generic type ? instead of T.  We
			// thus track the class and perform a cast before returning the new instance.
			Optional<MatchedConstructor<T>> match = Stream.of(cls.getDeclaredConstructors())
					.filter(constructor -> constructor.canAccess(null))
					.map(constructor -> tryMatch(cls, constructor, args))
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(MatchedConstructor::getDistance))
					.findFirst();
			
			if (match.isEmpty()) {
				return null;
			}
			
			return match.get();
		}
		
	}
	
	static class MatchedMethod<T> {
		
		private final T instance;
		
		private final Method method;
		
		private final MatchedParameter<?, ?>[] parameters;
		
		public MatchedMethod(T instance, Method method, MatchedParameter<?, ?>[] parameters) {
			super();
			this.instance = instance;
			this.method = method;
			this.parameters = parameters;
		}
		
		public T getInstance() {
			return instance;
		}
		
		public Method getMethod() {
			return method;
		}
		
		public int getDistance() {
			return Stream.of(parameters).mapToInt(MatchedParameter::getDistance).sum();
		}
		
		public Object invoke() throws InvocationTargetException {
			try {
				return method.invoke(instance, Stream.of(parameters).map(p -> p.getCastValue()).toArray());
			} catch (IllegalAccessException e) {
				// Should not occur since we filter by accessibility?
				throw new FrameworkException("Unable to reflexively access method", e);
			}
		}
		
		private static <T> MatchedMethod<T> tryMatch(T instance, Method method, Object... args) {
			Parameter[] parameters = method.getParameters();
			MatchedParameter<?, ?>[] matchedParameters = new MatchedParameter<?, ?>[parameters.length];
			
			if (args.length < parameters.length) {
				return null;
			}
			
			if (!method.isVarArgs() && args.length > parameters.length) {
				return null;
			}
			
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				
				if (parameter.isVarArgs()) {
					Object array = Array.newInstance(parameters[i].getType().getComponentType(), args.length - i);
					
					for (int j = i; j < args.length; j++) {
						MatchedParameter<?, ?> matchedVarArgsParameter = MatchedParameter.of(args[j],
								ClassUtils.primitiveToWrapper(parameter.getType().getComponentType()));
						
						if (matchedVarArgsParameter == null) {
							return null;
						}
						
						Array.set(array, j - i, matchedVarArgsParameter.getCastValue());
					}
					
					matchedParameters[i] = MatchedParameter.of(array, parameter.getType());
				} else {
					MatchedParameter<?, ?> matchedParameter = MatchedParameter.of(args[i],
							ClassUtils.primitiveToWrapper(parameter.getType()));
							
					if (matchedParameter == null) {
						return null;
					}
							
					matchedParameters[i] = matchedParameter;
				}
			}
					
			return new MatchedMethod<>(instance, method, matchedParameters);
		}
		
		public static <T> MatchedMethod<T> of(Class<?> cls, T instance, String methodName, Object... args) {
			Optional<MatchedMethod<T>> match = Stream.of(cls.getDeclaredMethods())
					.filter(method -> method.getName().equals(methodName))
					.filter(method -> instance == null ^ !Modifier.isStatic(method.getModifiers()))
					.filter(method -> method.canAccess(instance))
					.map(method -> tryMatch(instance, method, args))
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(MatchedMethod::getDistance))
					.findFirst();
			
			if (match.isEmpty()) {
				if (cls.getSuperclass() == null) {
					return null;
				} else {
					return of(cls.getSuperclass(), instance, methodName, args);
				}
			}
			
			return match.get();
		}
		
	}
	
	static class MatchedParameter<T, R> {
		
		/**
		 * Penalty applied when calling the {@code valueOf} method to convert or parse a value.
		 */
		private static final int VALUEOF_CONVERSION = 10;
		
		/**
		 * Penalty applied when converting a whole number into a floating-point number.
		 */
		private static final int FLOATING_POINT_EXPANSION = 5;
		
		private final T originalValue;
		
		private final R castValue;
		
		private final int distance;
		
		private MatchedParameter(T originalValue, R castValue, int distance) {
			super();
			this.originalValue = originalValue;
			this.castValue = castValue;
			this.distance = distance;
		}

		public T getOriginalValue() {
			return originalValue;
		}

		public R getCastValue() {
			return castValue;
		}

		public int getDistance() {
			return distance;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("(");
			
			if (originalValue instanceof String) {
				sb.append("\"");
				sb.append(originalValue);
				sb.append("\"");
			} else {
				sb.append(originalValue);
			}
			
			if (distance > 0) {
				sb.append(", ");
				sb.append(originalValue.getClass().getSimpleName());
				sb.append(" => ");
				sb.append(castValue.getClass().getSimpleName());
				sb.append(" +");
				sb.append(distance);
			}

			sb.append(")");
			return sb.toString();
		}
		
		public static <T, R> MatchedParameter<T, R> of(T originalValue, Class<R> toClass) {
			if (toClass == null || originalValue == null) {
				return null;
			}
			
			Object castValue = originalValue;
			int distance = 0;
						
			try {
				if (toClass.isInstance(castValue)) {
					// do nothing
				} else if (Double.class.equals(toClass)) {
					if (castValue instanceof String string) {
						try {
							castValue = Long.valueOf(string);
						} catch (NumberFormatException e) {
							castValue = Double.valueOf(string);
						}
						
						distance += VALUEOF_CONVERSION;
					}
					
					if (castValue instanceof Number value) {
						double doubleValue = value.doubleValue();
						
						if (isWholeNumber(value)) {
							distance += FLOATING_POINT_EXPANSION;
						}
						
						castValue = doubleValue;
						distance += getConversionDistance(value, doubleValue);
					}
				} else if (Float.class.equals(toClass)) {
					if (castValue instanceof String string) {
						try {
							castValue = Long.valueOf(string);
						} catch (NumberFormatException e) {
							castValue = Float.valueOf(string);
						}
						
						distance += VALUEOF_CONVERSION;
					}
					
					if (castValue instanceof Number value) {
						float floatValue = value.floatValue();
						
						if (isWholeNumber(value)) {
							distance += FLOATING_POINT_EXPANSION;
						}
						
						if (castValue instanceof Double) {
							double doubleValue = value.doubleValue();
							
							if (Double.isInfinite(doubleValue) || Double.isNaN(doubleValue)) {
								// allow conversion as-is
							} else if (doubleValue > Float.MAX_VALUE || doubleValue < -Float.MAX_VALUE) {
								// exceeds range of float
								return null;
							} else if (Math.abs(doubleValue - floatValue) > Settings.EPS && Settings.isVerbose()) {
								System.err.println("Conversion from " + Double.class.getSimpleName() + " to " +
										Float.class.getSimpleName() + " results in a loss of precision (" +
										Math.abs(doubleValue - floatValue) + ")");
							}
						}
						
						castValue = floatValue;
						distance += getConversionDistance(value, floatValue);
					}
				} else if (Long.class.equals(toClass)) {
					if (castValue instanceof String string) {
						castValue = Long.valueOf(string);
						distance += VALUEOF_CONVERSION;
					} 
					
					if (castValue instanceof Number value) {
						if (isFloatingPoint(value)) {
							return null;
						}
						
						long longValue = value.longValue();
						castValue = longValue;
						distance += getConversionDistance(value, longValue);
					} 
				} else if (Integer.class.equals(toClass)) {
					if (castValue instanceof String string) {
						castValue = Integer.valueOf(string);
						distance += VALUEOF_CONVERSION;
					}
										
					if (castValue instanceof Number value) {
						if (isFloatingPoint(value) || value.longValue() > Integer.MAX_VALUE || 
								value.longValue() < Integer.MIN_VALUE) {
							return null;
						}
						
						int intValue = value.intValue();
						castValue = intValue;
						distance += getConversionDistance(value, intValue);
					}
				} else if (Short.class.equals(toClass)) {
					if (castValue instanceof String string) {
						castValue = Short.valueOf(string);
						distance += VALUEOF_CONVERSION;
					}
					
					if (castValue instanceof Number value) {
						if (isFloatingPoint(value) || value.longValue() > Short.MAX_VALUE ||
								value.longValue() < Short.MIN_VALUE) {
							return null;
						}
						
						short shortValue = value.shortValue();
						castValue = shortValue;
						distance += getConversionDistance(value, shortValue);
					}
				} else if (Byte.class.equals(toClass)) {
					if (castValue instanceof String string) {
						castValue = Byte.valueOf(string);
						distance += VALUEOF_CONVERSION;
					}
					
					if (castValue instanceof Number value) {
						if (isFloatingPoint(value) || value.longValue() > Byte.MAX_VALUE ||
								value.longValue() < Byte.MIN_VALUE) {
							return null;
						}
						
						byte byteValue = value.byteValue();
						castValue = byteValue;
						distance += getConversionDistance(value, byteValue);
					}
				} else {
					try {
						castValue = MethodUtils.invokeStaticMethod(toClass, "valueOf", castValue);
						distance += VALUEOF_CONVERSION;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
							NoSuchMethodException | SecurityException e) {
						return null;
					}
				}
			} catch (NumberFormatException e) {
				return null;
			}

			try {
				return new MatchedParameter<T, R>(originalValue, toClass.cast(castValue), distance);
			} catch (ClassCastException e) {
				return null;
			}
		}
		
		private static boolean isFloatingPoint(Number value) {
			return value instanceof Float || value instanceof Double;
		}
		
		private static boolean isWholeNumber(Number value) {
			return value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
		}
		
		private static int getConversionDistance(Number originalValue, Number castValue) {
			return Math.abs(getByteSize(castValue) - getByteSize(originalValue));
		}
		
		private static int getByteSize(Number value) {
			if (value instanceof Byte) {
				return Byte.BYTES;
			} else if (value instanceof Short) {
				return Short.BYTES;
			} else if (value instanceof Integer) {
				return Integer.BYTES;
			} else if (value instanceof Long) {
				return Long.BYTES;
			} else if (value instanceof Float) {
				return Float.BYTES;
			} else if (value instanceof Double) {
				return Double.BYTES;
			} else {
				return Validate.that("value", value).fails("Not a supported numeric type");
			}
		}
		
	}

}
