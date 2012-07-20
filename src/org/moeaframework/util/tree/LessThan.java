package org.moeaframework.util.tree;

public class LessThan extends Node {
	
	public LessThan() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public LessThan copyNode() {
		return new LessThan();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return NumberArithmetic.lessThan(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}
	
	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
