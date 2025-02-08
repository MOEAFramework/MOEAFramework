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

import java.io.File;
import java.io.IOException;

import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Locates resources used for testing.  These methods handle differences between different distributions, including
 * <ol>
 *   <li>Ant source
 *   <li>Maven source
 *   <li>JAR file
 * </ol>
 */
public class TestResources {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TestResources() {
		super();
	}

	/**
	 * Locates the given resource, potentially extracting it to a temporary file, and returns the file.
	 * 
	 * @param resource the resource name
	 * @return the resource file
	 * @throws IOException if an I/O error occurred or the resource was not found
	 */
	public static File asFile(String resource) throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return file;
		} else {
			return Resources.asFile(TestResources.class, resource, ResourceOption.REQUIRED, ResourceOption.ABSOLUTE,
					ResourceOption.TEMPORARY);
		}
	}
	
	/**
	 * Locates a local test resource.
	 *
	 * @param path the resource name
	 * @return the local file
	 */
	public static File getLocalTestResource(String resource) {
		return new File("src/test/resources").exists() ? new File("src/test/resources", resource) :
			new File("test", resource);
	}

}
