package org.moeaframework.experiment.store.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.util.TypedProperties;

public class DataType2<T> {
	
	public static final DataType2<?> SAMPLES = DataType2.of("samples");
	
	public static final TextDataType<TypedProperties> INPUTS = new TypedPropertiesDataType("inputs");
	
	public static final BinaryDataType<NondominatedPopulation> APPROXIMATION_SET = new BinaryDataType<NondominatedPopulation>(
			"approximationSet",
			(in) -> new NondominatedPopulation(Population.loadBinary(in)),
			(out, value) -> value.saveBinary(out));
		
	public static final TextDataType<TypedProperties> INDICATOR_VALUES = new TypedPropertiesDataType("indicatorValues");
	
	public static final TextDataType<TypedProperties> STATISTICS = new TypedPropertiesDataType("statistics");
	
	protected final String name;

	DataType2(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(name)
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

		DataType2 rhs = (DataType2)obj;
		return new EqualsBuilder()
				.append(name, rhs.name)
				.isEquals();
	}
	
	public static <T> DataType2<T> of(String name) {
		return new DataType2<T>(name);
	}

}
