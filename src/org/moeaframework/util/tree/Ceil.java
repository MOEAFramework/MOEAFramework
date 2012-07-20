package org.moeaframework.util.tree;

public class Ceil extends Node{
	
	public Ceil() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Ceil copyNode() {
		return new Ceil();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.ceil(
				(Number)getArgument(0).evaluate(environment));
	}

}
