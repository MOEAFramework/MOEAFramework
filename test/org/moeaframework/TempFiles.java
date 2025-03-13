/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Static methods for creating temporary files and directories.  All files and directories produced using these methods
 * are automatically deleted on exit.  Any content placed in a temporary directory will be deleted, even if the file
 * was not produced by these methods.
 */
public class TempFiles {
	
	private static final List<java.io.File> FILES_TO_CLEANUP;
	
	private static final java.io.File TEMP_DIRECTORY;
		
	static {
		FILES_TO_CLEANUP = new ArrayList<>();
		TEMP_DIRECTORY = new java.io.File("temp");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				// process in reverse order (use reversed() if updating to Java 21+)
				while (!FILES_TO_CLEANUP.isEmpty()) {
					java.io.File file = FILES_TO_CLEANUP.remove(FILES_TO_CLEANUP.size() - 1);
					
					try {
						if (file.isDirectory()) {
							FileUtils.deleteDirectory(file);
						} else {
							FileUtils.delete(file);
						}
					} catch (IOException e) {
						System.err.println(e);
					}
				}
			}
			
		});
	}
	
	private TempFiles() {
		super();
	}

	public static File createFileWithExtension(String extension) throws IOException {
		if (extension != null && extension.length() > 0 && extension.charAt(0) != '.') {
			extension = "." + extension;
		}
		
		java.io.File file = java.io.File.createTempFile("test", extension);
		file.delete(); // remove the empty file
		deleteOnExit(file);
		return new File(file);
	}

	public static File createFile() throws IOException {
		return createFileWithExtension(null);
	}
	
	public static File createDirectory() throws IOException {
		java.io.File directory = Files.createTempDirectory("test").toFile();
		deleteOnExit(directory);
		return new File(directory);
	}
	
	private static void deleteOnExit(java.io.File file) {
		FILES_TO_CLEANUP.add(file);
	}
	
	/**
	 * Intended to look and feel like {@link java.io.File} for code examples, but redirect to a temporary location to
	 * avoid polluting the source tree.  To use:
	 * <pre>
	 *     # Remove the Java import:
	 *     import java.io.File;
	 * 
	 *     # And import this class instead:
	 *     import static org.moeaframework.TempFiles.File;
	 * </pre>
	 */
	public static class File extends java.io.File {
	
		private static final long serialVersionUID = 7856173549658560374L;
	
		public File(String name) throws IOException {
			this(convertPath(name));
		}
		
		File(java.io.File file) {
			super(file.getParent(), file.getName());
		}
		
		private static final java.io.File convertPath(String name) {
			java.io.File file = new java.io.File(name);
			
			if (file.exists()) {
				return file;
			} else {
				return new java.io.File(TEMP_DIRECTORY, name);
			}
		}
		
		public File withContent(String content) throws IOException {
			Files.writeString(toPath(), content);
			return this;
		}
	
	}

}
