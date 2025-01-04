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
package org.moeaframework.mock;

import java.io.IOException;

import org.moeaframework.Assert;
import org.moeaframework.analysis.store.TransactionalOutputStream;

public class MockTransactionalOutputStream extends TransactionalOutputStream {
	
	private MockOutputStream innerStream;

	private boolean isCommitted;
	
	private boolean isRolledBack;
	
	public MockTransactionalOutputStream() {
		this(new MockOutputStream());
	}
	
	MockTransactionalOutputStream(MockOutputStream stream) {
		super(stream);
		this.innerStream = stream;
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
	
	public boolean isClosed() {
		return innerStream.isClosed();
	}
	
	public boolean isCommitted() {
		return isCommitted;
	}
	
	public boolean isRolledBack() {
		return isRolledBack;
	}
	
	public void assertClosed() {
		Assert.assertTrue("Transactional output stream was not closed", isClosed());
	}
	
	public void assertCommitted() {
		Assert.assertTrue("Transactional output stream was not committed", isCommitted());
	}
	
	public void assertRolledBack() {
		Assert.assertTrue("Transactional output stream was not rolled back", isRolledBack());
	}
	
	public byte[] getContent() {
		return innerStream.toByteArray();
	}
	
	@Override
	public String toString() {
		return innerStream.toString();
	}
	
}
