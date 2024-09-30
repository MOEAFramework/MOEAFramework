package org.moeaframework.snippet;

import java.io.IOException;

import org.moeaframework.TempFiles;

/**
 * Allow examples to use a readable filename, such as {@code new File("name.ext")}, but is redirected to a temporary
 * file to avoid polluting the source tree.
 */
public class File extends java.io.File {

	private static final long serialVersionUID = 7856173549658560374L;
	
	private static final java.io.File tempDirectory;
	
	static {
		try {
			tempDirectory = TempFiles.createDirectory();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public File(String name) throws IOException {
		super(tempDirectory, name);
	}

}