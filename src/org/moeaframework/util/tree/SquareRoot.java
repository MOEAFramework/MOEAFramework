package org.moeaframework.util.tree;

public class SquareRoot extends Node{
	
	public SquareRoot() {
		super(Number.class, Number.class);
	}
	
	@Override
	public SquareRoot copyNode() {
		return new SquareRoot();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sqrt(
				(Number)getArgument(0).evaluate(environment));
	}

}
