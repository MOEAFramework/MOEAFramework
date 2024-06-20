package org.moeaframework.experiment.store.fs;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;

public abstract class FileMap {
	
	private static final Pattern FILENAME_INVALID_CHAR = Pattern.compile("[\\\\/:\"*?<>|]");
	
	protected final File root;
	
	public FileMap(File root) {
		super();
		this.root = root;
	}
	
	public File getRoot() {
		return root;
	}
	
	protected String cleanPathSegment(String filename) {
		Matcher matcher = FILENAME_INVALID_CHAR.matcher(filename);
		return matcher.replaceAll("_");
	}
	
	abstract File map(Schema schema, Key key, DataType dataType);
	
	protected Manifest getManifest() {
		Manifest manifest = new Manifest();
		manifest.setString("fileMap", getClass().getName());
		return manifest;
	}

}
