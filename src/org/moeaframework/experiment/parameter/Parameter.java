package org.moeaframework.experiment.parameter;

public abstract class Parameter {
	
	private final String name;
	
	public Parameter(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static ParameterBuilder named(String name) {
		return new ParameterBuilder(name);
	}
	
}
