package org.moeaframework.util.tree;

public class Square extends Node{
	
	public Square() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Square copyNode() {
		return new Square();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.pow(
				(Number)getArgument(0).evaluate(environment),
				2.0);
	}

}
