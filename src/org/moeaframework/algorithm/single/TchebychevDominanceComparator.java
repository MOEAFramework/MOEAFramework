package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;

public class TchebychevDominanceComparator extends ChainedComparator implements
Serializable {

	private static final long serialVersionUID = -1433954844334603655L;

	public TchebychevDominanceComparator() {
		super(new AggregateConstraintComparator(),
				new TchebychevObjectiveComparator());
	}
	
	public TchebychevDominanceComparator(double... weights) {
		super(new AggregateConstraintComparator(),
				new TchebychevObjectiveComparator(weights));
	}

}
