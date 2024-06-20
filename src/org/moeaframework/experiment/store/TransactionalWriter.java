package org.moeaframework.experiment.store;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writer with a transaction mechanism requiring one to call {@link #commit()} before the content is stored.  The
 * typical use is:
 * <pre>{@code
 *   try (TransactionalWriter writer = dataStore.writer(key).asText()) {
 *       writer.write(...);
 *       writer.commit();
 *   }
 * }</pre>
 */
public abstract class TransactionalWriter extends FilterWriter {
	
	private boolean isCommitted = false;
	
	private boolean isClosed = false;
	
	public TransactionalWriter(Writer out) {
		super(out);
	}
	
	public void commit() throws IOException {
		isCommitted = true;
	}
	
	@Override
	public void close() throws IOException {
		if (isClosed) {
			return;
		}
		
		super.close();
		
		if (isCommitted) {
			doCommit();
		} else {
			System.err.println("Stream closed without being committed, data being discarded");
			doRollback();
		}
		
		isClosed = true;
	}
	
	protected abstract void doCommit() throws IOException;
	
	protected abstract void doRollback() throws IOException;

}
