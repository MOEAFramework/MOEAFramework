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
package org.moeaframework.analysis.store.fs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.Constructable;

/**
 * Defines how containers and blobs are mapped to paths on a file system.
 * <p>
 * Since arbitrary names can be used, certain characters are escaped:
 * <ol>
 *   <li>Invalid or reserved characters are escaped by their hex value (e.g., {@code "/"} becomes {@code "%2F"}).  This
 *       also includes whitespace characters.
 *   <li>The character {@code "%"}, if not being used to escape a value, is represented by {@code "%%"}.
 *   <li>Special filenames, such as {@code "."}, {@code ".."}, and {@code "~"}, are escaped to avoid interpreting as
 *       relative paths.
 * </ol>
 */
public abstract class FileMap implements Constructable {
			
	private static final CharSequenceTranslator filenameTranslator;
	
	private static final Map<String, String> specialFilenames;
	
	static {
		final Map<String, String> filenameEscapeMap = new HashMap<>();
		filenameEscapeMap.put("%", "%%");
		filenameEscapeMap.put("\\", "%5C");
		filenameEscapeMap.put("/", "%2F");
		filenameEscapeMap.put(":", "%3A");
		filenameEscapeMap.put("\"", "%22");
		filenameEscapeMap.put("*", "%2A");
		filenameEscapeMap.put("?", "%3F");
		filenameEscapeMap.put("<", "%3C");
		filenameEscapeMap.put(">", "%3E");
		filenameEscapeMap.put("|", "%7C");
		filenameEscapeMap.put(" ", "%20");
		filenameEscapeMap.put("\t", "%09");
		filenameEscapeMap.put("\n", "%0A");
		filenameEscapeMap.put("\r", "%0D");
		
		filenameTranslator = new AggregateTranslator(
				new LookupTranslator(Collections.unmodifiableMap(filenameEscapeMap)));
		
		specialFilenames = new HashMap<>();
		specialFilenames.put(".", "%46");
		specialFilenames.put("..", "%46%46");
		specialFilenames.put("~", "%7E");
	}
	
	public FileMap() {
		super();
	}
	
	/**
	 * Returns the path to the container associated with the given reference.  If containers are not supported, this
	 * method may throw {@link UnsupportedOperationException}.
	 * 
	 * @param schema the schema defining the structure
	 * @param root the root directory
	 * @param reference the container reference
	 * @return the container path
	 * @throws IOException if an I/O error occurred
	 * @throws UnsupportedOperationException if containers are not supported
	 */
	abstract Path mapContainer(Schema schema, Path root, Reference reference) throws IOException;
	
	/**
	 * Returns the path to the blob associated with the given reference and name.
	 * 
	 * @param schema the schema defining the structure
	 * @param root the root directory
	 * @param reference the container reference
	 * @param name the blob name
	 * @return the blob path
	 * @throws IOException if an I/O error occurred
	 */
	abstract Path mapBlob(Schema schema, Path root, Reference reference, String name) throws IOException;
	
	/**
	 * Updates the manifest with information about this file map.
	 * 
	 * @param manifest the manifest
	 */
	public void updateManifest(Manifest manifest) {
		manifest.setString("fileMap", getDefinition());
	}
	
	/**
	 * Returns a file system-safe path for the give file name.  
	 * 
	 * @param filename the file name
	 * @return the file system-safe path
	 */
	public static Path escapePath(String filename) {
		if (specialFilenames.containsKey(filename)) {
			return Path.of(specialFilenames.get(filename));
		}
		
		return Path.of(filenameTranslator.translate(filename));
	}

}
