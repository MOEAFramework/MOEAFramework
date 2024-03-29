package org.moeaframework.core.objective;

public class Maximize extends Objective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	public Maximize() {
		super();
	}
	
	protected Maximize(double value) {
		super(value);
	}

	@Override
	public int compareTo(Objective other) {
		if (other instanceof Maximize) {
			return -Double.compare(getValue(), other.getValue());
		} else {
			throw new IllegalArgumentException(getClass().getSimpleName() + " and " + other.getClass().getSimpleName() +
					" are not comparable");
		}
	}

	@Override
	public Maximize copy() {
		return new Maximize(getValue());
	}
	
}
