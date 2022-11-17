package org.moeaframework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that produces the concatenation of multiple iterators.
 */
public class Concaterator<T> implements Iterator<T> {
	
	private final Iterator<T>[] iterators;
	
	private int index;
	
	/**
	 * Constructs an iterator of the given iterators.
	 * 
	 * @param iterators the individual iterators
	 */
	@SafeVarargs
	public Concaterator(Iterator<T>... iterators) {
		super();
		this.iterators = iterators;
	}

	@Override
	public boolean hasNext() {
		while (index < iterators.length) {
			if (iterators[index].hasNext()) {
				return true;
			}
			
			index++;
		}
		
		return false;
	}

	@Override
	public T next() {
		if (hasNext()) {
			return iterators[index].next();
		}

		throw new NoSuchElementException();
	}
	
}