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
package org.moeaframework.analysis.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.TypedProperties;

public class ReferenceTest {
	
	private Reference reference;
	
	@Before
	public void setUp() {
		TypedProperties properties = new TypedProperties();
		properties.setString("string", "foo");
		properties.setInt("int", 5);
		
		reference = Reference.of(properties);
	}
	
	@After
	public void tearDown() {
		reference = null;
	}

	@Test
	public void test() {
		Assert.assertSize(2, reference.fields());
		Assert.assertContains(reference.fields(), "string");
		Assert.assertContains(reference.fields(), "int");
		Assert.assertEquals("foo", reference.get("string"));
		Assert.assertEquals("5", reference.get("int"));
	}
	
	@Test
	public void testExtend() {
		Reference extendedReference = reference.extend("newKey", "newVal");
		
		Assert.assertSize(3, extendedReference.fields());
		Assert.assertContains(extendedReference.fields(), "string");
		Assert.assertContains(extendedReference.fields(), "int");
		Assert.assertContains(extendedReference.fields(), "newKey");
		Assert.assertEquals("foo", extendedReference.get("string"));
		Assert.assertEquals("5", extendedReference.get("int"));
		Assert.assertEquals("newVal", extendedReference.get("newKey"));
	}
	
	@Test
	public void testOverwrite() {
		Reference overwriteReference = reference.extend("string", "bar");
		
		Assert.assertSize(2, overwriteReference.fields());
		Assert.assertContains(overwriteReference.fields(), "string");
		Assert.assertContains(overwriteReference.fields(), "int");
		Assert.assertEquals("bar", overwriteReference.get("string"));
		Assert.assertEquals("5", overwriteReference.get("int"));
	}

}
