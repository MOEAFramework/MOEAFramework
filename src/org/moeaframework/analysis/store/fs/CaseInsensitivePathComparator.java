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

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Comparator for {@link Path} that performs case-insensitive comparisons of each path segment.
 */
class CaseInsensitivePathComparator implements Comparator<Path> {

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
		
		if (path1.getNameCount() < path2.getNameCount()) {
			return -1;
		} else if (path1.getNameCount() > path2.getNameCount()) {
			return 1;
		}
		
		return 0;
	}
	
}