package org.moeaframework.experiment.parameter;

import org.moeaframework.experiment.Sample;

/**
 * Sampled parameters specify a range of possible values that are sampled randomly or according to some sequence.
 */
public abstract class SampledParameter extends Parameter {

	public SampledParameter(String name) {
		super(name);
	}
	
	public abstract void apply(Sample sample, double d);

}
