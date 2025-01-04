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
import org.moeaframework.mock.MockTransactionalOutputStream;

public class TransactionalOutputStreamTest {

	@Test
	public void testCommit() throws IOException {
		MockTransactionalOutputStream stream = null;
		
		try {
			stream = new MockTransactionalOutputStream();
			stream.write("foo".getBytes());
			stream.commit();
		} finally {
			stream.close();
		}
		
		Assert.assertEquals("foo", stream.toString());
		stream.assertClosed();
		stream.assertCommitted();
	}
	
	@Test
	public void testMultipleCalls() throws IOException {
		MockTransactionalOutputStream stream = null;
		
		try {
			stream = new MockTransactionalOutputStream();
			stream.write("foo".getBytes());
			stream.commit();
			stream.commit();
		} finally {
			stream.close();
			stream.close();
		}
		
		Assert.assertEquals("foo", stream.toString());
		stream.assertClosed();
		stream.assertCommitted();
	}
	
	@Test
	public void testRollback() throws IOException {
		MockTransactionalOutputStream stream = null;
		
		try {
			stream = new MockTransactionalOutputStream();
			stream.write("foo".getBytes());
		} finally {
			stream.close();
		}
		
		stream.assertClosed();
		stream.assertRolledBack();
	}

}
