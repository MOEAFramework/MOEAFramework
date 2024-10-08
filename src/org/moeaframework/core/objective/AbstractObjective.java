package org.moeaframework.core.objective;

public abstract class AbstractObjective implements Objective {
	
	private static final long serialVersionUID = 8819865234325786924L;
	
	protected double value;
	
	public AbstractObjective() {
		this(Double.NaN);
	}
	
	protected AbstractObjective(double value) {
		super();
		this.value = value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public abstract Objective copy();
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +  value + ")";
	}
	
}
