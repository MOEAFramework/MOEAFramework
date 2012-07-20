package org.moeaframework.util.tree;

public class Add extends Node{
	
	public Add() {
		super(Number.class, Number.class, Number.class);
	}
	
	@Override
	public Add copyNode() {
		return new Add();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.add(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
