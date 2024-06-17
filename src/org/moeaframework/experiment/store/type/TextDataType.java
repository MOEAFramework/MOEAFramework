package org.moeaframework.experiment.store.type;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalWriter;

public class TextDataType<T> extends DataType2<T> {
	
	private TextReader<T> reader;
	
	private TextWriter<T> writer;

	TextDataType(String name, TextReader<T> reader, TextWriter<T> writer) {
		super(name);
		this.reader = reader;
		this.writer = writer;
	}
	
	public T read(DataStore dataStore, Key key) throws IOException {
		try (Reader in = dataStore.reader(key, DataType.of(getName())).asText()) {
			return reader.read(in);
		}
	}
	
	public void write(DataStore dataStore, Key key, T value) throws IOException {
		try (TransactionalWriter out = dataStore.writer(key, DataType.of(getName())).asText()) {
			writer.write(out, value);
			out.commit();
		}
	}
	
	@FunctionalInterface
	static interface TextReader<T> {
		
		T read(Reader in) throws IOException;

	}
	
	@FunctionalInterface
	static interface TextWriter<T> {
		
		void write(Writer out, T value) throws IOException;

	}

}
