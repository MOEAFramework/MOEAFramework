package org.moeaframework.experiment.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

/**
 * A read-only context for a given key in the data store.  Use {@link #asText()} or {@link #asBinary()} to obtain a
 * {@link Reader} or {@link InputStream}, respectively, to read content.
 */
public interface DataReader {
	
	boolean exists();
	
	Instant lastModified();
	
	Reader asText() throws IOException;
	
	InputStream asBinary() throws IOException;
	
	public default void extract(File file) throws IOException {
		extract(file.toPath());
	}
	
	public default void extract(Path path) throws IOException {
		try (InputStream in = asBinary()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public default void extract(OutputStream out) throws IOException {
		try (InputStream in = asBinary()) {
			in.transferTo(out);
		}
	}
	
	public default void extract(Writer out) throws IOException {
		try (Reader in = asText()) {
			in.transferTo(out);
		}
	}
	
	public default <T extends Serializable> T extractObject(Class<T> type) throws IOException {
		try (InputStream in = asBinary();
				ObjectInputStream ois = new ObjectInputStream(in)) {
			return type.cast(ois.readObject());
		} catch (ClassNotFoundException e) {
			throw new IOException("Deserialization failed", e);
		}
	}

}
