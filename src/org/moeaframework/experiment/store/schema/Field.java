package org.moeaframework.experiment.store.schema;

import java.io.Serializable;
import java.util.function.Function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Field<T extends Comparable<T> & Serializable> {
	
	public static final Field<String> ALGORITHM = Field.named("algorithm").asString();
	
	public static final Field<String> PROBLEM = Field.named("problem").asString();
	
	public static final Field<Integer> SEED = Field.named("seed").asInt();
	
	private final String name;
	
	private final Class<T> type;
	
	private final Function<String, T> valueOf;
	
	Field(String name, Class<T> type, Function<String, T> valueOf) {
		super();
		this.name = name;
		this.type = type;
		this.valueOf = valueOf;
	}
	
	public T cast(Object object) {
		return type.cast(object);
	}
	
	public T valueOf(String string) {
		return valueOf.apply(string);
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "{" + name + "=" + type.getName() + "}";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(name)
				.append(type)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		Field<?> rhs = (Field<?>)obj;
		return new EqualsBuilder()
				.append(name, rhs.name)
				.append(type, rhs.type)
				.isEquals();
	}
	
	public static FieldBuilder named(String name) {
		return new FieldBuilder(name);
	}
	
}