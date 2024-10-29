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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.moeaframework.analysis.store.Hash;
import org.moeaframework.analysis.store.Key;

public class HashFileMap extends FileMap {

	private final int prefixLength;
		
	protected HashFileMap(Path root, int prefixLength) {
		super(root);
		this.prefixLength = prefixLength;
	}
	
	@Override
	public Path map(Key key) throws IOException {
		return map(Hash.of(key));
	}
	
	@Override
	public Path map(Key key, String name) throws IOException {
		return map(Hash.of(key, name));
	}
	
	private Path map(Hash hash) {
		String hexString = hash.toString();
		Path path = getRoot();
		
		if (prefixLength > 0) {
			String prefix = hexString.substring(0, prefixLength);
			path = path.resolve(prefix);
		}
				
		return path.resolve(hexString);
	}
	
	public static HashFileMap at(File root) {
		return at(root.toPath());
	}
	
	public static HashFileMap at(File root, int prefixLength) {
		return at(root.toPath(), prefixLength);
	}
	
	public static HashFileMap at(Path root) {
		return at(root, 2);
	}
	
	public static HashFileMap at(Path root, int prefixLength) {
		return new HashFileMap(root, prefixLength);
	}
	
	@Override
	protected void updateManifest(Manifest manifest) {
		super.updateManifest(manifest);
		manifest.setInt("prefixLength", prefixLength);
	}

}
