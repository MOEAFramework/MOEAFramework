package org.moeaframework.mock;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

public class MockDominanceComparator implements DominanceComparator {

	@Override
	public int compare(Solution solution1, Solution solution2) {
		return -1;
	}

}
