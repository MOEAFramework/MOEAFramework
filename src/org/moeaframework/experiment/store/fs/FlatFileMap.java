package org.moeaframework.experiment.store.fs;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

public class FlatFileMap extends FileMap {
				
	protected FlatFileMap(File root) {
		super(root);
	}
	
	@Override
	public File map(Schema schema, Key key, DataType dataType) {
		String[] segments = key.getSegments(schema);
		String filename = Arrays.stream(segments).collect(Collectors.joining("_")) + "." + dataType;
		return new File(getRoot(), filename);
	}
	
	@Override
	void validateManifest(TypedProperties properties) {
		super.validateManifest(properties);
	}
	
	@Override
	void createManifest(TypedProperties properties) {
		super.createManifest(properties);
	}
	
	public static FlatFileMap at(File root) {
		return new FlatFileMap(root);
	}

}
