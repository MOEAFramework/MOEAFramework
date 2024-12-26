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
package org.moeaframework.util.io;

import java.io.IOException;

/**
 * Callback function used for an I/O operation.
 * 
 * @param <T> the type of the stream, reader, writer, or input object
 */
@FunctionalInterface
public interface IOCallback<T> {
	
	/**
	 * Invokes this callback with the given stream, reader, writer, or object.
	 * <p>
	 * When dealing with a stream, reader, or writer, any resources are automatically closed when the callback
	 * returns.  Thus, the callback itself does not need close the stream.  However, the callback should throw an
	 * exception if the operation failed and needs to be aborted.
	 * 
	 * @param stream the stream, reader, or writer
	 * @throws IOException if an I/O error occurred
	 */
	public void accept(T stream) throws IOException;
	
}