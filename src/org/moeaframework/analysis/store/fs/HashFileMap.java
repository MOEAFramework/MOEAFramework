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
package org.moeaframework.analysis.store.fs;

import java.io.IOException;
import java.nio.file.Path;

import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.Defined;

/**
 * File map that hashes the reference to create a flat folder structure.  An optional prefix is used at the top-level
 * to help organize the containers.
 */
public class HashFileMap extends FileMap {

	private final int prefixLength;
	
	/**
	 * Constructs a hash file map with the given prefix length.
	 * 
	 * @param prefixLength if {@code > 0}, the length of the prefix; if {@code 0}, no prefix folder is created
	 */
	public HashFileMap(int prefixLength) {
		super();
		this.prefixLength = prefixLength;
	}
	
	@Override
	public Path mapContainer(Schema schema, Path root, Reference reference) throws IOException {
		return map(root, Hash.of(schema, reference));
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
		return Defined.createDefinition(FileMap.class, getClass(), prefixLength);
	}

}
