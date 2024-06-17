package org.moeaframework.experiment.parameter;

import java.util.List;

import org.moeaframework.util.TypedProperties;

public class Constant<T> extends EnumeratedParameter {
	
	private final T value;
	
	public Constant(String name, T value) {
		super(name);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public List<TypedProperties> enumerate(List<TypedProperties> samples) {
		for (TypedProperties sample : samples) {
			sample.setString(getName(), value.toString());
		}
		
		return samples;
	}
	
	@Override
	public String toString() {
		return getName() + ": " + getValue();
	}

}
