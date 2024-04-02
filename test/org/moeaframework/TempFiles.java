/* Copyright 2009-2024 David Hadka
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Static methods for creating temporary files and directories.  All files and directories produced using these methods
 * are automatically deleted on exit.  Any content placed in directories will be deleted, even if the file was not
 * produced by these methods.
 */
public class TempFiles {
	
	private static final List<File> filesToCleanup = new ArrayList<File>();
	
	private static Thread cleanupThread;
	
	private TempFiles() {
		super();
	}

	public static File createFileWithExtension(String extension) throws IOException {
		File file = File.createTempFile("test", extension);
		file.delete(); // remove the empty file
		deleteOnExit(file);
		return file;
	}

	public static File createFile() throws IOException {
		return createFileWithExtension(null);
	}

	public static File createFileWithContent(String data) throws IOException {
		return createFileWithContent(data, null);
	}
	
	public static File createFileWithContent(String data, String extension) throws IOException {
		File file = createFileWithExtension(extension);
		Files.writeString(file.toPath(), data);
		return file;
	}
	
	public static File createDirectory() throws IOException {
		File directory = Files.createTempDirectory("test").toFile();
		deleteOnExit(directory);
		return directory;
	}
	
	private static void deleteOnExit(File file) {
		if (cleanupThread == null) {
			cleanupThread = new Thread() {

				@Override
				public void run() {
					// process in reverse order (use reversed() if updating to Java 21+)
					while (!filesToCleanup.isEmpty()) {
						File file = filesToCleanup.remove(filesToCleanup.size() - 1);
						
						if (file.exists()) {
							try {
								if (file.isDirectory()) {
									Files.walk(file.toPath())
										.sorted(Comparator.reverseOrder())
										.map(Path::toFile)
										.forEach(File::delete);
								} else {
									file.delete();
								}
							} catch (IOException e) {
								System.err.println(e);
							}
						}
					}
				}
				
			};
			
			Runtime.getRuntime().addShutdownHook(cleanupThread);
		}
		
		filesToCleanup.add(file);
	}

}
