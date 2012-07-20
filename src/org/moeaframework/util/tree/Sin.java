package org.moeaframework.util.tree;

public class Sin extends Node{
	
	public Sin() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Sin copyNode() {
		return new Sin();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sin(
				(Number)getArgument(0).evaluate(environment));
	}

}
