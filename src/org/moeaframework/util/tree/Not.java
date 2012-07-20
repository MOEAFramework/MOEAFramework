package org.moeaframework.util.tree;

public class Not extends Node {

	public Not() {
		super(Boolean.class, Boolean.class);
	}
	
	@Override
	public Not copyNode() {
		return new Not();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return !(Boolean)getArgument(0).evaluate(environment);
	}

}
