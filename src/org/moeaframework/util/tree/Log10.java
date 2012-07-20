package org.moeaframework.util.tree;

public class Log10 extends Node{
	
	public Log10() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Log10 copyNode() {
		return new Log10();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.log10(
				(Number)getArgument(0).evaluate(environment));
	}

}
