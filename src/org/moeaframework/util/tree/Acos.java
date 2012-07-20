package org.moeaframework.util.tree;

public class Acos extends Node{
	
	public Acos() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Acos copyNode() {
		return new Acos();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.acos(
				(Number)getArgument(0).evaluate(environment));
	}

}
