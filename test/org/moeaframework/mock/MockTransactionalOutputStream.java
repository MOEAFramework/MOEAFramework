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
