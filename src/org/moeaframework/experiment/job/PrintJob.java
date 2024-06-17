package org.moeaframework.experiment.job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;

public class PrintJob extends Job {
	
	private final DataType dataType;
	
	private final OutputStream out;
	
	public PrintJob(DataReference reference) {
		this(reference, System.out);
	}
	
	public PrintJob(DataReference reference, OutputStream out) {
		this(reference.getKey(), reference.getDataType(), out);
	}
	
	public PrintJob(Key key, DataType dataType) {
		this(key, dataType, System.out);
	}
	
	public PrintJob(Key key, DataType dataType, OutputStream out) {
		super(key);
		this.dataType = dataType;
		this.out = out;
	}

	@Override
	public void execute(DataStore dataStore) {
		try (InputStream in = dataStore.reader(key, dataType).asBinary()) {
			in.transferTo(out);
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public boolean isComplete(DataStore dataStore) {
		return false;
	}

	@Override
	public Collection<DataReference> requires() {
		return List.of(DataReference.of(key, dataType));
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of();
	}

}
