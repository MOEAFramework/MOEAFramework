package org.moeaframework.core.objective;

import org.moeaframework.core.FrameworkException;

public class Minimize extends AbstractObjective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	public Minimize() {
		super();
	}
	
	protected Minimize(double value) {
		super(value);
	}

	@Override
	public int compareTo(Objective other) {
		if (getClass() != other.getClass()) {
			throw new FrameworkException("unable to compare objective values between " + getClass().getSimpleName() +
					" and " + other.getClass().getSimpleName());
		}
		
		return Double.compare(getValue(), other.getValue());
	}

	@Override
	public Minimize copy() {
		return new Minimize(getValue());
	}

}
