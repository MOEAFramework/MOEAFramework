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
package org.moeaframework.analysis.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes output files.
 */
public interface OutputWriter extends Closeable {
	
	/**
	 * Returns the number of entries in the file. If the file already existed, this returns the number of complete
	 * entries in the output file. This value is incremented on every invocation to the {@link #write} method.
	 * 
	 * @return the number of entries in the file
	 */
	public int getNumberOfEntries();
	
	/**
	 * Writes the specified non-dominated population and optional attributes to the file.
	 * 
	 * @param entry the non-dominated population and optional attributes
	 * @throws IOException if an I/O error occurred
	 */
	public void write(ResultEntry entry) throws IOException;
	
	/**
	 * Replaces the destination file with the source file, but only if content is different.  This avoids changing the
	 * modification timestamp on the file if the files are identical.
	 * 
	 * @param source the source file, which is required to exist
	 * @param destination the destination file, which might not exist
	 * @return {@code true} if the destination was updated; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public static boolean replace(File source, File destination) throws IOException {
		Path sourcePath = source.toPath();
		Path destinationPath = destination.toPath();
		
		if (!destination.exists() || Files.mismatch(sourcePath, destinationPath) >= 0) {
			Files.move(sourcePath, destinationPath,  StandardCopyOption.REPLACE_EXISTING);
			return true;
		} else {
			Files.deleteIfExists(sourcePath);
			return false;
		}
	}

}
