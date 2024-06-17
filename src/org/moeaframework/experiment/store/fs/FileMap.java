package org.moeaframework.experiment.store.fs;

import java.io.File;

import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

public abstract class FileMap {
	
	protected final File root;
	
	public FileMap(File root) {
		super();
		this.root = root;
	}
	
	public File getRoot() {
		return root;
	}
	
	void validateSchema(Schema schema) {
		// Do nothing.  All schemas are valid unless overridden.  This could, for example, check if the fields map
		// to valid file names (e.g., max length and invalid characters).
	}
	
	abstract File map(Schema schema, Key key, DataType dataType);
	
	void validateManifest(TypedProperties properties) {
		if (!getClass().getName().equals(properties.getString("fileMap"))) {
			throw new ManifestValidationException("Expected mapping " + getClass().getName() + " but was " +
					properties.getString("fileMap"));
		}
	}
	
	void createManifest(TypedProperties properties) {
		properties.setString("fileMap", getClass().getName());
	}

}
