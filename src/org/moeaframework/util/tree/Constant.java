package org.moeaframework.util.tree;

public class Constant extends Node {
	
	private final Object value;
	
	public Constant(double value) {
		this(Number.class, value);
	}
	
	public Constant(long value) {
		this(Number.class, value);
	}
	
	public Constant(boolean value) {
		this(Boolean.class, value);
	}
	
	public Constant(Class<?> returnType, Object value) {
		super(returnType);
		this.value = value;
	}
	
	@Override
	public Constant copyNode() {
		return new Constant(getReturnType(), value);
	}
	
	@Override
	public Object evaluate(Environment environment) {
		return value;
	}
	
	public String toString() {
		return String.valueOf(value);
	}

}
