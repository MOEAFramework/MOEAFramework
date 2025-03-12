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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.function.IOConsumer;
import org.apache.commons.io.function.IOFunction;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CloseShieldWriter;
import org.moeaframework.core.Stateful;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TableFormat;

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
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	boolean exists() throws DataStoreException;
	
	/**
	 * Deletes this blob if it exists.
	 * 
	 * @return {@code true} if the blob was deleted; {@code false} if the blob does not exist
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	boolean delete() throws DataStoreException;
	
	/**
	 * Returns the last modified time of the blob.
	 * 
	 * @return the last modified time
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	Instant lastModified() throws DataStoreException;
	
	/**
	 * Creates and returns a {@link Reader} for reading text from this blob.  The caller is responsible for closing the
	 * reader when finished.
	 * 
	 * @return a reader for this blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	Reader openReader() throws DataStoreException;
	
	/**
	 * Creates and returns an {@link InputStream} for reading binary data from this blob.  The caller is responsible for
	 * closing the stream when finished.
	 * 
	 * @return an input stream for this blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	InputStream openInputStream() throws DataStoreException;
	
	/**
	 * Creates and returns a {@link TransactionalWriter} for writing text to this blob.  The caller is responsible for
	 * committing and closing the writer when finished.  If the writer is closed before being committed, any written
	 * content is discarded.
	 * 
	 * @return the writer for this blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	TransactionalWriter openWriter() throws DataStoreException;
	
	/**
	 * Creates and returns a {@link TransactionalOutputStream} for writing binary data to this blob.  The caller is
	 * responsible for committing and closing the stream when finished.  If the stream is closed before being committed,
	 * any written content is discarded.
	 * 
	 * @return the output stream for this blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	TransactionalOutputStream openOutputStream() throws DataStoreException;
	
	/**
	 * Executes the consumer function if this blob is missing.
	 * 
	 * @param consumer the consumer function
	 * @return {@code true} if the blob is missing and the consumer was invoked; {@code false} otherwise
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default boolean ifMissing(IOConsumer<Blob> consumer) throws DataStoreException {
		try {
			if (!exists()) {
				consumer.accept(this);
				return true;
			}
			
			return false;
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Executes the consumer function if this blob exists.
	 * 
	 * @param consumer the consumer function
	 * @return {@code true} if the blob exists and the consumer was invoked; {@code false} otherwise
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default boolean ifFound(IOConsumer<Blob> consumer) throws DataStoreException {
		try {
			if (exists()) {
				consumer.accept(this);
				return true;
			}
			
			return false;
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Extracts the content of this blob to a file.
	 * 
	 * @param file the destination file
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void extractTo(File file) throws DataStoreException {
		extractTo(file.toPath());
	}
	
	/**
	 * Extracts the content of this blob to a path.
	 * 
	 * @param path the destination path
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void extractTo(Path path) throws DataStoreException {
		try (InputStream in = openInputStream()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Transfers the content of this blob to an output stream.
	 * 
	 * @param out the output stream
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void extractTo(OutputStream out) throws DataStoreException {
		try (InputStream in = openInputStream()) {
			in.transferTo(out);
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Transfers the content of this blob to a writer.
	 * 
	 * @param out the writer
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void extractTo(Writer out) throws DataStoreException {
		try (Reader in = openReader()) {
			in.transferTo(out);
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Stores the contents of a file to this blob.
	 * 
	 * @param file the source file
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeFrom(File file) throws DataStoreException {
		storeFrom(file.toPath());
	}
	
	/**
	 * Stores the contents of a path to this blob.
	 * 
	 * @param path the source path
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeFrom(Path path) throws DataStoreException {
		try (TransactionalOutputStream out = openOutputStream()) {
			Files.copy(path, out);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Extracts the content of this blob to a string.
	 * 
	 * @return the content of the blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default String extractText() throws DataStoreException {
		return extractReader(IOUtils::toString);
	}
	
	/**
	 * Extracts the content of this blob to bytes.
	 * 
	 * @return the content of the blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default byte[] extractBytes() throws DataStoreException {
		return extractInputStream(IOUtils::toByteArray);
	}
	
	/**
	 * Extracts the content of this blob to a {@link Population}.
	 * 
	 * @return the content of the blob
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default Population extractPopulation() throws DataStoreException {
		return extractReader(Population::load);
	}
	
	/**
	 * Executes a function with an {@link InputStream} for reading this blob.  The input stream is automatically closed
	 * when the function returns.
	 * 
	 * @param <R> the return type
	 * @param function the function
	 * @return the object read from the stream
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default <R> R extractInputStream(IOFunction<InputStream, R> function) throws DataStoreException {
		try (InputStream in = openInputStream()) {
			return function.apply(in);
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Executes a function with a {@link Reader} for reading this blob.  The reader is automatically closed when the
	 * function returns.
	 * 
	 * @param <R> the return type
	 * @param function the function
	 * @return the object read from the reader
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default <R> R extractReader(IOFunction<Reader, R> function) throws DataStoreException {
		try (Reader in = openReader()) {
			return function.apply(in);
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Extracts the state and loads it into the {@link Stateful} object.
	 * 
	 * @param <T> the object type
	 * @param value the stateful object
	 * @return the stateful object
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ClassNotFoundException if the class of the serialized object could not be found
	 */
	public default <T extends Stateful> T extractState(T value) throws DataStoreException, ClassNotFoundException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			value.loadState(ois);
			return value;
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @return the deserialized object
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ClassNotFoundException if the class of the serialized object could not be found
	 */
	public default Object extractObject() throws DataStoreException, ClassNotFoundException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			return ois.readObject();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @param <T> the return type
	 * @param type the type of the object
	 * @return the deserialized object cast to the given type
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ClassNotFoundException if the class of the serialized object could not be found
	 */
	public default <T> T extractObject(Class<T> type) throws DataStoreException, ClassNotFoundException {
		return type.cast(extractObject());
	}
	
	/**
	 * Stores the given string to this blob.
	 * 
	 * @param text the text to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeText(String text) throws DataStoreException {
		storeWriter((Writer writer) -> writer.append(text));
	}
	
	/**
	 * Stores the given data to this blob.
	 * 
	 * @param data the data to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeBytes(byte[] data) throws DataStoreException {
		storeOutputStream((OutputStream stream) -> stream.write(data));
	}
	
	/**
	 * Stores the given {@link Population} object to this blob as a result file.
	 * 
	 * @param population the population to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storePopulation(Population population) throws DataStoreException {
		storeWriter(population::save);
	}
	
	/**
	 * Stores the given {@link Formattable} object to this blob using the {@link TableFormat#Plaintext} format.
	 * 
	 * @param formattable the formattable object to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeText(Formattable<?> formattable) throws DataStoreException {
		storeText(formattable, TableFormat.Plaintext);
	}
	
	/**
	 * Stores the given {@link Formattable} object to this blob using the specified table format.
	 * 
	 * @param formattable the formattable object to store
	 * @param tableFormat the table format
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeText(Formattable<?> formattable, TableFormat tableFormat) throws DataStoreException {
		storePrintStream((PrintStream ps) -> formattable.save(tableFormat, ps));
	}
	
	/**
	 * Stores the content read from an input stream to this this blob.
	 * 
	 * @param in the input stream
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeFrom(InputStream in) throws DataStoreException {
		try (TransactionalOutputStream out = openOutputStream()) {
			in.transferTo(out);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Stores the content read from a reader to this this blob.
	 * 
	 * @param reader the reader
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeFrom(Reader reader) throws DataStoreException {
		try (TransactionalWriter writer = openWriter()) {
			reader.transferTo(writer);
			writer.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Executes a consumer with an {@link OutputStream} for writing to this blob.  The stream is automatically closed
	 * when the consumer returns.
	 * 
	 * @param consumer the consumer
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeOutputStream(IOConsumer<OutputStream> consumer) throws DataStoreException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out)) {
			consumer.accept(shielded);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Executes a consumer with a {@link PrintStream} for writing to this blob.  The stream is automatically closed
	 * when the consumer returns.
	 * 
	 * @param consumer the consumer
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storePrintStream(IOConsumer<PrintStream> consumer) throws DataStoreException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out);
				PrintStream ps = new PrintStream(shielded)) {
			consumer.accept(ps);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Executes a consumer with a {@link Writer} for writing to this blob.  The writer is automatically closed when the
	 * consumer returns.
	 * 
	 * @param consumer the consumer function
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default void storeWriter(IOConsumer<Writer> consumer) throws DataStoreException {
		try (TransactionalWriter out = openWriter();
				CloseShieldWriter shielded = CloseShieldWriter.wrap(out)) {
			consumer.accept(shielded);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Stores a {@link Stateful} object to the blob.
	 * 
	 * @param <T> the object type
	 * @param value the stateful object to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default <T extends Stateful> void storeState(T value) throws DataStoreException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			value.saveState(oos);
			out.commit();
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}	
	
	/**
	 * Stores a serializable object to the blob.
	 * 
	 * @param value the object to store
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws NotSerializableException if the object is not serializable
	 */
	public default void storeObject(Object value) throws DataStoreException, NotSerializableException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(value);
			out.commit();
		} catch (NotSerializableException e) {
			throw e;
		} catch (IOException e) {
			throw DataStoreException.wrap(e, this);
		}
	}
	
	/**
	 * Returns the URI for this blob, which can be used with {@link DataStoreFactory#resolveBlob(java.net.URI)}.
	 * 
	 * @return the URI
	 */
	public default URI getURI() {
		return DataStoreURI.resolve(this);
	}
}
