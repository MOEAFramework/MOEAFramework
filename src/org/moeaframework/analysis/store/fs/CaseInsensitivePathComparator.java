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