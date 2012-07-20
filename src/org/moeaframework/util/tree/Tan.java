package org.moeaframework.util.tree;

public class Tan extends Node{
	
	public Tan() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Tan copyNode() {
		return new Tan();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.tan(
				(Number)getArgument(0).evaluate(environment));
	}

}
