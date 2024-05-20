package org.moeaframework.core.attribute;

import org.moeaframework.core.Solution;

public final class NicheDistance implements Attribute {
	
	public static final String ATTRIBUTE_NAME = "nicheDistance";
	
	private NicheDistance() {
		super();
	}
	
	public static final boolean hasAttribute(Solution solution) {
		return solution.hasAttribute(ATTRIBUTE_NAME);
	}
	
	public static final void setAttribute(Solution solution, double value) {
		solution.setAttribute(ATTRIBUTE_NAME, value);
	}
	
	public static final double getAttribute(Solution solution) {
		return (Double)solution.getAttribute(ATTRIBUTE_NAME);
	}

}
