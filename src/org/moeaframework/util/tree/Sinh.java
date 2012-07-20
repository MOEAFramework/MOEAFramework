package org.moeaframework.util.tree;

public class Sinh extends Node{
	
	public Sinh() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Sinh copyNode() {
		return new Sinh();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sinh(
				(Number)getArgument(0).evaluate(environment));
	}

}
