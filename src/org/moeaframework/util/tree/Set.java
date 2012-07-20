package org.moeaframework.util.tree;

public class Set extends Node {
	
	private final String name;
	
	public Set(Class<?> type, String name) {
		super(type, type);
		this.name = name;
	}

	@Override
	public Set copyNode() {
		return new Set(getArgumentType(0), name);
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = getArgument(0).evaluate(environment);
		environment.set(name, value);
		return value;
	}

}
