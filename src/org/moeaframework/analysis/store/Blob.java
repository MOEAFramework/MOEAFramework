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
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CloseShieldWriter;

public interface Blob {
	
	Key getKey();
	
	String getName();
	
	Container getContainer();
	
	boolean exists() throws IOException;
	
	boolean delete() throws IOException;
	
	Instant lastModified() throws IOException;
	
	Reader openReader() throws IOException;
	
	InputStream openInputStream() throws IOException;
		
	TransactionalWriter openWriter() throws IOException;
	
	TransactionalOutputStream openOutputStream() throws IOException;
	
	public default void extract(File file) throws IOException {
		extract(file.toPath());
	}
	
	public default void extract(Path path) throws IOException {
		try (InputStream in = openInputStream()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public default void extract(OutputStream out) throws IOException {
		try (InputStream in = openInputStream()) {
			in.transferTo(out);
		}
	}
	
	public default void extract(Writer out) throws IOException {
		try (Reader in = openReader()) {
			in.transferTo(out);
		}
	}
	
	public default void extract(InputStreamCallback callback) throws IOException {
		try (InputStream in = openInputStream()) {
			callback.accept(in);
		}
	}
	
	public default void extract(ReaderCallback callback) throws IOException {
		try (Reader in = openReader()) {
			callback.accept(in);
		}
	}
	
	public default <T extends Serializable> T extractObject(Class<T> type) throws IOException {
		try (InputStream in = openInputStream();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			return type.cast(ois.readObject());
		} catch (ClassNotFoundException e) {
			throw new IOException("Deserialization failed", e);
		}
	}
	
	public default void store(File file) throws IOException {
		store(file.toPath());
	}
	
	public default void store(Path path) throws IOException {
		try (TransactionalOutputStream out = openOutputStream()) {
			Files.copy(path, out);
			out.commit();
		}
	}
	
	public default void store(InputStream in) throws IOException {
		try (TransactionalOutputStream out = openOutputStream()) {
			in.transferTo(out);
			out.commit();
		}
	}
	
	public default void store(Reader reader) throws IOException {
		try (TransactionalWriter writer = openWriter()) {
			reader.transferTo(writer);
			writer.commit();
		}
	}
	
	public default void store(OutputStreamCallback callback) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out)) {
			callback.accept(shielded);
			out.commit();
		}
	}
	
	public default void store(PrintStreamCallback callback) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				CloseShieldOutputStream shielded = CloseShieldOutputStream.wrap(out);
				PrintStream ps = new PrintStream(shielded)) {
			callback.accept(ps);
			out.commit();
		}
	}
	
	public default void store(WriterCallback callback) throws IOException {
		try (TransactionalWriter out = openWriter();
				CloseShieldWriter shielded = CloseShieldWriter.wrap(out)) {
			callback.accept(shielded);
			out.commit();
		}
	}
	
	public default <T extends Serializable> void storeObject(T value) throws IOException {
		try (TransactionalOutputStream out = openOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(value);
			out.commit();
		}
	}
	
	@FunctionalInterface
	public static interface IOCallback<T> {
		
		public void accept(T stream) throws IOException;
		
	}
	
	@FunctionalInterface
	public static interface InputStreamCallback extends IOCallback<InputStream> {
				
	}
	
	@FunctionalInterface
	public static interface ReaderCallback extends IOCallback<Reader> {
		
	}
	
	@FunctionalInterface
	public static interface OutputStreamCallback extends IOCallback<OutputStream> {
		
	}
	
	@FunctionalInterface
	public static interface WriterCallback extends IOCallback<Writer> {
		
	}
	
	@FunctionalInterface
	public static interface PrintStreamCallback extends IOCallback<PrintStream> {
		
	}

}
