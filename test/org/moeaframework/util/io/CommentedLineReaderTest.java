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
package org.moeaframework.util.io;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.moeaframework.Assert;

public class CommentedLineReaderTest {

	@Test
	public void testNormalInput() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(
				new StringReader("#comment line\nnon-comment line\n# comment line"))) {
			Assert.assertEquals("non-comment line", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testEmptyFile() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(""))) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testEmptyFileWithComment() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader("#comment line\n# comment line"))) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testWrap() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(""));
				CommentedLineReader wrappedReader = CommentedLineReader.wrap(reader)) {
			Assert.assertSame(reader, wrappedReader);
		}
	}

}
