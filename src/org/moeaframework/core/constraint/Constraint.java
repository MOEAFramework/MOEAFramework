package org.moeaframework.core.constraint;

import java.io.Serializable;

public abstract class Constraint implements Comparable<Constraint>, Serializable {

	private static final long serialVersionUID = -9233514055091031L;
	
	/**
	 * Constant used to indicate a constraint is satisfied.
	 */
	public static final double SATISFIED = 0.0;
	
	protected double value;
		
	public Constraint() {
		this(Double.NaN);
	}
	
	protected Constraint(double value) {
		super();
		this.value = value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public int compareTo(Constraint other) {
		return Double.compare(getMagnitudeOfViolation(), other.getMagnitudeOfViolation());
	}

	public boolean isViolation() {
		return getMagnitudeOfViolation() != SATISFIED;
	}
	
	public abstract double getMagnitudeOfViolation();
	
	public abstract Constraint copy();

}
