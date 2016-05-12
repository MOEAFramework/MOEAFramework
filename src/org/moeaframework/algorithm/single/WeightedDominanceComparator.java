package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;

public class WeightedDominanceComparator extends ChainedComparator implements
Serializable {

	private static final long serialVersionUID = -2110850728860429004L;

	public WeightedDominanceComparator() {
		super(new AggregateConstraintComparator(),
				new WeightedObjectiveComparator());
	}
	
	public WeightedDominanceComparator(double... weights) {
		super(new AggregateConstraintComparator(),
				new WeightedObjectiveComparator(weights));
	}

}
