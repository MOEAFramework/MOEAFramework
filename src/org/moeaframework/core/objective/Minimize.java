package org.moeaframework.core.objective;

public class Minimize extends Objective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	public Minimize() {
		super();
	}
	
	protected Minimize(double value) {
		super(value);
	}

	@Override
	public int compareTo(Objective other) {
		return Double.compare(getValue(), other.getValue());
	}

	@Override
	public Minimize copy() {
		return new Minimize(getValue());
	}
	
}
