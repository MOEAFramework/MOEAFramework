package org.moeaframework.util.tree;

public class Exp extends Node{
	
	public Exp() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Exp copyNode() {
		return new Exp();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.exp(
				(Number)getArgument(0).evaluate(environment));
	}

}
