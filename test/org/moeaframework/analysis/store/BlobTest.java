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
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.time.Instant;
import java.util.List;

import org.apache.commons.io.function.IOConsumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;
import org.moeaframework.TempFiles;
import org.moeaframework.TempFiles.File;
import org.moeaframework.analysis.io.EmptyResultFileException;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockInputStream;
import org.moeaframework.mock.MockOutputStream;
import org.moeaframework.mock.MockReader;
import org.moeaframework.mock.MockTransactionalOutputStream;
import org.moeaframework.mock.MockTransactionalWriter;
import org.moeaframework.mock.MockWriter;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

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
	public void testExtractToFile() throws IOException {
		File file = TempFiles.createFile();
		
		blob.setContent("foo");
		blob.extractTo(file);
		Assert.assertFileWithContent("foo", file);
	}
	
	@Test
	public void testExtractToPath() throws IOException {
		File file = TempFiles.createFile();
		
		blob.setContent("foo");
		blob.extractTo(file.toPath());
		Assert.assertFileWithContent("foo", file);
	}
	
	@Test
	public void testExtractText() {
		blob.setContent("foo");
		Assert.assertEquals("foo", blob.extractText());
	}
	
	@Test
	public void testExtractBytes() {
		blob.setContent("foo".getBytes());
		Assert.assertArrayEquals("foo".getBytes(), blob.extractBytes());
	}
	
	@Test
	public void testExtractToWriter() throws IOException {
		try (MockWriter writer = new MockWriter()) {
			blob.setContent("foo");
			blob.extractTo(writer);
			Assert.assertEquals("foo", writer.toString());
		}
	}
	
	@Test
	public void testExtractToOutputStream() throws IOException {
		try (MockOutputStream stream = new MockOutputStream()) {
			blob.setContent("foo");
			blob.extractTo(stream);
			Assert.assertEquals("foo", stream.toString());
		}
	}
	
	@Test
	public void testExtractInputStream() throws IOException {
		blob.setContent("foo");
			
		String text = blob.extractInputStream((InputStream stream) -> {
			try (MockOutputStream result = new MockOutputStream()) {
				stream.transferTo(result);
				stream.close(); // has no effect
				return result.toString();
			}
		});
			
		Assert.assertEquals("foo", text);
	}
	
	@Test
	public void testExtractReader() throws IOException {
		blob.setContent("foo");
			
		String text = blob.extractReader((Reader reader) -> {
			try (MockWriter result = new MockWriter()) {
				reader.transferTo(result);
				reader.close(); // has no effect
				return result.toString();
			}
		});
			
		Assert.assertEquals("foo", text);
	}

	@Test
	public void testStoreFromFile() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		
		blob.storeFrom(file);
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreFromPath() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		
		blob.storeFrom(file.toPath());
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreText() {
		blob.storeText("foo");
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreBytes() {
		blob.storeBytes("foo".getBytes());
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreFormattable() {
		Formattable<String> formattable = new Formattable<>() {

			@Override
			public TabularData<String> asTabularData() {
				List<String> data = List.of("foo");
				
				TabularData<String> table = new TabularData<>(data);
				table.addColumn(new Column<String, String>("Value", x -> x));
				
				return table;
			}
			
		};
		
		blob.storeText(formattable);
		blob.assertContentNormalized("Value\n-----\nfoo\n");
	}
	
	@Test
	public void testStoreFromReader() throws IOException {		
		try (MockReader reader = new MockReader("foo")) {
			blob.storeFrom(reader);
			blob.assertContent("foo");
			
			Assert.assertFalse(reader.isClosed());
		}
	}
	
	@Test
	public void testStoreFromInputStream() throws IOException {		
		try (MockInputStream reader = new MockInputStream("foo")) {
			blob.storeFrom(reader);
			blob.assertContent("foo");
			
			Assert.assertFalse(reader.isClosed());
		}
	}
	
	@Test
	public void testStoreOutputStream() {
		blob.storeOutputStream((OutputStream stream) -> {
			stream.write("foo".getBytes());
			stream.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStorePrintStream() {
		blob.storePrintStream((PrintStream stream) -> {
			stream.print("foo");
			stream.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreWriter() {
		blob.storeWriter((Writer writer) -> {
			writer.write("foo");
			writer.close(); // has no effect
		});
		
		blob.assertContent("foo");
	}
	
	@Test
	public void testStoreExtractPopulation() throws IOException {
		Population expected = NondominatedPopulation.load("./pf/DTLZ2.2D.pf");
		blob.storePopulation(expected);
		
		Population actual = blob.extractPopulation();
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testExtractPopulationOnInvalidFile() throws IOException {
		blob.storeText("foo");
		Assert.assertThrows(EmptyResultFileException.class, () -> blob.extractPopulation());
	}
	
	@Test
	public void testStoreExtractObject() throws NotSerializableException, ClassNotFoundException {
		blob.storeObject(5);
		
		Object obj = blob.extractObject();
		Assert.assertInstanceOf(Integer.class, obj);
		Assert.assertEquals(5, obj);
		
		Integer val = blob.extractObject(Integer.class);
		Assert.assertEquals(5, val);
		
		Assert.assertThrows(ClassCastException.class, () -> blob.extractObject(Double.class));
	}
	
	@Test
	public void testStoreExtractState() throws IOException, ClassNotFoundException {
		Population expected = NondominatedPopulation.load("./pf/DTLZ2.2D.pf");
		blob.storeState(expected);
		
		Population actual = new Population();
		blob.extractState(actual);
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testIfMissing() {
		CallCounter<IOConsumer<Blob>> counter = CallCounter.mockIOConsumer();
		
		blob.ifMissing(counter.getProxy());
		Assert.assertEquals(1, counter.getTotalCallCount());
		
		blob.storeText("foo");
		blob.ifMissing(counter.getProxy());
		Assert.assertEquals(1, counter.getTotalCallCount());
	}
	
	@Test
	public void testIfFound() {
		CallCounter<IOConsumer<Blob>> counter = CallCounter.mockIOConsumer();
		
		blob.ifFound(counter.getProxy());
		Assert.assertEquals(0, counter.getTotalCallCount());
		
		blob.storeText("foo");
		blob.ifFound(counter.getProxy());
		Assert.assertEquals(1, counter.getTotalCallCount());
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
		
		public void assertContentNormalized(String expected) {
			Assert.assertEqualsNormalized(expected, toString());
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
		public boolean exists() throws DataStoreException {
			return content != null;
		}

		@Override
		public boolean delete() throws DataStoreException {
			if (content != null) {
				content = null;
				return true;
			}
			
			return false;
		}

		@Override
		public Instant lastModified() throws DataStoreException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Reader openReader() throws DataStoreException {
			if (content != null) {
				return new StringReader(toString());
			}
			
			throw new BlobNotFoundException(this);
		}

		@Override
		public InputStream openInputStream() throws DataStoreException {
			if (content != null) {
				return new ByteArrayInputStream(content);
			}
			
			throw new BlobNotFoundException(this);
		}

		@Override
		public TransactionalWriter openWriter() throws DataStoreException {
			return new MockTransactionalWriter() {

				@Override
				protected void doCommit() throws IOException {
					content = toString().getBytes();
					super.doCommit();
				}
				
			};
		}

		@Override
		public TransactionalOutputStream openOutputStream() throws DataStoreException {
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
