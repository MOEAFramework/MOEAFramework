package org.moeaframework.util.tree;

public class Max extends Node{
	
	public Max() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Max copyNode() {
		return new Max();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.max(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
