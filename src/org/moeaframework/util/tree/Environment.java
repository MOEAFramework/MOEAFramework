package org.moeaframework.util.tree;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	
	private Environment parent;
	
	private Map<String, Object> memory;
	
	public Environment() {
		this(null);
	}
	
	public Environment(Environment parent) {
		super();
		this.parent = parent;
		
		memory = new HashMap<String, Object>();
	}
	
	public <T> T get(Class<T> type, String name) {
		Object value = memory.get(name);
		
		if ((value == null) && (parent != null)) {
			value = parent.get(type, name);
		}
		
		if (value == null) {
			return null;
		} else {
			return type.cast(value);
		}
	}
	
	public void set(String name, Object value) {
		memory.put(name, value);
	}

}
