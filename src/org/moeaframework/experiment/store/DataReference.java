package org.moeaframework.experiment.store;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DataReference {
	
	private final Key key;
	
	private final DataType dataType;
	
	public DataReference(Key key, DataType dataType) {
		super();
		this.key = key;
		this.dataType = dataType;
	}

	public Key getKey() {
		return key;
	}

	public DataType getDataType() {
		return dataType;
	}
	
	@Override
	public String toString() {
		return "DataReference[" + key + ", " + dataType + "]";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(key)
				.append(dataType)
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

		DataReference rhs = (DataReference)obj;
		return new EqualsBuilder()
				.append(key, rhs.key)
				.append(dataType, rhs.dataType)
				.isEquals();
	}
	
	public static DataReference of(Key key, DataType dataType) {
		return new DataReference(key, dataType);
	}

}
