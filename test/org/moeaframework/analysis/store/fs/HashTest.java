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
package org.moeaframework.analysis.store.fs;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.TypedProperties;

public class HashTest {
	
	private static final Pattern REGEX = Pattern.compile("[0-9A-F]{64}", Pattern.CASE_INSENSITIVE);
	
	@Test
	public void test() throws IOException {
		Assert.assertEquals(Hash.of("foo").toString(), Hash.of("foo").toString());
		Assert.assertNotEquals(Hash.of("foo").toString(), Hash.of("bar").toString());
		Assert.assertStringMatches(Hash.of("foo").toString(), REGEX);
		
		Assert.assertEquals(Hash.of("foo"), Hash.of("foo"));
		Assert.assertEquals(Hash.of("foo").hashCode(), Hash.of("foo").hashCode());
		
		Assert.assertNotEquals(Hash.of("foo"), Hash.of("bar"));
		Assert.assertNotEquals(Hash.of("foo").hashCode(), Hash.of("bar").hashCode());
	}
	
	@Test
	public void testSerializable() throws IOException {
		Assert.assertEquals(Hash.of(1).toString(), Hash.of(1).toString());
		Assert.assertNotEquals(Hash.of(1).toString(), Hash.of(2).toString());
		Assert.assertStringMatches(Hash.of(1).toString(), REGEX);
	}
	
	@Test
	public void testReference() throws IOException {
		TypedProperties properties1 = new TypedProperties();
		properties1.setInt("int", 5);
		properties1.setDouble("double", 2.0);
		properties1.setString("str", "hello");
		
		TypedProperties properties2 = properties1.copy();
		
		TypedProperties properties3 = new TypedProperties();
		properties3.setInt("int", 5);
		properties3.setDouble("double", 5.0);
		properties3.setString("str", "hello");
		
		Assert.assertEquals(Hash.of(Reference.of(properties1)).toString(), Hash.of(Reference.of(properties2)).toString());
		Assert.assertNotEquals(Hash.of(Reference.of(properties1)).toString(), Hash.of(Reference.of(properties3)).toString());
		Assert.assertStringMatches(Hash.of(Reference.of(properties1)).toString(), REGEX);
	}

}
