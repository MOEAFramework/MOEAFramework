package org.moeaframework.util.tree;

public class Modulus extends Node{
	
	public Modulus() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Modulus copyNode() {
		return new Modulus();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.mod(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
