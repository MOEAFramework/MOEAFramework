package org.moeaframework.analysis.store;

import java.util.stream.Collectors;

/**
 * Abstract implementation of a reference.
 */
abstract class AbstractReference implements Reference {
	
	public AbstractReference() {
		super();
	}

	@Override
	public String toString() {
		return "Reference" + fields().stream().map(x -> x + "=" + get(x)).collect(Collectors.joining(",", "(", ")"));
	}
	
}