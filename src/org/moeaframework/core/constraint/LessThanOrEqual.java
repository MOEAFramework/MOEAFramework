package org.moeaframework.core.constraint;

public class LessThanOrEqual extends ThresholdConstraint {

	private static final long serialVersionUID = -2950482083962039249L;

	public LessThanOrEqual(double threshold) {
		super(threshold);
	}
	
	LessThanOrEqual(double value, double threshold, double epsilon) {
		super(value, threshold, epsilon);
	}

	@Override
	public double getMagnitudeOfViolation() {
		double diff = Math.abs(value - threshold);
		return getValue() <= threshold || diff <= epsilon ? 0.0 : diff;
	}

	@Override
	public LessThanOrEqual copy() {
		return new LessThanOrEqual(value, threshold, epsilon);
	}
	
	public static LessThanOrEqual to(double threshold) {
		return new LessThanOrEqual(threshold);
	}

}
