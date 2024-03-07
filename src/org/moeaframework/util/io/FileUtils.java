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
package org.moeaframework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;

/**
 * Helper methods for working with files beyond what is provided by {@link File}.
 */
public class FileUtils {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private FileUtils() {
		super();
	}

	/**
	 * Moves the file from one path to another. This method can rename a file or
	 * move it to a different directory, like the Unix {@code mv} command.
	 *
	 * @param source the source file
	 * @param destination the destination file
	 * @throws IOException if an I/O error occurs
	 */
	public static void move(File source, File destination) throws IOException {
		if (source.equals(destination)) {
			return;
		}
		
		try {
			Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (NoSuchFileException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	/**
	 * Copies all the bytes from one file to another.
	 *.
	 * @param source the source file
	 * @param destination the destination file
	 * @throws IOException if an I/O error occurred
	 */
	public static void copy(File source, File destination) throws IOException {
		if (source.equals(destination)) {
			return;
		}
		
		try {
			Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (NoSuchFileException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	/**
	 * Deletes a file.
	 *
	 * @param file the file to delete
	 * @throws IOException if the file could not be deleted
	 */
	public static void delete(File file) throws IOException {
		Files.deleteIfExists(file.toPath());
	}
	
	/**
	 * Creates the specified directory if it does not yet exist.
	 * 
	 * @param directory the directory to create
	 * @throws IOException if the directory could not be created
	 */
	public static void mkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				throw new IOException("the path " + directory + " exists but is not a directory");
			}
		} else {
			if (!directory.mkdirs()) {
				throw new IOException("failed to create directory " + directory);
			}
		}
	}
	
	/**
	 * Reads the file using the UTF8 charset.
	 * 
	 * @param file the file to read
	 * @return the content of the file
	 * @throws IOException if the file does not exist or an I/O error occurred while reading the file
	 */
	public static String readUTF8(File file) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToString(file, "UTF8");
	}

}
