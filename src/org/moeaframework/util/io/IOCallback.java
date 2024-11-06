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