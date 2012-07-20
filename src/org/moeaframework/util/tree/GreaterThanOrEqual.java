package org.moeaframework.util.tree;

public class GreaterThanOrEqual extends Node {
	
	public GreaterThanOrEqual() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public GreaterThanOrEqual copyNode() {
		return new GreaterThanOrEqual();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return NumberArithmetic.greaterThanOrEqual(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
