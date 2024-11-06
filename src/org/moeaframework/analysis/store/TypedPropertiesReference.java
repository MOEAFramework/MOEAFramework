package org.moeaframework.analysis.store;

import java.util.Collections;
import java.util.Set;

import org.moeaframework.core.TypedProperties;

/**
 * Reference constructed from the keys and values defined by a {@link TypedProperties} object.
 */
class TypedPropertiesReference extends AbstractReference {
	
	private final TypedProperties properties;
	
	public TypedPropertiesReference(TypedProperties properties) {
		super();
		this.properties = properties;
	}
	
	@Override
	public Set<String> fields() {
		return Collections.unmodifiableSet(properties.keySet());
	}
	
	@Override
	public String get(String field) {
		return properties.getString(field);
	}
	
}