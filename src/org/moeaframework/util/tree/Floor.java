package org.moeaframework.util.tree;

public class Floor extends Node{
	
	public Floor() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Floor copyNode() {
		return new Floor();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.floor(
				(Number)getArgument(0).evaluate(environment));
	}

}
