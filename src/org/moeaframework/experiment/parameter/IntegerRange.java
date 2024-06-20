package org.moeaframework.experiment.parameter;

import org.moeaframework.experiment.Sample;

public class IntegerRange extends SampledParameter {
	
	private final int lowerBound;
	
	private final int upperBound;

	public IntegerRange(String name, int lowerBound, int upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}
	
	@Override
	public void apply(Sample sample, double d) {
		sample.setInt(getName(), (int)(lowerBound + d * (upperBound - lowerBound + Math.nextAfter(1.0, Double.NEGATIVE_INFINITY))));
	}
	
	@Override
	public String toString() {
		return getName() + ": (" + getLowerBound() + ", " + getUpperBound() + ")";
	}

}
