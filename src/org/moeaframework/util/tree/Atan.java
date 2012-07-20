package org.moeaframework.util.tree;

public class Atan extends Node{
	
	public Atan() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Atan copyNode() {
		return new Atan();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.atan(
				(Number)getArgument(0).evaluate(environment));
	}

}
