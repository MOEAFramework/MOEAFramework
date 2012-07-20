package org.moeaframework.util.tree;

public class Power extends Node{
	
	public Power() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Power copyNode() {
		return new Power();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.pow(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
