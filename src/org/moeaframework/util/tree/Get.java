package org.moeaframework.util.tree;

public class Get extends Node {
	
	private final String name;
	
	public Get(Class<?> type, String name) {
		super(type);
		this.name = name;
	}

	@Override
	public Get copyNode() {
		return new Get(getReturnType(), name);
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = environment.get(getReturnType(), name);
		
		if (value == null) {
			if (getReturnType().equals(Float.class) || 
					getReturnType().equals(Double.class)) {
				value = 0.0;
			} else {
				value = 0;
			}
		}
		
		return value;
	}
	
	public Object getDefaultValue() {
		if (getReturnType().equals(Byte.class) ||
				getReturnType().equals(Short.class) ||
				getReturnType().equals(Integer.class) || 
				getReturnType().equals(Long.class)) {
			return 0;
		} else if (getReturnType().equals(Float.class) ||
				getReturnType().equals(Double.class)) {
			return 0.0;
		} else if (getReturnType().equals(Boolean.class)) {
			return false;
		} else {
			return null;
		}
	}
	
	public String toString() {
		return name;
	}

}
