package org.moeaframework.experiment.parameter;

import org.moeaframework.experiment.Sample;

public class DecimalRange extends SampledParameter {
	
	private final double lowerBound;
	
	private final double upperBound;

	public DecimalRange(String name, double lowerBound, double upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}
	
	@Override
	public void apply(Sample sample, double d) {
		sample.setDouble(getName(), lowerBound + d * (upperBound - lowerBound));
	}
	
	@Override
	public String toString() {
		return getName() + ": (" + getLowerBound() + ", " + getUpperBound() + ")";
	}

}
