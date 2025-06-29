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

import java.lang.reflect.InvocationTargetException;
import java.util.stream.IntStream;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.util.ReflectionUtils.MatchedConstructor;
import org.moeaframework.util.ReflectionUtils.MatchedMethod;
import org.moeaframework.util.ReflectionUtils.MatchedParameter;

public class ReflectionUtilsTest {
	
	@SuppressWarnings("unused")
	public static class ReflectedClass {
		
		public ReflectedClass() {
			super();
		}
		
		public ReflectedClass(int x) {
			
		}
		
		public void publicMethodNoArgs() {
			
		}
		
		public int publicMethodInt(int x) {
			return x;
		}
		
		public double publicMethodDouble(double x) {
			return x;
		}
		
		public String publicMethodString(String x) {
			return x;
		}
		
		protected int protectedMethodInt(int x) {
			return x;
		}
		
		private int privateMethodInt(int x) {
			return x;
		}
		
		public static int staticMethodInt(int x) {
			return x;
		}
		
		public int duplicateName(int x) {
			return x;
		}
		
		public double duplicateName(double x) {
			return x;
		}
		
		public int varArgs(int... xs) {
			return IntStream.of(xs).sum();
		}
		
		public static void main(String[] args) {
			
		}
		
	}
	
	public static class OverridesClass {
		
		@Override
		public String toString() {
			return "overridden";
		}
		
	}
	
	public static class ValueOfClass {
		
		private final String value;
		
		public ValueOfClass(String value) {
			super();
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ValueOfClass valueOf(String value) {
			return new ValueOfClass(value);
		}
		
	}
	
	@Test
	public void testIsImplemented() {
		Assert.assertFalse(ReflectionUtils.isImplemented(ReflectedClass.class, "toString"));
		Assert.assertTrue(ReflectionUtils.isImplemented(OverridesClass.class, "toString"));
	}
	
	@Test(expected = NoSuchMethodException.class)
	public void testInvokeConstructorMissing() throws NoSuchMethodException, InvocationTargetException, InstantiationException {
		ReflectionUtils.invokeConstructor(ReflectedClass.class, 1, 2, 3);
	}
	
	@Test(expected = NoSuchMethodException.class)
	public void testInvokeMethodMissing() throws NoSuchMethodException, InvocationTargetException {
		ReflectionUtils.invokeMethod(new ReflectedClass(), "missing");
	}
	
	@Test(expected = NoSuchMethodException.class)
	public void testInvokeStaticMethodMissing() throws NoSuchMethodException, InvocationTargetException {
		ReflectionUtils.invokeStaticMethod(ReflectedClass.class, "missing");
	}
	
	public static class MatchedConstructorTest {
		
		@Test
		public void test() throws InstantiationException, InvocationTargetException {
			MatchedConstructor<ReflectedClass> match = MatchedConstructor.of(ReflectedClass.class);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertInstanceOf(ReflectedClass.class, match.invoke());
			
			match = MatchedConstructor.of(ReflectedClass.class, 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertInstanceOf(ReflectedClass.class, match.invoke());
			
			match = MatchedConstructor.of(ReflectedClass.class, "5");
			Assert.assertNotNull(match);
			Assert.assertEquals(10, match.getDistance());
			Assert.assertInstanceOf(ReflectedClass.class, match.invoke());
		}
		
	}
	
	public static class MatchedMethodTest {
		
		@Test
		public void testInstanceMethod() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodNoArgs");
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertNull(match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodInt", 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodInt", "5");
			Assert.assertNotNull(match);
			Assert.assertEquals(10, match.getDistance());
			Assert.assertEquals(5, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodDouble", 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(9, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodDouble", 5.0);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodDouble", "5.0");
			Assert.assertNotNull(match);
			Assert.assertEquals(10, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "publicMethodString", "5");
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals("5", match.invoke());
		}
		
		@Test
		public void testProtectedMethod() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "protectedMethodInt", 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5, match.invoke());
		}
		
