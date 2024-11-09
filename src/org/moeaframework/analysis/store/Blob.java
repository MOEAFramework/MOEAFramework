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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CloseShieldWriter;
import org.moeaframework.util.io.IOCallback;
import org.moeaframework.util.io.InputStreamCallback;
import org.moeaframework.util.io.OutputStreamCallback;
import org.moeaframework.util.io.PrintStreamCallback;
import org.moeaframework.util.io.ReaderCallback;
import org.moeaframework.util.io.WriterCallback;

/**
 * Reference to a blob, which is simply a piece of data identified by its container and name. 
 */
public interface Blob {
	
	/**
	 * Gets the name of this blob.
	 * 
	 * @return the blo name
	 */
	String getName();
	
	/**
	 * Gets the container for this blob.
	 * 
	 * @return the container
	 */
	Container getContainer();
	
	/**
	 * Returns {@code true} if the blob exists; {@code false} otherwise.
	 * 
	 * @return {@code true} if the blob exists; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	boolean exists() throws IOException;
	
	/**
	 * Deletes this blob if it exists.
	 * 
	 * @return {@code true} if the blob was deleted; {@code false} if the blob does not exist
	 * @throws IOException if an I/O error occurred
	 */
	boolean delete() throws IOException;
	
	/**
	 * Returns the last modified time of the blob.
	 * 
	 * @return the last modified time
	 * @throws IOException if an I/O error occurred
	 */
	Instant lastModified() throws IOException;
	
	/**
	 * Creates and returns a {@link Reader} for reading text from this blob.  The caller is responsible for closing the
	 * reader when finished.
	 * 
	 * @return a reader for this blob
	 * @throws IOException if an I/O error occurred
	 */
	Reader openReader() throws IOException;
	
	/**
	 * Creates and returns an {@link InputStream} for reading binary data from this blob.  The caller is responsible for
	 * closing the stream when finished.
	 * 
	 * @return an input stream for this blob
	 * @throws IOException if an I/O error occurred
	 */
	InputStream openInputStream() throws IOException;
	
	/**
	 * Creates and returns a {@link TransactionalWriter} for writing text to this blob.  The caller is responsible for
	 * committing and closing the writer when finished.  If the writer is closed before being committed, any written
	 * content is discarded.
	 * 
	 * @return the writer for this blob
	 * @throws IOException if an I/O error occurred
	 */
	TransactionalWriter openWriter() throws IOException;
	
	/**
	 * Creates and returns a {@link TransactionalOutputStream} for writing binary data to this blob.  The caller is
	 * responsible for committing and closing the stream when finished.  If the stream is closed before being committed,
	 * any written content is discarded.
	 * 
	 * @return the output stream for this blob
	 * @throws IOException if an I/O error occurred
	 */
	TransactionalOutputStream openOutputStream() throws IOException;
	
	/**
	 * Executes the callback function if this blob is missing.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob is missing and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean ifMissing(IOCallback<Blob> callback) throws IOException {
		if (!exists()) {
			callback.accept(this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Executes the callback function if this blob exists.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob exists and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean ifFound(IOCallback<Blob> callback) throws IOException {
		if (exists()) {
			callback.accept(this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Extracts the content of this blob to a file.
	 * 
	 * @param file the destination file
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(File file) throws IOException {
		extract(file.toPath());
	}
	
	/**
	 * Extracts the content of this blob to a path.
	 * 
	 * @param path the destination path
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(Path path) throws IOException {
		try (InputStream in = openInputStream()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	/**
	 * Transfers the content of this blob to an output stream.
	 * 
	 * @param out the output stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(OutputStream out) throws IOException {
		try (InputStream in = openInputStream()) {
			in.transferTo(out);
		}
	}
	
	/**
	 * Transfers the content of this blob to a writer.
	 * 
	 * @param out the writer
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(Writer out) throws IOException {
		try (Reader in = openReader()) {
			in.transferTo(out);
		}
	}
	
	/**
	 * Executes a callback with an {@link InputStream} for reading this blob.  The input stream is automatically closed
	 * when the callback returns.
	 * 
	 * @param callback the callback function
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(InputStreamCallback callback) throws IOException {
		try (InputStream in = openInputStream()) {
			callback.accept(in);
		}
	}
	
	/**
	 * Executes a callback with a {@link Reader} for reading this blob.  The reader is automatically closed when the
	 * callback returns.
	 * 
	 * @param callback the callback function
	 * @throws IOException if an I/O error occurred
	 */
	public default void extract(ReaderCallback callback) throws IOException {
		try (Reader in = openReader()) {
			callback.accept(in);
		}
	}
	
