package org.moeaframework.util.tree;

public class Tanh extends Node{
	
	public Tanh() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Tanh copyNode() {
		return new Tanh();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.tanh(
				(Number)getArgument(0).evaluate(environment));
	}

}
