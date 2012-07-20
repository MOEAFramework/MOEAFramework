package org.moeaframework.util.tree;

public class Sign extends Node{
	
	public Sign() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Sign copyNode() {
		return new Sign();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sign(
				(Number)getArgument(0).evaluate(environment));
	}

}
