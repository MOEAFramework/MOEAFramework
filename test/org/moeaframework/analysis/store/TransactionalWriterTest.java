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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.mock.MockTransactionalWriter;

public class TransactionalWriterTest {

	@Test
	public void testCommit() throws IOException {
		MockTransactionalWriter writer = null;
			
		try {
			writer = new MockTransactionalWriter();
			writer.write("foo");
			writer.commit();
		} finally {
			writer.close();
		}
		
		Assert.assertEquals("foo", writer.toString());
		writer.assertClosed();
		writer.assertCommitted();
	}
	
	@Test
	public void testMultipleCalls() throws IOException {
		MockTransactionalWriter writer = null;
		
		try {
			writer = new MockTransactionalWriter();
			writer.write("foo");
			writer.commit();
			writer.commit();
		} finally {
			writer.close();
			writer.close();
		}
		
		Assert.assertEquals("foo", writer.toString());
		writer.assertClosed();
		writer.assertCommitted();
	}
	
	@Test
	public void testRollback() throws IOException {
		MockTransactionalWriter writer = null;
		
		try {
			writer = new MockTransactionalWriter();
			writer.write("foo");
		} finally {
			writer.close();
		}
		
		writer.assertClosed();
		writer.assertRolledBack();
	}

}
