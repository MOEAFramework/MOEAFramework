package org.moeaframework.util.tree;

public class LessThanOrEqual extends Node {
	
	public LessThanOrEqual() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public LessThanOrEqual copyNode() {
		return new LessThanOrEqual();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return NumberArithmetic.lessThanOrEqual(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}
	
	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
