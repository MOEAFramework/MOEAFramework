package org.moeaframework.experiment.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.experiment.Sample;

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
	public List<Sample> enumerate(List<Sample> samples) {
		List<Sample> result = new ArrayList<Sample>();
		
		for (Sample sample : samples) {
			for (T value : values) {
				Sample newSample = new Sample();
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
