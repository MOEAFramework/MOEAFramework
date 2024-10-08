package org.moeaframework.core.objective;

import org.moeaframework.core.FrameworkException;

public class Maximize extends AbstractObjective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	public Maximize() {
		super();
	}
	
	protected Maximize(double value) {
		super(value);
	}

	@Override
	public int compareTo(Objective other) {
		if (getClass() != other.getClass()) {
			throw new FrameworkException("unable to compare objective values between " + getClass().getSimpleName() +
					" and " + other.getClass().getSimpleName());
		}
		
		return -Double.compare(getValue(), other.getValue());
	}

	@Override
	public Maximize copy() {
		return new Maximize(getValue());
	}
	
}
