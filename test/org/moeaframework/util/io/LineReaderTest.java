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
import java.util.NoSuchElementException;

import org.junit.Test;
import org.moeaframework.Assert;

public class LineReaderTest {
	
	private final String normalInput = "#comment line\nnon-comment line\n# comment line";
	
	private final String commentedInput = "#comment line\n# comment line";
	
	private final String blankInput = "   \n   ";
	
	private final String emptyInput = "";
	
	@Test
	public void testReadLineKeepingComments() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(normalInput))) {
			Assert.assertEquals("#comment line", reader.readLine());
			Assert.assertEquals("non-comment line", reader.readLine());
			Assert.assertEquals("# comment line", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}

	@Test
	public void testReadLineNormalInput() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(normalInput)).skipComments()) {
			Assert.assertEquals("non-comment line", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testIteratorNormalInput() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(normalInput)).skipComments()) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertEquals("non-comment line", reader.next());
			
			Assert.assertFalse(reader.hasNext());
			Assert.assertThrows(NoSuchElementException.class, () -> reader.next());
		}
	}
	
	@Test
	public void testTransferToNormalInput() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(normalInput)).skipComments();
				StringWriter writer = new StringWriter()) {
			
			reader.transferTo(writer);
			
			Assert.assertEquals("non-comment line" + System.lineSeparator(), writer.toString());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testReadLineEmptyFile() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(emptyInput))) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testIteratorEmptyFile() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(emptyInput))) {
			Assert.assertFalse(reader.hasNext());
			Assert.assertThrows(NoSuchElementException.class, () -> reader.next());
		}
	}
	
	@Test
	public void testTransferToEmptyFile() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(emptyInput));
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			Assert.assertEquals("", writer.toString());
		}
	}
	
	@Test
	public void testReadLineBlankLine() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(blankInput)).skipBlanks()) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testIteratorBlankLine() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(blankInput)).skipBlanks()) {
			Assert.assertFalse(reader.hasNext());
			Assert.assertThrows(NoSuchElementException.class, () -> reader.next());
		}
	}
	
	@Test
	public void testTransferToBlankLine() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(blankInput)).skipBlanks();
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			Assert.assertEquals("", writer.toString());
		}
	}
	
	@Test
	public void testTrim() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(blankInput)).trim()) {
			Assert.assertEquals("", reader.readLine());
			Assert.assertEquals("", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testReadLineEmptyFileWithComment() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(commentedInput)).skipComments()) {
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testIteratorEmptyFileWithComment() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(commentedInput)).skipComments()) {
			Assert.assertFalse(reader.hasNext());
			Assert.assertThrows(NoSuchElementException.class, () -> reader.next());
		}
	}
	
	@Test
	public void testTransferToEmptyFileWithComment() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(commentedInput)).skipComments();
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			Assert.assertEquals("", writer.toString());
		}
	}
	
	@Test
	public void testWrap() throws IOException {
		try (LineReader reader = new LineReader(new StringReader(""));
				LineReader wrappedReader = LineReader.wrap(reader)) {
			Assert.assertSame(reader, wrappedReader);
		}
	}

}
