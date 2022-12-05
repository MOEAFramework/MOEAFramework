/* Copyright 2009-2022 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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