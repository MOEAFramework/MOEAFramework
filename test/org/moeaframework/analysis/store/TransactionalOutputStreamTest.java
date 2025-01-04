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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.moeaframework.Assert;

public class TransactionalOutputStreamTest {

	@Test
	public void testCommit() throws IOException {
		try (ByteArrayOutputStream innerStream = new ByteArrayOutputStream()) {
			TestTransactionalOutputStream stream = new TestTransactionalOutputStream(innerStream);
			
			try {
				stream.write("foo".getBytes());
				stream.commit();
			} finally {
				stream.close();
			}
			
			Assert.assertEquals("foo", innerStream.toString());
			Assert.assertTrue(stream.isCommitted);
			Assert.assertFalse(stream.isRolledBack);
		}
	}
	
	@Test
	public void testMultipleCalls() throws IOException {
		try (ByteArrayOutputStream innerStream = new ByteArrayOutputStream()) {
			TestTransactionalOutputStream stream = new TestTransactionalOutputStream(innerStream);
			
			try {
				stream.write("foo".getBytes());
				stream.commit();
				stream.commit();
			} finally {
				stream.close();
				stream.close();
			}
			
			Assert.assertEquals("foo", innerStream.toString());
			Assert.assertTrue(stream.isCommitted);
			Assert.assertFalse(stream.isRolledBack);
		}
	}
	
	@Test
	public void testRollback() throws IOException {
		try (ByteArrayOutputStream innerStream = new ByteArrayOutputStream()) {
			TestTransactionalOutputStream stream = new TestTransactionalOutputStream(innerStream);
			
			try {
				stream.write("foo".getBytes());
			} finally {
				stream.close();
			}
			
			Assert.assertEquals("foo", innerStream.toString());
			Assert.assertFalse(stream.isCommitted);
			Assert.assertTrue(stream.isRolledBack);
		}
	}
	
	public static class TestTransactionalOutputStream extends TransactionalOutputStream {
		
		boolean isCommitted;
		
		boolean isRolledBack;
		
		public TestTransactionalOutputStream(OutputStream out) {
			super(out);
		}
		
		@Override
		protected void doCommit() throws IOException {
			Assert.assertFalse("Transactional output stream was previously committed", isCommitted);
			Assert.assertFalse("Transactional output stream was previously rolled back", isRolledBack);
			
			isCommitted = true;
		}

		@Override
		protected void doRollback() throws IOException {
			Assert.assertFalse("Transactional output stream was previously committed", isCommitted);
			Assert.assertFalse("Transactional output stream was previously rolled back", isRolledBack);
			
			isRolledBack = true;
		}
		
	}

}
