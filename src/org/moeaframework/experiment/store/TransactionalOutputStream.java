package org.moeaframework.experiment.store;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream with a transaction mechanism requiring one to call {@link #commit()} before the content is stored.
 * The typical use is:
 * <pre>{@code
 *   try (TransactionalOutputStream out = dataStore.writer(key).asBinary()) {
 *       out.write(...);
 *       out.commit();
 *   }
 * }</pre>
 */
public abstract class TransactionalOutputStream extends FilterOutputStream {
	
	private boolean isCommitted = false;
	
	private boolean isClosed = false;
	
	public TransactionalOutputStream(OutputStream out) {
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
		isClosed = true;
		
		if (isCommitted) {
			doCommit();
		} else {
			System.err.println("Stream closed without being committed, data being discarded");
			doRollback();
		}
	}

	protected abstract void doCommit() throws IOException;
	
	protected abstract void doRollback() throws IOException;

}
