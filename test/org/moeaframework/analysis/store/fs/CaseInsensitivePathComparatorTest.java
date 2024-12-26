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

import java.nio.file.Path;
import java.util.Comparator;

import org.junit.Test;
import org.moeaframework.Assert;

public class CaseInsensitivePathComparatorTest {
	
	@Test
	public void test() {
		Comparator<Path> comparator = new CaseInsensitivePathComparator();
		
		Assert.assertTrue(comparator.compare(Path.of(""), Path.of("")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("/"), Path.of("/")) == 0);
		
		Assert.assertTrue(comparator.compare(Path.of("foo"), Path.of("foo")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("foo"), Path.of("FOO")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("FOO"), Path.of("foo")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("foo"), Path.of("bar")) > 1);
		Assert.assertTrue(comparator.compare(Path.of("bar"), Path.of("foo")) < 1);
		
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo/bar")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/FOO/bar")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo/BAR")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo/bar/")) == 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("foo/bar/")) == 0);
		
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo")) > 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo/baz")) < 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("/foo/bar/baz")) < 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar"), Path.of("")) > 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo"), Path.of("/foo/bar")) < 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/baz"), Path.of("/foo/bar")) > 0);
		Assert.assertTrue(comparator.compare(Path.of("/foo/bar/baz"), Path.of("/foo/bar")) > 0);
		Assert.assertTrue(comparator.compare(Path.of(""), Path.of("/foo/bar")) < 0);
	}

}
