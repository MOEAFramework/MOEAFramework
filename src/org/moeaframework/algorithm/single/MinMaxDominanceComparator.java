package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;

public class MinMaxDominanceComparator extends ChainedComparator implements
SingleObjectiveComparator, Serializable {

	private static final long serialVersionUID = -1433954844334603655L;

	public MinMaxDominanceComparator() {
		super(new AggregateConstraintComparator(),
				new MinMaxObjectiveComparator());
	}
	
	public MinMaxDominanceComparator(double... weights) {
		super(new AggregateConstraintComparator(),
				new MinMaxObjectiveComparator(weights));
	}

}
