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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.moeaframework.Assert;

public class TransactionalWriterTest {

	@Test
	public void testCommit() throws IOException {
		try (StringWriter innerWriter = new StringWriter()) {
			TestTransactionalWriter writer = new TestTransactionalWriter(innerWriter);
			
			try {
				writer.write("foo");
				writer.commit();
			} finally {
				writer.close();
			}
			
			Assert.assertEquals("foo", innerWriter.toString());
			Assert.assertTrue(writer.isCommitted);
			Assert.assertFalse(writer.isRolledBack);
		}
	}
	
	@Test
	public void testMultipleCalls() throws IOException {
		try (StringWriter innerWriter = new StringWriter()) {
			TestTransactionalWriter writer = new TestTransactionalWriter(innerWriter);
			
			try {
				writer.write("foo");
				writer.commit();
				writer.commit();
			} finally {
				writer.close();
				writer.close();
			}
			
			Assert.assertEquals("foo", innerWriter.toString());
			Assert.assertTrue(writer.isCommitted);
			Assert.assertFalse(writer.isRolledBack);
		}
	}
	
	@Test
	public void testRollback() throws IOException {
		try (StringWriter innerWriter = new StringWriter()) {
			TestTransactionalWriter writer = new TestTransactionalWriter(innerWriter);
			
			try {
				writer.write("foo");
			} finally {
				writer.close();
			}
			
			Assert.assertEquals("foo", innerWriter.toString());
			Assert.assertFalse(writer.isCommitted);
			Assert.assertTrue(writer.isRolledBack);
		}
	}
	
	public static class TestTransactionalWriter extends TransactionalWriter {
		
		boolean isCommitted;
		
		boolean isRolledBack;
		
		public TestTransactionalWriter(Writer out) {
			super(out);
		}
		
		@Override
		protected void doCommit() throws IOException {
			Assert.assertFalse("Transactional writer was previously committed", isCommitted);
			Assert.assertFalse("Transactional writer was previously rolled back", isRolledBack);
			
			isCommitted = true;
		}

		@Override
		protected void doRollback() throws IOException {
			Assert.assertFalse("Transactional writer was previously committed", isCommitted);
			Assert.assertFalse("Transactional writer was previously rolled back", isRolledBack);
			
			isRolledBack = true;
		}
		
	}

}
