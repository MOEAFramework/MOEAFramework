package org.moeaframework.experiment.store.schema;

public class FieldBuilder {
	
	private final String name;
	
	FieldBuilder(String name) {
		super();
		this.name = name;
	}
	
	public Field<Integer> asInt() {
		return new Field<>(name, Integer.class, sample -> sample.getInt(name));
	}
	
	public Field<Long> asLong() {
		return new Field<>(name, Long.class, sample -> sample.getLong(name));
	}
	
	public Field<Double> asDecimal() {
		return new Field<>(name, Double.class, sample -> sample.getDouble(name));
	}
	
	public Field<String> asString() {
		return new Field<>(name, String.class, sample -> sample.getString(name));
	}

}
