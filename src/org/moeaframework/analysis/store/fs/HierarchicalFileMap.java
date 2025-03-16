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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.Defined;

/**
 * Stores files in a hierarchical folder structure, alternating between the reference key and the associated value.
 * Keys are ordered, though any existing folder structure is reused.
 * <p>
 * Paths are treated in a case-insensitive manner, even on platforms with case-sensitive file systems.
 */
public class HierarchicalFileMap extends FileMap {

	/**
	 * Constructs a new hierarchical file map.
	 */
	public HierarchicalFileMap() {
		super();
	}

	@Override
	public Path mapContainer(Path root, Reference reference) throws IOException {
		Path path = root;

		if (reference.isRoot()) {
			return path;
		}

		Map<Path, Path> escapedPathSegments = new TreeMap<>(CASE_INSENSITIVE_ORDER);

		for (String field : reference.fields()) {
			Path escapedKey = escapePath(field);
			Path escapedValue = escapePath(reference.get(field));

			escapedPathSegments.put(escapedKey, escapedValue);
		}

		// Match any existing folder structure, though note the same key could appear at multiple locations / depths
		// within the hierarchy.
		HierarchicalFileVisitor visitor = new HierarchicalFileVisitor(root, escapedPathSegments);
		Files.walkFileTree(path, visitor);

		Optional<Path> longestPath = visitor.getMatches().stream()
				.sorted((x, y) -> -Integer.compare(x.getNameCount(), y.getNameCount()))
				.findFirst();

		if (longestPath.isPresent()) {
			path = longestPath.get();

			// If the longest path ended on a key, append the value.  Also remove all keys from path segments.
			Path relativePath = root.relativize(path);

			if (relativePath.getNameCount() % 2 == 1) {
				path = path.resolve(escapedPathSegments.get(relativePath.getFileName()));
			}

			for (int i = 0; i < relativePath.getNameCount(); i += 2) {
				escapedPathSegments.remove(relativePath.getName(i));
			}
		}

		// Remaining folder structure must match order
		for (Map.Entry<Path, Path> entry : escapedPathSegments.entrySet()) {
			path = path.resolve(entry.getKey()).resolve(entry.getValue());
		}

		return path;
	}

	@Override
	public String getDefinition() {
		return Defined.createDefinition(FileMap.class, getClass());
	}

	/**
	 * File visitor that collects all paths that match the path segments.
	 */
	private static class HierarchicalFileVisitor extends SimpleFileVisitor<Path> {

		private final Path root;

		private final Map<Path, Path> escapedPathSegments;

		private final List<Path> matches;

		public HierarchicalFileVisitor(Path root, Map<Path, Path> escapedPathSegments) {
			super();
			this.root = root;
			this.escapedPathSegments = escapedPathSegments;

			matches = new ArrayList<>();
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if (dir.equals(root)) {
				return FileVisitResult.CONTINUE;
			}

			Path relativePath = root.relativize(dir);

			if (relativePath.getNameCount() % 2 == 1) {
				if (!escapedPathSegments.containsKey(relativePath.getFileName())) {
					return FileVisitResult.SKIP_SUBTREE;
				}
			} else {
				Path keyPath = relativePath.getName(relativePath.getNameCount()-2);

				if (!escapedPathSegments.containsKey(keyPath) ||
						CASE_INSENSITIVE_ORDER.compare(escapedPathSegments.get(keyPath), relativePath.getFileName()) != 0) {
					return FileVisitResult.SKIP_SUBTREE;
				}
			}

			matches.add(dir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.SKIP_SIBLINGS;
		}

		public List<Path> getMatches() {
			return matches;
		}

	}

}
