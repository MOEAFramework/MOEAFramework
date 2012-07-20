package org.moeaframework.util.tree;

public class While extends Node {

	public While() {
		super(Object.class, Boolean.class, Object.class);
	}

	@Override
	public While copyNode() {
		return new While();
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = null;
		
		while ((Boolean)getArgument(0).evaluate(environment)) {
			value = getArgument(2).evaluate(environment);
		}
		
		return value;
	}

}
