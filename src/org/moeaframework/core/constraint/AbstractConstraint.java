package org.moeaframework.core.constraint;

public abstract class AbstractConstraint implements Constraint {

	private static final long serialVersionUID = -9233514055091031L;
	
	protected double value;
		
	public AbstractConstraint() {
		this(Double.NaN);
	}
	
	protected AbstractConstraint(double value) {
		super();
		this.value = value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public abstract double getMagnitudeOfViolation();
	
	public abstract Constraint copy();
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +  value + ")";
	}

}
