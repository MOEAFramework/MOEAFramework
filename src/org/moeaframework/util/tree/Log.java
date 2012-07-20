package org.moeaframework.util.tree;

public class Log extends Node{
	
	public Log() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Log copyNode() {
		return new Log();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.log(
				(Number)getArgument(0).evaluate(environment));
	}

}