		@Test
		public void testStaticMethod() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, null, "staticMethodInt", 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5, match.invoke());
		}
		
		@Test
		public void testMainMethod() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, null, "main", (Object)new String[0]);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertNull(match.invoke());
		}
		
		@Test
		public void testPrivateMethod() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "privateMethodInt", 5);
			Assert.assertNull(match);
		}
		
		@Test
		public void testNoMatchingName() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "foo", 5);
			Assert.assertNull(match);
		}
		
		@Test
		public void testIncorrectModifier() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, null, "publicMethodInt", 5);
			Assert.assertNull(match);
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "staticMethodInt", 5);
			Assert.assertNull(match);
		}
		
		@Test
		public void testDuplicateName() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", 5);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", 5L);
			Assert.assertNotNull(match);
			Assert.assertEquals(4, match.getDistance());
			Assert.assertEquals(5, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", "5");
			Assert.assertNotNull(match);
			Assert.assertEquals(10, match.getDistance());
			Assert.assertEquals(5, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", 5.0f);
			Assert.assertNotNull(match);
			Assert.assertEquals(4, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", 5.0);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
			
			match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "duplicateName", "5.0");
			Assert.assertNotNull(match);
			Assert.assertEquals(10, match.getDistance());
			Assert.assertEquals(5.0, match.invoke());
		}
		
		@Test
		public void testVarArgs() throws InvocationTargetException {
			MatchedMethod<?> match = MatchedMethod.of(ReflectedClass.class, new ReflectedClass(), "varArgs", 1, 2, 3);
			Assert.assertNotNull(match);
			Assert.assertEquals(0, match.getDistance());
			Assert.assertEquals(6, match.invoke());
		}
		
	}
	
	public static class MatchedParameterTest {
		
		@Test
		public void testTypeConversion() {
			test((byte)5, Byte.class, (byte)5, 0);
			test((short)5, Byte.class, (byte)5, 1);
			test(5, Byte.class, (byte)5, 3);
			test(5L, Byte.class, (byte)5, 7);
			test("5", Byte.class, (byte)5, 10);
			
			test((byte)5, Short.class, (short)5, 1);
			test((short)5, Short.class, (short)5, 0);
			test(5, Short.class, (short)5, 2);
			test(5L, Short.class, (short)5, 6);
			test("5", Short.class, (short)5, 10);
			
			test((byte)5, Integer.class, 5, 3);
			test((short)5, Integer.class, 5, 2);
			test(5, Integer.class, 5, 0);
			test(5L, Integer.class, 5, 4);
			test("5", Integer.class, 5, 10);
			
			test((byte)5, Long.class, 5L, 7);
			test((short)5, Long.class, 5L, 6);
			test(5, Long.class, 5L, 4);
			test(5L, Long.class, 5L, 0);
			test("5", Long.class, 5L, 10);
			
			test((byte)5, Float.class, 5.0f, 8);
			test((short)5, Float.class, 5.0f, 7);
			test(5, Float.class, 5.0f, 5);
			test(5L, Float.class, 5.0f, 9);
			test(5.0f, Float.class, 5.0f, 0);
			test(5.0, Float.class, 5.0f, 4);
			test("5", Float.class, 5.0f, 19);
			test("5.0", Float.class, 5.0f, 10);
			
			test((byte)5, Double.class, 5.0, 12);
			test((short)5, Double.class, 5.0, 11);
			test(5, Double.class, 5.0, 9);
			test(5L, Double.class, 5.0, 5);
			test(5.0f, Double.class, 5.0, 4);
			test(5.0, Double.class, 5.0, 0);
			test("5", Double.class, 5.0, 15);
			test("5.0", Double.class, 5.0, 10);
		}
		
		@Test
		public void testInvalidConversions() {
			test(5.0f, Byte.class, null, -1);
			test(5.0f, Short.class, null, -1);
			test(5.0f, Integer.class, null, -1);
			test(5.0f, Long.class, null, -1);
			test(5.0, Byte.class, null, -1);
			test(5.0, Short.class, null, -1);
			test(5.0, Integer.class, null, -1);
			test(5.0, Long.class, null, -1);
			test("5.0", Byte.class, null, -1);
			test("5.0", Short.class, null, -1);
			test("5.0", Integer.class, null, -1);
			test("5.0", Long.class, null, -1);
			
			test(Short.MAX_VALUE, Byte.class, null, -1);
			test(Short.MIN_VALUE, Byte.class, null, -1);
			test(Integer.MAX_VALUE, Short.class, null, -1);
			test(Integer.MIN_VALUE, Short.class, null, -1);
			test(Long.MAX_VALUE, Integer.class, null, -1);
			test(Long.MIN_VALUE, Integer.class, null, -1);
			test(Float.MAX_VALUE, Long.class, null, -1);
			test(-Float.MAX_VALUE, Long.class, null, -1);
			test(Double.MAX_VALUE, Float.class, null, -1);
			test(-Double.MAX_VALUE, Float.class, null, -1);
		}
		
		@Test
		public void testSpecialCases() {
			test(Double.POSITIVE_INFINITY, Float.class, Float.POSITIVE_INFINITY, 4);
			test(Double.NEGATIVE_INFINITY, Float.class, Float.NEGATIVE_INFINITY, 4);
			test(Float.POSITIVE_INFINITY, Double.class, Double.POSITIVE_INFINITY, 4);
			test(Float.NEGATIVE_INFINITY, Double.class, Double.NEGATIVE_INFINITY, 4);
			
			test(Double.NaN, Float.class, Float.NaN, 4);
			test(Float.NaN, Double.class, Double.NaN, 4);
		}
		
		public <L, R> void test(L originalValue, Class<R> expectedType, R expectedValue, int expectedDistance) {
			MatchedParameter<L, R> match = MatchedParameter.of(originalValue, expectedType); 
			
			if (expectedValue == null) {
				Assert.assertNull(match);
			} else {
				Assert.assertNotNull(match);
				Assert.assertEquals(originalValue, match.getOriginalValue());
				Assert.assertEquals(expectedValue, match.getCastValue());
				Assert.assertEquals(expectedDistance, match.getDistance());
			}
		}
		
		@Test
		public void testValueOf() {
			MatchedParameter<String, ValueOfClass> match = MatchedParameter.of("test", ValueOfClass.class); 
			
			Assert.assertNotNull(match);
			Assert.assertEquals("test", match.getCastValue().getValue());
		}
		
	}
	
}
