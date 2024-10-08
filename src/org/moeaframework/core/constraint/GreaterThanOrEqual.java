package org.moeaframework.core.constraint;

public class GreaterThanOrEqual extends ThresholdConstraint {

	private static final long serialVersionUID = 132924592516674872L;

	public GreaterThanOrEqual(double threshold) {
		super(threshold);
	}
	
	GreaterThanOrEqual(double value, double threshold, double epsilon) {
		super(value, threshold, epsilon);
	}

	@Override
	public double getMagnitudeOfViolation() {
		double diff = Math.abs(value - threshold);
		return value >= threshold || diff <= epsilon ? 0.0 : -diff;
	}

	@Override
	public GreaterThanOrEqual copy() {
		return new GreaterThanOrEqual(value, threshold, epsilon);
	}
	
	public static GreaterThanOrEqual to(double threshold) {
		return new GreaterThanOrEqual(threshold);
	}

}
