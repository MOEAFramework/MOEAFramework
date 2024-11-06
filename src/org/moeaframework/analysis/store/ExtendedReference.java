package org.moeaframework.analysis.store;

import java.util.Set;
import java.util.TreeSet;

/**
 * Reference adding or overwriting a field in an existing reference.
 */
class ExtendedReference extends AbstractReference {
	
	private final Reference reference;
	
	private final String name;
	
	private final String value;
	
	public ExtendedReference(Reference reference, String name, String value) {
		super();
		this.reference = reference;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Set<String> fields() {
		Set<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		result.addAll(reference.fields());
		result.add(name);
		return result;
	}
	
	@Override
	public String get(String name) {
		if (this.name.equalsIgnoreCase(name)) {
			return value;
		}
		
		return reference.get(name);
	}
	
}