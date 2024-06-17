package org.moeaframework.experiment.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.util.TypedProperties;

public class Enumeration<T> extends EnumeratedParameter {
	
	private List<T> values;
	
	@SafeVarargs
	public Enumeration(String name, T... values) {
		this(name, List.of(values));
	}

	public Enumeration(String name, List<T> values) {
		super(name);
		this.values = values;
	}
	
	public List<T> getValues() {
		return values;
	}

	@Override
	public List<TypedProperties> enumerate(List<TypedProperties> samples) {
		List<TypedProperties> result = new ArrayList<TypedProperties>();
		
		for (TypedProperties sample : samples) {
			for (T value : values) {
				TypedProperties newSample = new TypedProperties();
				newSample.addAll(sample);
				newSample.setString(getName(), value.toString());
				result.add(newSample);
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(": [");
		sb.append(values.stream().map(v -> v.toString()).collect(Collectors.joining(",")));
		sb.append("]");
		return sb.toString();
	}

}
