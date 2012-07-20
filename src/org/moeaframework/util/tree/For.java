package org.moeaframework.util.tree;

public class For extends Node {
	
	private final String variableName;
	
	public For(String variableName) {
		super(Object.class, Number.class, Number.class, Number.class, Object.class);
		this.variableName = variableName;
	}

	@Override
	public For copyNode() {
		return new For(variableName);
	}

	@Override
	public Object evaluate(Environment environment) {
		Number start = (Number)getArgument(0).evaluate(environment);
		Number end = (Number)getArgument(1).evaluate(environment);
		Number step = (Number)getArgument(2).evaluate(environment);
		Object value = null;
		
		for (Number i = start; NumberArithmetic.lessThan(i, end); i = NumberArithmetic.add(i, step)) {
			environment.set(variableName, i);
			value = getArgument(3).evaluate(environment);
		}
		
		return value;
	}

}
