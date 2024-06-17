package org.moeaframework.experiment.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A write context for a given key in the data store, allowing the caller to modify content in the data store.  Use
 * {@link #asText()} or {@link #asBinary()} to obtain a {@link Writer} or {@link OutputStream}, respectively, to store
 * content.
 */
public interface DataWriter {
	
	boolean exists();
	
	boolean delete();
	
	TransactionalWriter asText() throws IOException;
	
	TransactionalOutputStream asBinary() throws IOException;
	
	public default void store(File file) throws IOException {
		store(file.toPath());
	}
	
	public default void store(Path path) throws IOException {
		try (TransactionalOutputStream out = asBinary()) {
			Files.copy(path, out);
			out.commit();
		}
	}
	
	public default void store(InputStream in) throws IOException {
		try (TransactionalOutputStream out = asBinary()) {
			in.transferTo(out);
			out.commit();
		}
	}
	
	public default void store(Reader in) throws IOException {
		try (TransactionalWriter out = asText()) {
			in.transferTo(out);
			out.commit();
		}
	}
	
	public default <T extends Serializable> void storeObject(T value) throws IOException {
		try (TransactionalOutputStream out = asBinary();
				ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(value);
			out.commit();
		}
	}

}
