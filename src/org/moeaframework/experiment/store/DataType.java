package org.moeaframework.experiment.store;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DataType {
	
	public static final DataType SAMPLES = DataType.of("samples");
		
	public static final DataType APPROXIMATION_SET = DataType.of("approximationSet");
	
	public static final DataType REFERENCE_SET = DataType.of("referenceSet");
	
	public static final DataType INDICATOR_VALUES = DataType.of("indicatorValues");
	
	public static final DataType DESCRIPTIVE_STATISTICS = DataType.of("descriptiveStatistics");
	
	public static final DataType STATISTICAL_COMPARISON = DataType.of("statisticalComparison");
	
	protected final String name;

	public DataType(String name) {
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

		DataType rhs = (DataType)obj;
		return new EqualsBuilder()
				.append(name, rhs.name)
				.isEquals();
	}
	
	public static DataType of(String name) {
		return new DataType(name);
	}

}
