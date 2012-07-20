package org.moeaframework.util.tree;

public class Min extends Node{
	
	public Min() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Min copyNode() {
		return new Min();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.min(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
