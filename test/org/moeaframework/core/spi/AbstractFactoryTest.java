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
package org.moeaframework.core.spi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * General tests for SPI factories.
 */
@Ignore("Abstract test class")
public abstract class AbstractFactoryTest<T, S extends AbstractFactory<T>> {
	
	/**
	 * Returns the type of the provider.
	 */
	public abstract Class<T> getProviderType();
	
	/**
	 * Returns the type of the factory.
	 */
	public abstract Class<S> getFactoryType();
	
	/**
	 * Creates a new instance of the factory.
	 */
	public S createFactory() {
		try {
			Class<S> type = getFactoryType();
			return type.cast(MethodUtils.invokeStaticMethod(type, "getInstance"));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}

	@Test
	public void testDefaultProviders() throws IOException {
		String resource = "/META-INF/services/" + getProviderType().getName();
		
		try (LineReader lineReader = Resources.asLineReader(Settings.class, resource, ResourceOption.REQUIRED).skipComments()) {
			for (String line : lineReader) {
				Assert.assertTrue("Provider " + line + " not found", createFactory().hasProvider(line));
			}
		}
	}
	
	@Test
	public void testHasProviderNotFound() throws IOException {
		Assert.assertFalse(createFactory().hasProvider("providerThatDoesNotExist"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInstanceThrowsIfNull() throws Throwable {
		try {
			MethodUtils.invokeStaticMethod(getFactoryType(), "setInstance", (S)null);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
	
	@Test
	public void testGetInstanceHasDefault() {
		Assert.assertNotNull(createFactory());
	}

	@Test
	public void testSignature() {
		Class<S> type = getFactoryType();
		
		Method getInstance = MethodUtils.getAccessibleMethod(type, "getInstance");
		Assert.assertEquals("getInstance must return " + type, type, getInstance.getReturnType());
		Assert.assertTrue("getInstance must be public", Modifier.isPublic(getInstance.getModifiers()));
		Assert.assertTrue("getInstance must be static", Modifier.isStatic(getInstance.getModifiers()));
		Assert.assertTrue("getInstance must be synchronized", Modifier.isSynchronized(getInstance.getModifiers()));
		
		Method setInstance = MethodUtils.getAccessibleMethod(type, "setInstance", type);
		Assert.assertTrue("setInstance must be public", Modifier.isPublic(setInstance.getModifiers()));
		Assert.assertTrue("setInstance must be static", Modifier.isStatic(setInstance.getModifiers()));
		Assert.assertTrue("setInstance must be synchronized", Modifier.isSynchronized(setInstance.getModifiers()));
		
		Assert.assertTrue("Factories must have at least one public, synchronized getter.  This is necessary to " +
				"ensure access to the ServiceLoader is thread-safe.",
				Stream.of(type.getMethods()).anyMatch(m ->
					m.getName().startsWith("get") && Modifier.isPublic(m.getModifiers()) &&
					!Modifier.isStatic(m.getModifiers()) && Modifier.isSynchronized(m.getModifiers())));
	}
	
}
