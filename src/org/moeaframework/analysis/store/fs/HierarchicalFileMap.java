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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.moeaframework.analysis.store.Key;
import org.moeaframework.core.Constructable;

/**
 * Stores files in a hierarchical folder structure, alternating between the index name and the associated index value.
 */
public class HierarchicalFileMap extends FileMap {
				
	public HierarchicalFileMap() {
		super();
	}
	
	@Override
	public Path map(Path root, Key key) throws IOException {
		Path path = root;
		
		// convert key to valid file names
		Map<Path, Path> remaining = new TreeMap<>(FileMap.CASE_INSENSITIVE_ORDER);
		
		for (String index : key.indices()) {
			remaining.put(toPath(index), toPath(key.get(index).toString()));
		}
		
		// walk directory structure, reusing any existing folders
		while (!remaining.isEmpty()) {
			if (!Files.exists(path)) {
				break;
			}
			
			Optional<Path> match = Files.walk(path)
					.filter(Files::isDirectory)
					.filter(x -> remaining.containsKey(x.getFileName()))
					.findFirst();
				
			if (!match.isPresent()) {
				break;
			}
			
			Path matchingPath = match.get().getFileName();
			path = path.resolve(matchingPath).resolve(remaining.remove(matchingPath));
		}
		
		// if no exact match, extend directory structure with remaining indices
		for (Path remainingPath : remaining.keySet()) {
			path = path.resolve(remainingPath).resolve(remaining.get(remainingPath));
		}
			
		return path;
	}
	
	@Override
	public Path map(Path root, Key key, String name) throws IOException {
		Path path = map(root, key);
		return path.resolve(toPath(name));
	}
	
	@Override
	public String getDefinition() {
		return Constructable.createDefinition(FileMap.class, getClass());
	}

}
