package org.moeaframework.experiment.store.fs;

import java.io.File;

import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Hash;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

public class HashFileMap extends FileMap {

	private final int prefixLength;
		
	protected HashFileMap(File root, int prefixLength) {
		super(root);
		this.prefixLength = prefixLength;
	}
	
	@Override
	public File map(Schema schema, Key key, DataType dataType) {
		key.getSegments(schema); // validate key
		return map(Hash.of(key, dataType));
	}
	
	private File map(Hash hash) {
		String hexString = hash.toString();
		File path = getRoot();
		
		if (prefixLength > 0) {
			String prefix = hexString.substring(0, prefixLength);
			path = new File(path, prefix);
		}
				
		return new File(path, hexString);
	}
	
	public static HashFileMap at(File root) {
		return at(root, 2);
	}
	
	public static HashFileMap at(File root, int prefixLength) {
		return new HashFileMap(root, prefixLength);
	}
	
	@Override
	void validateManifest(TypedProperties properties) {
		super.validateManifest(properties);
		
		if (prefixLength != properties.getInt("prefixLength")) {
			throw new ManifestValidationException("Expected prefix length of " + prefixLength + " but was " +
					properties.getInt("prefixLength"));
		}
	}
	
	@Override
	void createManifest(TypedProperties properties) {
		super.createManifest(properties);
		properties.setInt("prefixLength", prefixLength);
	}

}
