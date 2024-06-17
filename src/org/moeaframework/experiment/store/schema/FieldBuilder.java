package org.moeaframework.experiment.store.schema;

public class FieldBuilder {
	
	private final String name;
	
	FieldBuilder(String name) {
		super();
		this.name = name;
	}
	
	public Field<Integer> asInt() {
		return new Field<>(name, Integer.class, Integer::valueOf);
	}
	
	public Field<Long> asLong() {
		return new Field<>(name, Long.class, Long::valueOf);
	}
	
	public Field<Double> asDecimal() {
		return new Field<>(name, Double.class, Double::valueOf);
	}
	
	public Field<String> asString() {
		return new Field<>(name, String.class, String::valueOf);
	}

}
