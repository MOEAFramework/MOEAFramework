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
import java.io.StringWriter;

import org.junit.Test;
import org.moeaframework.Assert;

public class CommentedLineReaderTest {
	
	private final String normalInput = "#comment line\nnon-comment line\n# comment line";
	
	private final String commentedInput = "#comment line\n# comment line";

	@Test
	public void testReadLineNormalInput() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(normalInput))) {
			Assert.assertEquals("non-comment line", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testTransferToNormalInput() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(normalInput));
				StringWriter writer = new StringWriter()) {
			
			reader.transferTo(writer);
			
			Assert.assertEquals("non-comment line\n", writer.toString());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testReadLineEmptyFile() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(""))) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testTransferToEmptyFile() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(""));
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			Assert.assertEquals("", writer.toString());
		}
	}
	
	@Test
	public void testReadLineEmptyFileWithComment() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(commentedInput))) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testTransferToEmptyFileWithComment() throws IOException {
		try (CommentedLineReader reader = new CommentedLineReader(new StringReader(commentedInput));
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			Assert.assertEquals("", writer.toString());
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
