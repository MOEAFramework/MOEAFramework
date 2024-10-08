package org.moeaframework.core.constraint;

public class Equal extends ThresholdConstraint {

	private static final long serialVersionUID = -2766984574651872793L;

	public Equal(double threshold) {
		super(threshold);
	}
	
	Equal(double value, double threshold, double epsilon) {
		super(value, threshold, epsilon);
	}

	@Override
	public double getMagnitudeOfViolation() {
		double diff = Math.abs(value - threshold);
		return diff <= epsilon ? 0.0 : diff;
	}

	@Override
	public Equal copy() {
		return new Equal(value, threshold, epsilon);
	}
	
	public static Equal to(double threshold) {
		return new Equal(threshold);
	}

}
