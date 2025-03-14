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

import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Assert;

public class DataStoreURITest {
	
	@Test
	public void testDefaultSchemeWithAbsolutePath() {
		DataStoreURI uri = DataStoreURI.parse("/foo");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("/foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testDefaultSchemeWithRelativePath() {
		DataStoreURI uri = DataStoreURI.parse("../foo");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testDefaultSchemeWithQuery() {
		DataStoreURI uri = DataStoreURI.parse("../foo?a=b&_name=c");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEquals(1, uri.getQuery().size());
		Assert.assertEquals("b", uri.getQuery().getString("a"));
		Assert.assertEquals("c", uri.getName());
	}
	
	@Test
	public void testDefaultSchemeWithFragment() {
		DataStoreURI uri = DataStoreURI.parse("../foo?a=b#c");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEquals(1, uri.getQuery().size());
		Assert.assertEquals("b", uri.getQuery().getString("a"));
		Assert.assertEquals("c", uri.getName());
	}
	
	@Test
	public void testFileSchemeWithAbsolutePath() {
		DataStoreURI uri = DataStoreURI.parse("file:/foo");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("/foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testFileSchemeWithRelativePath() {
		DataStoreURI uri = DataStoreURI.parse("file:../foo");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testFileSchemeWithQuery() {
		DataStoreURI uri = DataStoreURI.parse("file:../foo?a=b&_name=c");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEquals(1, uri.getQuery().size());
		Assert.assertEquals("b", uri.getQuery().getString("a"));
		Assert.assertEquals("c", uri.getName());
	}
	
	@Test
	public void testFileSchemeWithFragment() {
		DataStoreURI uri = DataStoreURI.parse("file:../foo?a=b#c");
		Assert.assertEquals("file", uri.getScheme());
		Assert.assertNull(uri.getServer());
		Assert.assertEquals(Path.of("../foo"), uri.getPath());
		Assert.assertEquals(1, uri.getQuery().size());
		Assert.assertEquals("b", uri.getQuery().getString("a"));
		Assert.assertEquals("c", uri.getName());
	}
	
	@Test
	public void testHttpsScheme() {
		DataStoreURI uri = DataStoreURI.parse("https://localhost/foo");
		Assert.assertEquals("https", uri.getScheme());
		Assert.assertEquals("localhost", uri.getServer());
		Assert.assertEquals(Path.of("/foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testHttpsSchemeWithUserInfo() {
		DataStoreURI uri = DataStoreURI.parse("https://user:pass@localhost:8080/foo");
		Assert.assertEquals("https", uri.getScheme());
		Assert.assertEquals("user:pass@localhost:8080", uri.getServer());
		Assert.assertEquals(Path.of("/foo"), uri.getPath());
		Assert.assertEmpty(uri.getQuery());
		Assert.assertNull(uri.getName());
	}
	
	@Test
	public void testHttpsSchemeWithQuery() {
		DataStoreURI uri = DataStoreURI.parse("https://localhost/foo?a=b&_name=c");
		Assert.assertEquals("https", uri.getScheme());
		Assert.assertEquals("localhost", uri.getServer());
		Assert.assertEquals(Path.of("/foo"), uri.getPath());
		Assert.assertEquals(1, uri.getQuery().size());
		Assert.assertEquals("b", uri.getQuery().getString("a"));
		Assert.assertEquals("c", uri.getName());
	}
	
	@Test
	public void testEquals() {
		Assert.assertNotEquals(DataStoreURI.parse("/foo"), null);
		
		Assert.assertEquals(DataStoreURI.parse("/foo"), DataStoreURI.parse("/foo"));
		Assert.assertEquals(DataStoreURI.parse("/foo"), DataStoreURI.parse("file:/foo"));
		Assert.assertEquals(DataStoreURI.parse("/foo"), DataStoreURI.parse("file:///foo"));

		Assert.assertEquals(DataStoreURI.parse("foo"), DataStoreURI.parse("foo"));
		Assert.assertEquals(DataStoreURI.parse("foo"), DataStoreURI.parse("file:foo"));
		Assert.assertEquals(DataStoreURI.parse("foo"), DataStoreURI.parse("file://foo"));
		
		Assert.assertNotEquals(DataStoreURI.parse("foo"), DataStoreURI.parse("/foo"));
		Assert.assertNotEquals(DataStoreURI.parse("foo"), DataStoreURI.parse("foo/bar"));

		Assert.assertEquals(DataStoreURI.parse("file:foo?a=b#c"), DataStoreURI.parse("file:foo?A=b#c"));
		Assert.assertEquals(DataStoreURI.parse("file:foo?a=b#c"), DataStoreURI.parse("file:foo?A=b&_name=c"));

	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(DataStoreURI.parse("/foo").hashCode(), DataStoreURI.parse("/foo").hashCode());
		Assert.assertEquals(DataStoreURI.parse("/foo").hashCode(), DataStoreURI.parse("file:/foo").hashCode());
		Assert.assertEquals(DataStoreURI.parse("/foo").hashCode(), DataStoreURI.parse("file:///foo").hashCode());

		Assert.assertEquals(DataStoreURI.parse("foo").hashCode(), DataStoreURI.parse("foo").hashCode());
		Assert.assertEquals(DataStoreURI.parse("foo").hashCode(), DataStoreURI.parse("file:foo").hashCode());
		Assert.assertEquals(DataStoreURI.parse("foo").hashCode(), DataStoreURI.parse("file://foo").hashCode());
		
		Assert.assertNotEquals(DataStoreURI.parse("foo").hashCode(), DataStoreURI.parse("/foo").hashCode());
		Assert.assertNotEquals(DataStoreURI.parse("foo").hashCode(), DataStoreURI.parse("foo/bar").hashCode());
	}
	
	@Test
	public void testWindows() {
		Assert.assertEquals(Path.of("D:/path/to/foo/"), DataStoreURI.parse("file:///D:/path/to/foo/").getPath());
	}

}