	/**
	 * Executes the callback with an {@link InputStream} for reading this blob, but only if the blob exists.  The
	 * stream is automatically closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob exists and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean extractIfFound(InputStreamCallback callback) throws IOException {
		return ifFound(b -> b.extract(callback));
	}
	
	/**
	 * Executes the callback with a {@link Reader} for reading this blob, but only if the blob exists.  The reader is
	 * automatically closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob exists and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean extractIfFound(ReaderCallback callback) throws IOException {
		return ifFound(b -> b.extract(callback));
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @return the deserialized object
	 * @throws IOException if an I/O error occurred
	 */
	public default Object extractObject() throws IOException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			return ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Deserialization failed", e);
		}
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @param <T> the return type
	 * @param type the type of the object
	 * @return the deserialized object cast to the given type
	 * @throws IOException if an I/O error occurred
	 */
	public default <T> T extractObject(Class<T> type) throws IOException {
		return type.cast(extractObject());
	}
	
	/**
	 * Stores the contents of a file to this blob.
	 * 
	 * @param file the source file
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(File file) throws IOException {
		store(file.toPath());
	}
	
	/**
	 * Stores the contents of a path to this blob.
	 * 
	 * @param path the source path
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(Path path) throws IOException {
		try (TransactionalOutputStream out = openOutputStream()) {
			Files.copy(path, out);
			out.commit();
		}
	}
	
	/**
	 * Stores the content read from an input stream to this this blob.
	 * 
	 * @param in the input stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(InputStream in) throws IOException {
		try (TransactionalOutputStream out = openOutputStream()) {
			in.transferTo(out);
			out.commit();
		}
	}
	
	/**
	 * Stores the content read from a reader to this this blob.
	 * 
	 * @param reader the reader
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(Reader reader) throws IOException {
		try (TransactionalWriter writer = openWriter()) {
			reader.transferTo(writer);
			writer.commit();
		}
	}
	
	/**
	 * Executes a callback with an {@link OutputStream} for writing to this blob.  The output stream is automatically
	 * closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(OutputStreamCallback callback) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out)) {
			callback.accept(shielded);
			out.commit();
		}
	}
	
	/**
	 * Executes a callback with a {@link PrintStream} for writing to this blob.  The output stream is automatically
	 * closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(PrintStreamCallback callback) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out);
				PrintStream ps = new PrintStream(shielded)) {
			callback.accept(ps);
			out.commit();
		}
	}
	
	/**
	 * Executes a callback with a {@link Writer} for writing to this blob.  The output stream is automatically
	 * closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @throws IOException if an I/O error occurred
	 */
	public default void store(WriterCallback callback) throws IOException {
		try (TransactionalWriter out = openWriter();
				CloseShieldWriter shielded = CloseShieldWriter.wrap(out)) {
			callback.accept(shielded);
			out.commit();
		}
	}
	
	/**
	 * Executes the callback with an {@link OutputStream} for writing to this blob, but only if the blob does not
	 * already exist.  The stream is automatically closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob did not exist and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean storeIfMissing(OutputStreamCallback callback) throws IOException {
		return ifMissing(b -> b.store(callback));
	}
	
	/**
	 * Executes the callback with a {@link PrintStream} for writing to this blob, but only if the blob does not
	 * already exist.  The stream is automatically closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob did not exist and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean storeIfMissing(PrintStreamCallback callback) throws IOException {
		return ifMissing(b -> b.store(callback));
	}
	
	/**
	 * Executes the callback with a {@link Writer} for writing to this blob, but only if the blob does not
	 * already exist.  The stream is automatically closed when the callback returns.
	 * 
	 * @param callback the callback function
	 * @return {@code true} if the blob did not exist and the callback was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean storeIfMissing(WriterCallback callback) throws IOException {
		return ifMissing(b -> b.store(callback));
	}
	
	/**
	 * Stores a serializable object to the blob.
	 * 
	 * @param value the object to store
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeObject(Object value) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(value);
			out.commit();
		}
	}

}
