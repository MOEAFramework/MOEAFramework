package org.moeaframework.experiment.parameter;

import org.moeaframework.util.TypedProperties;

public class LongRange extends SampledParameter {
	
	private final long lowerBound;
	
	private final long upperBound;

	public LongRange(String name, long lowerBound, long upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}
	
	@Override
	public void apply(TypedProperties sample, double d) {
		sample.setLong(getName(), (long)(lowerBound + d * (upperBound - lowerBound + Math.nextAfter(1.0, Double.NEGATIVE_INFINITY))));
	}
	
	@Override
	public String toString() {
		return getName() + ": (" + getLowerBound() + ", " + getUpperBound() + ")";
	}

}
