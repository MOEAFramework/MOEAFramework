package org.moeaframework.experiment.parameter;

import java.util.List;

import org.moeaframework.util.TypedProperties;

/**
 * Enumerated parameters have a fixed number of values that will all be present in the result.  This is akin to a
 * cross-join.
 *
 * @param <T> the type of the parameter
 */
public abstract class EnumeratedParameter extends Parameter {
	
	public EnumeratedParameter(String name) {
		super(name);
	}
	
	public abstract List<TypedProperties> enumerate(List<TypedProperties> samples);

}
