/* Copyright 2009-2018 David Hadka
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.commons.lang3.Validate;
import org.moeaframework.core.Settings;

/*
 * The following code is based on the Files and ByteStream classes by
 * Chris Nokleberg.  The code is replicated here since both classes are 
 * tagged with the @Beta annotation, indicating its API is subject to 
 * incompatible changes in future releases.
 * 
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Helper methods for working with files beyond what is provided by
 * {@link File}.
 */
public class FileUtils {
	
	/**
	 * Error message when unable to delete a file.
	 */
	private static final String UNABLE_TO_DELETE = "unable to delete {0}";
	
	/**
	 * Error message when unable to create a directory.
	 */
	private static final String UNABLE_TO_MKDIR = "unable to mkdir {0}";
	
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
		Validate.notNull(source, "source is null");
		Validate.notNull(destination, "destination is null");
		
		if (source.equals(destination)) {
			return;
		}

		if (!source.renameTo(destination)) {
			copy(source, destination);
			
			if (!source.delete()) {
				if (!destination.delete()) {
					throw new IOException(MessageFormat.format(UNABLE_TO_DELETE,
							destination));
				}
				
				throw new IOException(MessageFormat.format(UNABLE_TO_DELETE, 
						source));
			}
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
		Validate.notNull(source, "source is null");
		Validate.notNull(destination, "destination is null");
		
		if (source.equals(destination)) {
			return;
		}
		
		InputStream input = null;
		OutputStream output = null;

		try {
			input = new FileInputStream(source);

			try {
				output = new FileOutputStream(destination);
				
				copy(input, output);
			} finally {
				if (output != null) {
					output.close();
				}
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * Copies all bytes from the input stream to the output stream.
	 * Does not close or flush either stream.
	 *
	 * @param from the input stream to read from
	 * @param to the output stream to write to
	 * @return the number of bytes copied
	 * @throws IOException if an I/O error occurred
	 */
	private static long copy(InputStream from, OutputStream to)
	throws IOException {
		byte[] buf = new byte[Settings.BUFFER_SIZE];
		long total = 0;

		while (true) {
			int r = from.read(buf);

			if (r == -1) {
				break;
			}

			to.write(buf, 0, r);
			total += r;
		}

		return total;
	}

	/**
	 * Deletes a file.
	 *
	 * @param file the file to delete
	 * @throws IOException if the file could not be deleted
	 */
	public static void delete(File file) throws IOException {
		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException(MessageFormat.format(UNABLE_TO_DELETE, 
						file));
			}
		}
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
				throw new IOException(MessageFormat.format(UNABLE_TO_MKDIR,
						directory));
			}
		} else {
			if (!directory.mkdirs()) {
				throw new IOException(MessageFormat.format(UNABLE_TO_MKDIR,
						directory));
			}
		}
	}

}
