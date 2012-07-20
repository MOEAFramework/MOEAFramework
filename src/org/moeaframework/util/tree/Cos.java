package org.moeaframework.util.tree;

public class Cos extends Node{
	
	public Cos() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Cos copyNode() {
		return new Cos();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.cos(
				(Number)getArgument(0).evaluate(environment));
	}

}
