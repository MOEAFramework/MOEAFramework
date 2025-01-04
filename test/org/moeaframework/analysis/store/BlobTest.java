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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TempFiles.File;
import org.moeaframework.mock.MockInputStream;
import org.moeaframework.mock.MockOutputStream;
import org.moeaframework.mock.MockReader;
import org.moeaframework.mock.MockTransactionalOutputStream;
import org.moeaframework.mock.MockTransactionalWriter;
import org.moeaframework.mock.MockWriter;

public class BlobTest {
	
	private TestBlob blob;
	
	@Before
	public void setUp() {
		blob = new TestBlob();
	}
	
	@After
	public void tearDown() {
		blob = null;
	}
	
	@Test
	public void testExtractFile() throws IOException {
		File file = TempFiles.createFile();
		
		blob.setContent("foo");
		blob.extract(file);
		Assert.assertFileWithContent("foo", file);
	}
	
	@Test
	public void testExtractPath() throws IOException {
		File file = TempFiles.createFile();
		
		blob.setContent("foo");
		blob.extract(file.toPath());
		Assert.assertFileWithContent("foo", file);
	}
	
	@Test
	public void testExtractWriter() throws IOException {
		try (MockWriter writer = new MockWriter()) {
			blob.setContent("foo");
			blob.extract(writer);
			Assert.assertEquals("foo", writer.toString());
		}
	}
	
	@Test
	public void testExtractOutputStream() throws IOException {
		try (MockOutputStream stream = new MockOutputStream()) {
			blob.setContent("foo");
			blob.extract(stream);
			Assert.assertEquals("foo", stream.toString());
		}
	}
	
	@Test
	public void testExtractInputStreamCallback() throws IOException {
		try (MockOutputStream result = new MockOutputStream()) {
			blob.setContent("foo");
			
			blob.extract((InputStream stream) -> {
				stream.transferTo(result);
				stream.close(); // has no effect
			});
			
			Assert.assertEquals("foo", result.toString());
		}
	}
	
	@Test
	public void testExtractReaderCallback() throws IOException {
		try (MockWriter result = new MockWriter()) {
			blob.setContent("foo");
			
			blob.extract((Reader reader) -> {
				reader.transferTo(result);
				reader.close(); // has no effect
			});
			
			Assert.assertEquals("foo", result.toString());
		}
	}
	
	@Test
	public void testExtractIfFound() throws IOException {
		blob.extractIfFound((Reader reader) -> {
			Assert.fail("Blob does not exist");
		});
		
		blob.setContent("foo");
		
		try (MockWriter result = new MockWriter()) {
			blob.extractIfFound((Reader reader) -> {
				reader.transferTo(result);
			});
			
			Assert.assertEquals("foo", result.toString());
		}
	}

	@Test
	public void testStoreFile() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		
		blob.store(file);
		blob.assertContent("foo");
	}
	
	@Test
	public void testStorePath() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		
		blob.store(file.toPath());
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreReader() throws IOException {		
		try (MockReader reader = new MockReader("foo")) {
			blob.store(reader);
			blob.assertContent("foo");
			
			Assert.assertFalse(reader.isClosed());
		}
	}
	
	@Test
	public void testStoreInputStream() throws IOException {		
		try (MockInputStream reader = new MockInputStream("foo")) {
			blob.store(reader);
			blob.assertContent("foo");
			
			Assert.assertFalse(reader.isClosed());
		}
	}
	
	@Test
	public void testStoreOutputStreamCallback() throws IOException {
		blob.store((OutputStream stream) -> {
			stream.write("foo".getBytes());
			stream.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStorePrintStreamCallback() throws IOException {
		blob.store((PrintStream stream) -> {
			stream.print("foo");
			stream.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreWriterCallback() throws IOException {
		blob.store((Writer writer) -> {
			writer.write("foo");
			writer.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreIfMissing() throws IOException {
		blob.storeIfMissing((Writer writer) -> {
			writer.write("foo");
			writer.close(); // has no effect
		});
		
		blob.assertContent("foo");
		
		blob.storeIfMissing((Writer writer) -> {
			writer.write("bar");
			writer.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreExtractObject() throws IOException, ClassNotFoundException {
		blob.storeObject(5);
		
		Object obj = blob.extractObject();
		Assert.assertInstanceOf(Integer.class, obj);
		Assert.assertEquals(5, obj);
		
		Integer val = blob.extractObject(Integer.class);
		Assert.assertEquals(5, val);
		
		Assert.assertThrows(ClassCastException.class, () -> blob.extractObject(Double.class));
	}
	
	public static class TestBlob implements Blob {
		
		private byte[] content;
		
		public TestBlob() {
			this((byte[])null);
		}
		
		public TestBlob(String content) {
			super();
			setContent(content);
		}
		
		public TestBlob(byte[] content) {
			super();
			setContent(content);
		}
		
		public byte[] getContent() {
			return content;
		}
		
		public void setContent(byte[] content) {
			this.content = content;
		}
		
		public void setContent(String content) {
			setContent(content.getBytes());
		}
		
		@Override
		public String toString() {
			return new String(content);
		}
		
		public void assertContent(String expected) {
			Assert.assertEquals(expected, toString());
		}

		@Override
		public String getName() {
			return "Test";
		}

		@Override
		public Container getContainer() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean exists() throws IOException {
			return content != null;
		}

		@Override
		public boolean delete() throws IOException {
			if (content != null) {
				content = null;
				return true;
			}
			
			return false;
		}

		@Override
		public Instant lastModified() throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Reader openReader() throws IOException {
			if (content != null) {
				return new StringReader(toString());
			}
			
			throw new IOException("Test blob does not exist");
		}

		@Override
		public InputStream openInputStream() throws IOException {
			if (content != null) {
				return new ByteArrayInputStream(content);
			}
			
			throw new IOException("Test blob does not exist");
		}

		@Override
		public TransactionalWriter openWriter() throws IOException {
			return new MockTransactionalWriter() {

				@Override
				protected void doCommit() throws IOException {
					content = toString().getBytes();
					super.doCommit();
				}
				
			};
		}

		@Override
		public TransactionalOutputStream openOutputStream() throws IOException {
			return new MockTransactionalOutputStream() {

				@Override
				protected void doCommit() throws IOException {
					content = getContent();
					super.doCommit();
				}
				
			};
		}
		
	}

}
