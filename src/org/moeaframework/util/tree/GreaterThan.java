package org.moeaframework.util.tree;

public class GreaterThan extends Node {
	
	public GreaterThan() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public GreaterThan copyNode() {
		return new GreaterThan();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return NumberArithmetic.greaterThan(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
