package org.moeaframework.util.tree;

public class Equals extends Node {
	
	public Equals() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public Equals copyNode() {
		return new Equals();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		Number n1 = (Number)getArgument(0).evaluate(environment);
		Number n2 = (Number)getArgument(1).evaluate(environment);
		
		return n1.equals(n2);
	}

}
