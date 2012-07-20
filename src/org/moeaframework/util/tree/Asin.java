package org.moeaframework.util.tree;

public class Asin extends Node{
	
	public Asin() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Asin copyNode() {
		return new Asin();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.asin(
				(Number)getArgument(0).evaluate(environment));
	}

}
