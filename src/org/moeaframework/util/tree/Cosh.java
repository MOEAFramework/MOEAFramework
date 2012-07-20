package org.moeaframework.util.tree;

public class Cosh extends Node{
	
	public Cosh() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Cosh copyNode() {
		return new Cosh();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.cosh(
				(Number)getArgument(0).evaluate(environment));
	}

}
