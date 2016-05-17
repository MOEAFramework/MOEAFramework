package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;

public class LinearDominanceComparator extends ChainedComparator implements
SingleObjectiveComparator, Serializable {

	private static final long serialVersionUID = -2110850728860429004L;

	public LinearDominanceComparator() {
		super(new AggregateConstraintComparator(),
				new LinearObjectiveComparator());
	}
	
	public LinearDominanceComparator(double... weights) {
		super(new AggregateConstraintComparator(),
				new LinearObjectiveComparator(weights));
	}

}
