package org.moeaframework.util.tree;

public class IfElse extends Node {
	
	public IfElse() {
		this(Object.class);
	}
	
	public IfElse(Class<?> type) {
		super(type, Boolean.class, type, type);
	}

	@Override
	public IfElse copyNode() {
		return new IfElse(getReturnType());
	}

	@Override
	public Object evaluate(Environment environment) {
		Boolean condition = (Boolean)getArgument(0).evaluate(environment);
		
		if (condition) {
			return getArgument(1).evaluate(environment);
		} else {
			return getArgument(2).evaluate(environment);
		}
	}

}
