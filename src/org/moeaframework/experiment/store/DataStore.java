package org.moeaframework.experiment.store;

import org.moeaframework.experiment.store.schema.Schema;

/**
 * Interface for storing data or objects to some persistent backend.
 */
public interface DataStore {
	
	Schema getSchema();
	
	DataReader reader(Key key, DataType dataType);
	
	DataWriter writer(Key key, DataType dataType);
	
	public default DataReader reader(DataReference reference) {
		return reader(reference.getKey(), reference.getDataType());
	}
	
	public default DataWriter writer(DataReference reference) {
		return writer(reference.getKey(), reference.getDataType());
	}

	public default boolean contains(Key key, DataType dataType) {
		return reader(key, dataType).exists();
	}
	
	public default boolean contains(DataReference reference) {
		return contains(reference.getKey(), reference.getDataType());
	}

}
