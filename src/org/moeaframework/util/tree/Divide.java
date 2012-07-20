package org.moeaframework.util.tree;

public class Divide extends Node{
	
	public Divide() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Divide copyNode() {
		return new Divide();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.div(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
