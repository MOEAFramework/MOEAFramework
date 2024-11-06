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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Field;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.Constructable;

/**
 * Stores files in a hierarchical folder structure, alternating between the index name and the associated index value.
 * When given a {@link Schema}, the structure defined by the schema is used.  However, if schemaless, this reuses
 * the existing folder hierarchy.
 * <p>
 * Paths are treated in a case-insensitive manner, even on platforms with case-sensitive file systems.
 */
public class HierarchicalFileMap extends FileMap {
	
	private static final Comparator<Path> CASE_INSENSITIVE_ORDER = new CaseInsensitivePathComparator();
	
	/**
	 * Constructs a new hierarchical file map.
	 */
	public HierarchicalFileMap() {
		super();
	}
	
	// Implementation Note: Files.walk(path, 1) includes path as the first visited file, so we call skip(1) to only
	// include the path contents.
	
	@Override
	public Path mapContainer(Schema schema, Path root, Reference reference) throws IOException {
		Path path = root;
		List<Pair<Field<?>, String>> resolvedPath = schema.resolve(reference);
		Map<Path, Path> remainingPaths = new TreeMap<>(CASE_INSENSITIVE_ORDER);

		for (Pair<Field<?>, String> entry : resolvedPath) {
			Path escapedKey = escapePath(entry.getKey().getName());
			Path escapedValue = escapePath(entry.getValue());
			
			remainingPaths.put(escapedKey, escapedValue);
		}
		
		// When schemaless, match any existing folder structure
		if (schema.isSchemaless()) {
			while (Files.exists(path) && !remainingPaths.isEmpty()) {
				Optional<Path> matchingKey = Files.walk(path, 1)
						.skip(1)
						.filter(Files::isDirectory)
						.map(x -> x.getFileName())
						.filter(x -> remainingPaths.containsKey(x))
						.findFirst();
					
				if (!matchingKey.isPresent()) {
					break;
				}
				
				Path key = matchingKey.get();
				Path value = remainingPaths.remove(key);
				path = path.resolve(key);
				
				Optional<Path> matchingValue = Files.walk(path, 1)
						.skip(1)
						.filter(Files::isDirectory)
						.map(x -> x.getFileName())
						.filter(x -> CASE_INSENSITIVE_ORDER.compare(x, value) == 0)
						.findFirst();
				
				path = path.resolve(matchingValue.orElse(value));
			}
		}
		
		// Remaining folder structure must match order
		for (Pair<Field<?>, String> entry : resolvedPath) {
			Path escapedKey = escapePath(entry.getKey().getName());
			Path escapedValue = escapePath(entry.getValue());
			
			if (remainingPaths.containsKey(escapedKey)) {
				path = path.resolve(escapedKey).resolve(escapedValue);
			}
		}
			
		return path;
	}
	
	@Override
	public Path mapBlob(Schema schema, Path root, Reference reference, String name) throws IOException {
		Path escapedName = escapePath(name);
		Path containerPath = mapContainer(schema, root, reference);
		Optional<Path> matchingFile = Optional.empty();
		
		if (Files.exists(containerPath)) {
			matchingFile = Files.walk(containerPath, 1)
					.skip(1)
					.map(x -> x.getFileName())
					.filter(x -> CASE_INSENSITIVE_ORDER.compare(x, escapedName) == 0)
					.findFirst();
		}
		
		return containerPath.resolve(matchingFile.orElse(escapedName));
	}
	
	@Override
	public String getDefinition() {
		return Constructable.createDefinition(FileMap.class, getClass());
	}

}
