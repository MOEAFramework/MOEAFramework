package org.moeaframework.util.tree;

public class Xor extends Node {
	
	public Xor() {
		super(Boolean.class, Boolean.class, Boolean.class);
	}

	@Override
	public Xor copyNode() {
		return new Xor();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return (Boolean)getArgument(0).evaluate(environment) ^
				(Boolean)getArgument(1).evaluate(environment);
	}

}
