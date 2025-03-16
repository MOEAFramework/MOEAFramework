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

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.PropertyNotFoundException;
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
		
		Assert.assertContains(reference.fields(), "STRING");
		Assert.assertContains(reference.fields(), "INT");
		Assert.assertEquals("foo", reference.get("STRING"));
		Assert.assertEquals("5", reference.get("INT"));
		
		Assert.assertThrows(PropertyNotFoundException.class, () -> reference.get("missing"));
	}
	
	@Test
	public void testWith() {
		Reference extendedReference = reference.with("newKey", "newVal");
		
		Assert.assertSize(3, extendedReference.fields());
		Assert.assertContains(extendedReference.fields(), "string");
		Assert.assertContains(extendedReference.fields(), "int");
		Assert.assertContains(extendedReference.fields(), "newKey");
		Assert.assertEquals("foo", extendedReference.get("string"));
		Assert.assertEquals("5", extendedReference.get("int"));
		Assert.assertEquals("newVal", extendedReference.get("newKey"));
		
		Assert.assertContains(reference.fields(), "STRING");
		Assert.assertContains(reference.fields(), "INT");
		Assert.assertContains(extendedReference.fields(), "NEWKEY");
		Assert.assertEquals("foo", reference.get("STRING"));
		Assert.assertEquals("5", reference.get("INT"));
		Assert.assertEquals("newVal", extendedReference.get("NEWKEY"));
		
		Assert.assertThrows(PropertyNotFoundException.class, () -> reference.get("missing"));
	}
	
	@Test
	public void testOverwrite() {
		Reference overwriteReference = reference.with("string", "bar");
		
		Assert.assertSize(2, overwriteReference.fields());
		Assert.assertContains(overwriteReference.fields(), "string");
		Assert.assertContains(overwriteReference.fields(), "int");
		Assert.assertEquals("bar", overwriteReference.get("string"));
		Assert.assertEquals("5", overwriteReference.get("int"));
		
		Assert.assertContains(overwriteReference.fields(), "STRING");
		Assert.assertContains(overwriteReference.fields(), "INT");
		Assert.assertEquals("bar", overwriteReference.get("STRING"));
		Assert.assertEquals("5", overwriteReference.get("INT"));
	}
	
	@Test
	public void testRoot() {
		Reference root = Reference.root();
		
		Assert.assertNotNull(root);
		Assert.assertSize(0, root.fields());
		Assert.assertTrue(root.isRoot());
		
		Assert.assertThrows(PropertyNotFoundException.class, () -> root.get("missing"));
	}
	
	@Test
	public void testEqualsAndHashCode() {
		Reference reference1 = Reference.of("string", "foo");
		Reference reference2 = Reference.of("STRING", "FOO");
		Reference reference3 = Reference.of("string", "bar");
		
		Assert.assertNotEquals(reference1, null);
		Assert.assertEquals(reference1, reference1);
		Assert.assertEquals(reference1, reference2);
		Assert.assertNotEquals(reference1, reference3);
		
		Assert.assertEquals(reference1.hashCode(), reference2.hashCode());
		Assert.assertNotEquals(reference1.hashCode(), reference3.hashCode());
	}
	
	@Test
	public void testNormalize() throws IOException {
		Assert.assertEquals(Reference.normalize("abcdefghijklmnopqrstuvwxyz"), Reference.normalize("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		Assert.assertEquals(Reference.normalize("abcçdefgğhıijklmnoöprsştuüvyz"), Reference.normalize("ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZ"));
	}

}
