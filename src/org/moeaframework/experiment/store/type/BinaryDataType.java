package org.moeaframework.experiment.store.type;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;

public class BinaryDataType<T> extends DataType2<T> {
	
	private BinaryReader<T> reader;
	
	private BinaryWriter<T> writer;

	BinaryDataType(String name, BinaryReader<T> reader, BinaryWriter<T> writer) {
		super(name);
		this.reader = reader;
		this.writer = writer;
	}
	
	public T read(DataStore dataStore, Key key) throws IOException {
		try (InputStream in = dataStore.reader(key, DataType.of(getName())).asBinary()) {
			return reader.read(in);
		}
	}
	
	public void write(DataStore dataStore, Key key, T value) throws IOException {
		try (TransactionalOutputStream out = dataStore.writer(key, DataType.of(getName())).asBinary()) {
			writer.write(out, value);
			out.commit();
		}
	}
	
	@FunctionalInterface
	static interface BinaryReader<T> {
		
		T read(InputStream in) throws IOException;

	}
	
	@FunctionalInterface
	static interface BinaryWriter<T> {
		
		void write(OutputStream out, T value) throws IOException;

	}

}
