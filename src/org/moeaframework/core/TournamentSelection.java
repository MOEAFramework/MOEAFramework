package org.moeaframework.core;

import org.moeaframework.core.comparator.DominanceComparator;

/**
 * This file has been moved to the selection package.  Please update your imports accordingly.
 * 
 * @deprecated Moved to {@link org.moeaframework.core.selection.TournamentSelection}
 */
@Deprecated
public class TournamentSelection extends org.moeaframework.core.selection.TournamentSelection {

	public TournamentSelection() {
		super();
	}

	public TournamentSelection(DominanceComparator comparator) {
		super(comparator);
	}

	public TournamentSelection(int size, DominanceComparator comparator) {
		super(size, comparator);
	}

	public TournamentSelection(int size) {
		super(size);
	}

}
