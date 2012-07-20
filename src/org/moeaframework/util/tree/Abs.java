package org.moeaframework.util.tree;

public class Abs extends Node{
	
	public Abs() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Abs copyNode() {
		return new Abs();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.abs(
				(Number)getArgument(0).evaluate(environment));
	}

}
