package org.moeaframework.experiment.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.validate.Validate;

public class SampledParameterSet extends ParameterSet<Parameter> {
		
	public SampledParameterSet(Schema schema) {
		super(schema);
	}
	
	public SampledParameterSet(Schema schema, Parameter... parameters) {
		super(schema, parameters);
	}
	
	public SampledParameterSet(Schema schema, Collection<Parameter> parameters) {
		super(schema, parameters);
	}

	public Samples generate(int numberOfSamples, Sequence sequence) {
		// Identify which parameters are sampled vs expanded.
		List<SampledParameter> sampledParameters = new ArrayList<>();
		List<EnumeratedParameter> expandedParameters = new ArrayList<>();
		
		for (Parameter parameter : parameters.values()) {
			if (parameter instanceof SampledParameter sampledParameter) {
				sampledParameters.add(sampledParameter);
			} else if (parameter instanceof EnumeratedParameter enumeratedParameter) {
				expandedParameters.add(enumeratedParameter);
			} else {
				Validate.that("parameter", parameter).fails("Unsupported parameter type " +
						parameter.getClass().getName());
			}
		}
		
		List<TypedProperties> result = new ArrayList<>();
		
		// Expand the sampled parameters using the provided sequence generator.
		if (sampledParameters.size() > 0) {
			double[][] samples = sequence.generate(numberOfSamples, sampledParameters.size());
			
			for (double[] sample : samples) {
				TypedProperties properties = new TypedProperties();
				
				for (int i = 0; i < sampledParameters.size(); i++) {
					sampledParameters.get(i).apply(properties, sample[i]);
				}
				
				result.add(properties);
			}
		} else {
			result.add(new TypedProperties());
		}
		
		// Expand the enumerated parameters.
		for (int i = 0; i < expandedParameters.size(); i++) {
			result = expandedParameters.get(i).enumerate(result);
		}

		return new Samples(schema, result);
	}

}
