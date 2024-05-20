package org.moeaframework.core.attribute;

import org.moeaframework.core.Solution;

public final class OperatorIndex implements Attribute {
	
	public static final String ATTRIBUTE_NAME = "operator";

	private OperatorIndex() {
		super();
	}
	
	public static final boolean hasAttribute(Solution solution) {
		return solution.hasAttribute(ATTRIBUTE_NAME);
	}
	
	public static final void setAttribute(Solution solution, int value) {
		solution.setAttribute(ATTRIBUTE_NAME, value);
	}
	
	public static final int getAttribute(Solution solution) {
		return (Integer)solution.getAttribute(ATTRIBUTE_NAME);
	}
	
	public static final void removeAttribute(Solution solution) {
		solution.removeAttribute(ATTRIBUTE_NAME);
	}

}
