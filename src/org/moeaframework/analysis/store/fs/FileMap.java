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
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.analysis.store.Key;

public abstract class FileMap {
	
	public static final Comparator<Path> CASE_INSENSITIVE_ORDER = new CaseInsensitivePathComparator();
	
	private static final Pattern FILENAME_INVALID_CHAR = Pattern.compile("[\\\\/:\"*?<>|]");
	
	protected final Path root;
	
	public FileMap(Path root) {
		super();
		this.root = root;
	}
	
	public Path getRoot() {
		return root;
	}
	
	protected Path toPath(String filename) {
		Matcher matcher = FILENAME_INVALID_CHAR.matcher(filename);
		return Path.of(matcher.replaceAll("_"));
	}
	
	abstract Path map(Key key) throws IOException;
	
	abstract Path map(Key key, String name) throws IOException;
	
	protected void updateManifest(Manifest manifest) {
		manifest.setString("fileMap", getClass().getName());
	}
	
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
