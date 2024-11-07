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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writer with a transaction mechanism requiring one to call {@link #commit()} before the content is stored.  The
 * typical use is:
 * <pre>{@code
 *   try (TransactionalWriter writer = blob.openWriter()) {
 *       writer.write(...);
 *       writer.commit();
 *   }
 * }</pre>
 */
public abstract class TransactionalWriter extends FilterWriter {
	
	private boolean isCommitted = false;
	
	private boolean isClosed = false;
	
	/**
	 * Constructs a transactional writer.
	 * 
	 * @param out the temporary writer, where content is written before it is committed
	 */
	public TransactionalWriter(Writer out) {
		super(out);
	}
	
	/**
	 * Indicates the content was successfully written.  This method must be called before {@link #close()} to
	 * take effect.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
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
	
	/**
	 * Called during {@link #close()} to commit or complete the transaction.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	protected abstract void doCommit() throws IOException;
	
	/**
	 * Called during {@link #close()} to rollback the transaction.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	protected abstract void doRollback() throws IOException;

}
