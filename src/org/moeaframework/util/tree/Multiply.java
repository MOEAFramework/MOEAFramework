package org.moeaframework.util.tree;

public class Multiply extends Node {
	
	public Multiply() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Multiply copyNode() {
		return new Multiply();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.mul(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}
	
}
