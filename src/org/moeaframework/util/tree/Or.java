package org.moeaframework.util.tree;

public class Or extends Node {
	
	public Or() {
		super(Boolean.class, Boolean.class, Boolean.class);
	}

	@Override
	public Or copyNode() {
		return new Or();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return (Boolean)getArgument(0).evaluate(environment) ||
				(Boolean)getArgument(1).evaluate(environment);
	}

}
