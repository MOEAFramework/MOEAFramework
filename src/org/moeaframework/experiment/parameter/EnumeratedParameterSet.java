package org.moeaframework.experiment.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

public class EnumeratedParameterSet extends ParameterSet<EnumeratedParameter> {
	
	public EnumeratedParameterSet(Schema schema) {
		super(schema);
	}
	
	public EnumeratedParameterSet(Schema schema, EnumeratedParameter... parameters) {
		super(schema, parameters);
	}
	
	public EnumeratedParameterSet(Schema schema, Collection<EnumeratedParameter> parameters) {
		super(schema, parameters);
	}

	public Samples generate() {
		List<TypedProperties> result = new ArrayList<>();
		result.add(new TypedProperties());
		
		for (EnumeratedParameter parameter : parameters.values()) {
			result = parameter.enumerate(result);
		}

		return new Samples(schema, result);
	}

}
