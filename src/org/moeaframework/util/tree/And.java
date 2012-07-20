package org.moeaframework.util.tree;

public class And extends Node {
	
	public And() {
		super(Boolean.class, Boolean.class, Boolean.class);
	}

	@Override
	public And copyNode() {
		return new And();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return (Boolean)getArgument(0).evaluate(environment) &&
				(Boolean)getArgument(1).evaluate(environment);
	}

}
