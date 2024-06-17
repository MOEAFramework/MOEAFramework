package org.moeaframework.experiment.store.fs;

import java.io.File;

import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

/**
 * Stores files in a hierarchical structure.  The keys define the directory structure and the data type defines the
 * file name.
 */
public class HierarchicalFileMap extends FileMap {
				
	protected HierarchicalFileMap(File root) {
		super(root);
	}
	
	@Override
	public File map(Schema schema, Key key, DataType dataType) {
		String[] segments = key.getSegments(schema);		
		File path = getRoot();
				
		for (int i = 0; i < segments.length; i++) {
			path = new File(path, segments[i]);
		}
			
		return new File(path, dataType.toString());
	}
	
	@Override
	void validateManifest(TypedProperties properties) {
		super.validateManifest(properties);
	}
	
	@Override
	void createManifest(TypedProperties properties) {
		super.createManifest(properties);
	}
	
	public static HierarchicalFileMap at(File root) {
		return new HierarchicalFileMap(root);
	}

}
