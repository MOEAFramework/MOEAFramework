package org.moeaframework.experiment.parameter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.moeaframework.experiment.store.schema.Schema;

public abstract class ParameterSet<T extends Parameter> implements Iterable<T> {
	
	protected final Schema schema;
	
	protected final TreeMap<String, T> parameters;
	
	public ParameterSet(Schema schema) {
		super();
		this.schema = schema;
		this.parameters = new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);
	}
	
	@SafeVarargs
	public ParameterSet(Schema schema, T... parameters) {
		this(schema, List.of(parameters));
	}
	
	public ParameterSet(Schema schema, Collection<T> parameters) {
		this(schema);
		
		for (T parameter : parameters) {
			add(parameter);
		}
	}
	
	public Schema getSchema() {
		return schema;
	}
	
	public void add(T parameter) {
		if (!schema.isSchemaless() && !schema.isDefined(parameter.getName()) && !(parameter instanceof Constant)) {
			throw new IllegalArgumentException("Parameter '" + parameter +
					"' is not defined in the schema and is not constant");
		}
		
		parameters.put(parameter.getName(), parameter);
	}
	
	public T get(String name) {
		return parameters.get(name);
	}

	@Override
	public Iterator<T> iterator() {
		return parameters.values().iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Parameter parameter : parameters.values()) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			
			sb.append(parameter.toString());
		}
		
		return sb.toString();
	}

}
