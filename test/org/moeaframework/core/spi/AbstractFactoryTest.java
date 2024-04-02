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
package org.moeaframework.core.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;

/**
 * General tests for SPI factories.
 */
public abstract class AbstractFactoryTest<T, S extends AbstractFactory<T>> {
	
	/**
	 * Returns the type of the provider.
	 */
	public abstract Class<T> getProviderType();
	
	/**
	 * Creates a new instance of the factory.
	 */
	public abstract S createFactory();

	/**
	 * Validates that all providers listed in META-INF/services exist.
	 */
	@Test
	public void testDefaultProviders() throws IOException {
		try (InputStream stream = Settings.class.getResourceAsStream("/META-INF/services/" +
				getProviderType().getName())) {
			Assert.assertNotNull(stream);
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					System.out.println("Testing existence of provider " + line);
					Assert.assertTrue(createFactory().hasProvider(line));
				}
			}
		}
	}
	
	@Test
	public void testHasProviderNotFound() throws IOException {
		Assert.assertFalse(createFactory().hasProvider("providerThatDoesNotExist"));
	}

}
