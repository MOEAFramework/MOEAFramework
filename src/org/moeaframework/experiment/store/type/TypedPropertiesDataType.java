package org.moeaframework.experiment.store.type;

import org.moeaframework.util.TypedProperties;

public class TypedPropertiesDataType extends TextDataType<TypedProperties> {

	TypedPropertiesDataType(String name) {
		super(name,
				(in) -> {
					TypedProperties properties = new TypedProperties();
					properties.load(in);
					return properties;
				},
				(out, properties) -> properties.store(out));
	}

}
