package org.moeaframework.algorithm.single;

import java.util.Comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

public interface SingleObjectiveComparator extends DominanceComparator, Comparator<Solution> {

}
