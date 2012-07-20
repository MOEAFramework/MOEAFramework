package org.moeaframework.util.tree;

public class Sequence extends Node {
	
	public Sequence() {
		this(Object.class);
	}
	
	public Sequence(Class<?> type) {
		this(Object.class, type);
	}
	
	public Sequence(Class<?> type1, Class<?> type2) {
		super(type2, type1, type2);
	}
	
	public Sequence(Class<?>... types) {
		super(types[types.length-1], types);
	}

	@Override
	public Sequence copyNode() {
		return new Sequence(getArgumentTypes());
	}

	@Override
	public Object evaluate(Environment environment) {
		getArgument(0).evaluate(environment);
		return getArgument(1).evaluate(environment);
	}

}
