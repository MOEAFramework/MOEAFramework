/* Copyright 2009-2024 David Hadka
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
