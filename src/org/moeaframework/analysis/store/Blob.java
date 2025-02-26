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
	 * Executes the consumer function if this blob is missing.
	 * 
	 * @param consumer the consumer function
	 * @return {@code true} if the blob is missing and the consumer was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean ifMissing(IOConsumer<Blob> consumer) throws IOException {
		if (!exists()) {
			consumer.accept(this);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Executes the consumer function if this blob exists.
	 * 
	 * @param consumer the consumer function
	 * @return {@code true} if the blob exists and the consumer was invoked; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean ifFound(IOConsumer<Blob> consumer) throws IOException {
		if (exists()) {
			consumer.accept(this);
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
	public default void extractTo(File file) throws IOException {
		extractTo(file.toPath());
	}
	
	/**
	 * Extracts the content of this blob to a path.
	 * 
	 * @param path the destination path
	 * @throws IOException if an I/O error occurred
	 */
	public default void extractTo(Path path) throws IOException {
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
	public default void extractTo(OutputStream out) throws IOException {
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
	public default void extractTo(Writer out) throws IOException {
		try (Reader in = openReader()) {
			in.transferTo(out);
		}
	}
	
	/**
	 * Stores the contents of a file to this blob.
	 * 
	 * @param file the source file
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeFrom(File file) throws IOException {
		storeFrom(file.toPath());
	}
	
	/**
	 * Stores the contents of a path to this blob.
	 * 
	 * @param path the source path
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeFrom(Path path) throws IOException {
		try (TransactionalOutputStream out = openOutputStream()) {
			Files.copy(path, out);
			out.commit();
		}
	}
	
	/**
	 * Extracts the content of this blob to a string.
	 * 
	 * @return the content of the blob
	 * @throws IOException if an I/O error occurred
	 */
	public default String extractText() throws IOException {
		return extractReader(IOUtils::toString);
	}
	
	/**
	 * Extracts the content of this blob to bytes.
	 * 
	 * @return the content of the blob
	 * @throws IOException if an I/O error occurred
	 */
	public default byte[] extractBytes() throws IOException {
		return extractInputStream(IOUtils::toByteArray);
	}
	
	/**
	 * Extracts the content of this blob to a {@link Population}.
	 * 
	 * @return the content of the blob
	 * @throws IOException if an I/O error occurred
	 */
	public default Population extractPopulation() throws IOException {
		return extractReader(Population::load);
	}
	
	/**
	 * Executes a function with an {@link InputStream} for reading this blob.  The input stream is automatically closed
	 * when the function returns.
	 * 
	 * @param <R> the return type
	 * @param function the function
	 * @return the object read from the stream
	 * @throws IOException if an I/O error occurred
	 */
	public default <R> R extractInputStream(IOFunction<InputStream, R> function) throws IOException {
		try (InputStream in = openInputStream()) {
			return function.apply(in);
		}
	}
	
	/**
	 * Executes a function with a {@link Reader} for reading this blob.  The reader is automatically closed when the
	 * function returns.
	 * 
	 * @param <R> the return type
	 * @param function the function
	 * @return the object read from the reader
	 * @throws IOException if an I/O error occurred
	 */
	public default <R> R extractReader(IOFunction<Reader, R> function) throws IOException {
		try (Reader in = openReader()) {
			return function.apply(in);
		}
	}
	
	/**
	 * Extracts the state and loads it into the {@link Stateful} object.
	 * 
	 * @param value the stateful object
	 * @return the stateful object
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException 
	 */
	public default <T extends Stateful> T extractState(T value) throws IOException, ClassNotFoundException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			value.loadState(ois);
			return value;
		}
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @return the deserialized object
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException if the class of the serialized object could not be found
	 */
	public default Object extractObject() throws IOException, ClassNotFoundException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			return ois.readObject();
		}
	}
	
	/**
	 * Extracts the blob and deserializes the content.
	 * 
	 * @param <T> the return type
	 * @param type the type of the object
	 * @return the deserialized object cast to the given type
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException if the class of the serialized object could not be found
	 */
	public default <T> T extractObject(Class<T> type) throws IOException, ClassNotFoundException {
		return type.cast(extractObject());
	}
	
	/**
	 * Stores the given string to this blob.
	 * 
	 * @param text the text to store
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeText(String text) throws IOException {
		storeWriter((Writer writer) -> writer.append(text));
	}
	
	/**
	 * Stores the given data to this blob.
	 * 
	 * @param data the data to store
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeBytes(byte[] data) throws IOException {
		storeOutputStream((OutputStream stream) -> stream.write(data));
	}
	
	/**
	 * Stores the given {@link Population} object to this blob as a result file.
	 * 
	 * @param population the population to store
	 * @throws IOException if an I/O error occurred
	 */
	public default void storePopulation(Population population) throws IOException {
		storeWriter(population::save);
	}
	
	/**
	 * Stores the given {@link Formattable} object to this blob using the {@link TableFormat#Plaintext} format.
	 * 
	 * @param formattable the formattable object to store
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeText(Formattable<?> formattable) throws IOException {
		storeText(formattable, TableFormat.Plaintext);
	}
	
	/**
	 * Stores the given {@link Formattable} object to this blob using the specified table format.
	 * 
	 * @param formattable the formattable object to store
	 * @param tableFormat the table format
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeText(Formattable<?> formattable, TableFormat tableFormat) throws IOException {
		storePrintStream((PrintStream ps) -> formattable.save(tableFormat, ps));
	}
	
	/**
	 * Stores the content read from an input stream to this this blob.
	 * 
	 * @param in the input stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeFrom(InputStream in) throws IOException {
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
	public default void storeFrom(Reader reader) throws IOException {
		try (TransactionalWriter writer = openWriter()) {
			reader.transferTo(writer);
			writer.commit();
		}
	}
	
	/**
	 * Executes a consumer with an {@link OutputStream} for writing to this blob.  The stream is automatically closed
	 * when the consumer returns.
	 * 
	 * @param consumer the consumer
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeOutputStream(IOConsumer<OutputStream> consumer) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out)) {
			consumer.accept(shielded);
			out.commit();
		}
	}
	
	/**
	 * Executes a consumer with a {@link PrintStream} for writing to this blob.  The stream is automatically closed
	 * when the consumer returns.
	 * 
	 * @param consumer the consumer
	 * @throws IOException if an I/O error occurred
	 */
	public default void storePrintStream(IOConsumer<PrintStream> consumer) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out);
				PrintStream ps = new PrintStream(shielded)) {
			consumer.accept(ps);
			out.commit();
		}
	}
	
	/**
	 * Executes a consumer with a {@link Writer} for writing to this blob.  The writer is automatically closed when the
	 * consumer returns.
	 * 
	 * @param consumer the consumer function
	 * @throws IOException if an I/O error occurred
	 */
	public default void storeWriter(IOConsumer<Writer> consumer) throws IOException {
		try (TransactionalWriter out = openWriter();
				CloseShieldWriter shielded = CloseShieldWriter.wrap(out)) {
			consumer.accept(shielded);
			out.commit();
		}
	}
	
	/**
	 * Stores a {@link Stateful} object to the blob.
	 * 
	 * @param value the stateful object to store
	 * @throws IOException if an I/O error occurred
	 */
	public default <T extends Stateful> void storeState(T value) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			value.saveState(oos);
			out.commit();
		}
	}	
	
	/**
	 * Stores a serializable object to the blob.
	 * 
	 * @param value the object to store
	 * @throws IOException if an I/O error occurred
	 * @throws NotSerializableException if the object is not serializable
	 */
	public default void storeObject(Object value) throws IOException, NotSerializableException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(value);
			out.commit();
		}
	}

}
