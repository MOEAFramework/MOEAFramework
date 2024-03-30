package org.moeaframework.core.objective;

import java.io.Serializable;

public abstract class Objective implements Comparable<Objective>, Serializable {
	
	private static final long serialVersionUID = 8819865234325786924L;
	
	protected double value;
	
	public Objective() {
		this(Double.NaN);
	}
	
	protected Objective(double value) {
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
	
}
