package org.moeaframework.core.constraint;

import java.io.Serializable;

import org.moeaframework.core.FrameworkException;

public interface Constraint extends Comparable<Constraint>, Serializable {

	/**
	 * Constant used to indicate a constraint is satisfied.
	 */
	public static final double SATISFIED = 0.0;
		
	public double getValue();
	
	public void setValue(double value);
	
	/**
	 * Returns the magnitude of constraint violation as a non-negative number, with {@value #SATISFIED} representing
	 * satisfied or feasible constraints.  When comparing two constraints, smaller magnitudes are considered better.
	 * 
	 * @return the magnitude of constraint violation
	 */
	public double getMagnitudeOfViolation();
	
	public Constraint copy();
	
	public default boolean isViolation() {
		return getMagnitudeOfViolation() != SATISFIED;
	}

	@Override
	public default int compareTo(Constraint other) {
		if (getClass() != other.getClass()) {
			throw new FrameworkException("unable to compare constraint values between " + getClass().getSimpleName() +
					" and " + other.getClass().getSimpleName());
		}
		
		return Double.compare(getMagnitudeOfViolation(), other.getMagnitudeOfViolation());
	}
}
