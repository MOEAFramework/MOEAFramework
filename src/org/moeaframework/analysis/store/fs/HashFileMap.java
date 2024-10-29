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

import org.moeaframework.analysis.store.Key;
import org.moeaframework.core.Constructable;

public class HashFileMap extends FileMap {

	private final int prefixLength;
		
	public HashFileMap(int prefixLength) {
		super();
		this.prefixLength = prefixLength;
	}
	
	@Override
	public Path map(Path root, Key key) throws IOException {
		return map(root, Hash.of(key));
	}
	
	@Override
	public Path map(Path root, Key key, String name) throws IOException {
		return map(root, Hash.of(key, name));
	}
	
	private Path map(Path root, Hash hash) {
		String hexString = hash.toString();
		Path path = root;
		
		if (prefixLength > 0) {
			String prefix = hexString.substring(0, prefixLength);
			path = path.resolve(prefix);
		}
				
		return path.resolve(hexString);
	}
	
	@Override
	public String getDefinition() {
		return Constructable.createDefinition(FileMap.class, getClass(), prefixLength);
	}

}
