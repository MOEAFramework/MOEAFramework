package org.moeaframework.util.tree;

public class Subtract extends Node{
	
	public Subtract() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Subtract copyNode() {
		return new Subtract();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sub(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
