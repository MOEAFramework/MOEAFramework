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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.TypedProperties;

public class PropertyScopeTest {

	private TypedProperties original;

	@Before
	public void setUp() {
		original = new TypedProperties();
		original.setString("foo", "bar");
	}

	@After
	public void tearDown() {
		original = null;
	}
	
	private TypedProperties createTestProperties() {
		TypedProperties properties = new TypedProperties();
		properties.addAll(original);
		return properties;
	}
	
	private void assertOriginalProperties(TypedProperties properties) {
		Assert.assertEquals(properties, original);
	}
	
	@Test
	public void testNoChanges() {
		TypedProperties properties = createTestProperties();
		
		try (PropertyScope scope = properties.createScope()) {
			// do nothing
		}
		
		assertOriginalProperties(properties);
	}
	
	@Test
	public void testOverwriteValue() {
		TypedProperties properties = createTestProperties();
		Assert.assertEquals("bar", properties.getString("foo"));
		
		try (PropertyScope scope = properties.createScope().with("foo", "baz")) {
			Assert.assertEquals("baz", properties.getString("foo"));
		}
		
		assertOriginalProperties(properties);
	}
	
	@Test
	public void testNewValue() {
		TypedProperties properties = createTestProperties();
		
		try (PropertyScope scope = properties.createScope().with("new", "value")) {
			Assert.assertEquals("value", properties.getString("new"));
		}
		
		assertOriginalProperties(properties);
	}
	
	@Test
	public void testRemoveValue() {
		TypedProperties properties = createTestProperties();
		
		try (PropertyScope scope = properties.createScope().without("foo")) {
			Assert.assertFalse(properties.contains("foo"));
		}
		
		assertOriginalProperties(properties);
	}

}
