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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.moeaframework.analysis.store.Key;
import org.moeaframework.core.Constructable;

/**
 * Defines how containers and blobs are mapped to paths on a file system.
 */
public abstract class FileMap implements Constructable {
	
	public static final Comparator<Path> CASE_INSENSITIVE_ORDER = new CaseInsensitivePathComparator();
		
	private static final CharSequenceTranslator fileNameTranslator;
	
	static {
		final Map<CharSequence, CharSequence> fileNameEscapeMap = new HashMap<>();
		fileNameEscapeMap.put("%", "%%");
		fileNameEscapeMap.put("\\", "%5C");
		fileNameEscapeMap.put("/", "%2F");
		fileNameEscapeMap.put(":", "%3A");
		fileNameEscapeMap.put("\"", "%22");
		fileNameEscapeMap.put("*", "%2A");
		fileNameEscapeMap.put("?", "%3F");
		fileNameEscapeMap.put("<", "%3C");
		fileNameEscapeMap.put(">", "%3E");
		fileNameEscapeMap.put("|", "%7C");
		
		fileNameTranslator = new AggregateTranslator(
				new LookupTranslator(Collections.unmodifiableMap(fileNameEscapeMap)));
	}
	
	public FileMap() {
		super();
	}
	
	/**
	 * Returns the path to the container associated with the given key.  If containers are not supported, this method
	 * may throws {@link UnsupportedOperationException}.
	 * 
	 * @param root the root directory
	 * @param key the key
	 * @return the container path
	 * @throws IOException if an I/O error occurred
	 * @throws UnsupportedOperationException if containers are not supported
	 */
	abstract Path mapContainer(Path root, Key key) throws IOException;
	
	/**
	 * Returns the path to the blob associated with the given key and name.
	 * 
	 * @param root the root directory
	 * @param key the key
	 * @param name the blob name
	 * @return the blob path
	 * @throws IOException if an I/O error occurred
	 */
	abstract Path mapBlob(Path root, Key key, String name) throws IOException;
	
	/**
	 * Returns a file system-safe path for the give file name.
	 * 
	 * @param filename the file name
	 * @return the file system-safe path
	 */
	public static Path escapePath(String filename) {
		if (filename.equals(".")) {
			return Path.of("%46");
		}
		
		if (filename.equals("..")) {
			return Path.of("%46%46");
		}
		
		return Path.of(fileNameTranslator.translate(filename));
	}
	
	/**
	 * Comparator for {@link Path} that performs case-insensitive comparisons of each path segment.
	 */
	protected static class CaseInsensitivePathComparator implements Comparator<Path> {

		@Override
		public int compare(Path path1, Path path2) {
			for (int i = 0; i < Math.min(path1.getNameCount(), path2.getNameCount()); i++) {
				if (i > path1.getNameCount()) {
					return 1;
				}
				
				if (i > path2.getNameCount()) {
					return -1;
				}
				
				int cmp = String.CASE_INSENSITIVE_ORDER.compare(
						path1.getName(i).toString(), path2.getName(i).toString());
				
				if (cmp != 0) {
					return cmp;
				}
			}
			
			return 0;
		}
		
	}

}
