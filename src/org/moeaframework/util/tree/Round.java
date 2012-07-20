package org.moeaframework.util.tree;

public class Round extends Node{
	
	public Round() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Round copyNode() {
		return new Round();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.round(
				(Number)getArgument(0).evaluate(environment));
	}

}
