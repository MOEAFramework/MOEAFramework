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

import java.net.URI;

import org.junit.Test;
import org.moeaframework.Assert;

public class FileSystemDataStoreProviderTest {
	
	@Test
	public void test() {
		test("file://foo", "foo");
		test("file:///foo", "/foo");
		test("file://./foo", "./foo");
		test("file://../foo", "../foo");
		test("file://foo/bar", "foo/bar");
		test("file:///foo/bar", "/foo/bar");
	}
	
	public void test(String uri, String expectedPath) {
		FileSystemDataStoreProvider provider = new FileSystemDataStoreProvider();
		Assert.assertEquals(expectedPath, provider.toPath(URI.create(uri)));
	}

}
