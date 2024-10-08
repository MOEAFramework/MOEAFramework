package org.moeaframework.core.constraint;

import org.moeaframework.core.Settings;

public abstract class ThresholdConstraint extends AbstractConstraint {

	private static final long serialVersionUID = -3566414877502834332L;

	protected final double threshold;
	
	protected final double epsilon;
		
	public ThresholdConstraint(double threshold) {
		this(Double.NaN, threshold, Settings.EPS);
	}
	
	ThresholdConstraint(double value, double threshold, double epsilon) {
		super(value);
		this.threshold = threshold;
		this.epsilon = epsilon;
	}

}
